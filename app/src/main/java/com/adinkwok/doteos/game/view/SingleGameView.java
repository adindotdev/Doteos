/*
 * Copyright (C) 2018 Adin Kwok (adinkwok)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adinkwok.doteos.game.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Chronometer;

import com.adinkwok.doteos.Grids;
import com.adinkwok.doteos.R;
import com.adinkwok.doteos.UserSettings;
import com.adinkwok.doteos.game.DoteosActivity;
import com.adinkwok.doteos.menu.SingleGraphActivity;

import java.util.Objects;

public class SingleGameView extends SurfaceView implements SurfaceHolder.Callback {
    private final Context mContext;
    private final Chronometer mStopwatch;

    private final int mScreenHeight;
    private final int mScreenWidth;

    private final Vibrator mVibrator;
    private final Drawable mGreyDot;
    private final Drawable mBlueDot;
    private final Drawable mGreenDot;
    private final Drawable mRedDot;
    private final Drawable mRestartButton;
    
    /**
     * Holds data of all dots on the grid.
     * Dot data array holds the following:
     * 0 - Color
     * 1 - Number in grid
     * 2 - x-coordinate
     * 3 - y-coordinate
     * 4 - Time the dot is tapped
     */
    private int[][] mActiveDots;
    private int[] mFinalDotTimes;

    private int mDotPressBuffer;
    private int mDotSize;

    /**
     * Time counter, shown above the grid
     */
    private Paint mTimePaint;
    private boolean mIsVibrate;
    private int mCanvasColor;

    /**
     * The next dot to be tapped.
     */
    private int mDotIndex;

    /**
     * Use this to handle showing blank field before game
     */
    private boolean mGameOn;

    /**
     * Use this to prevent further tap input
     */
    private boolean mGameOver;

    private String mTimeCount;
    private int mTimeY;
    private int mTimeX;

    public SingleGameView(Context context) {
        this(context, 0, 0);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public SingleGameView(Context context, int screenHeight, int screenWidth) {
        super(context);
        Log.d("GAME VIEW", "GAME VIEW INITIATED, YEAH!");
        getHolder().addCallback(this);
        mContext = context;
        mStopwatch = new Chronometer(mContext);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        mScreenHeight = screenHeight;
        mScreenWidth = screenWidth;

        mGreyDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_grey);
        mBlueDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_blue);
        mGreenDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_green);
        mRedDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_red);
        mRestartButton = AppCompatResources.getDrawable(mContext, R.drawable.ic_restart);

        setupGame();
    }

    private void setupGame() {
        final int spaceConstant = mScreenWidth / 8;
        final int startingY = (mScreenHeight / 2) - (mScreenWidth / 4);
        final int endingY = (mScreenHeight / 2) + (mScreenWidth / 4);
        final int startingX = (mScreenWidth / 2) - (mScreenWidth / 4);
        final int endingX = (mScreenWidth / 2) + (mScreenWidth / 4);

        Grids.createPattern(1);
        mActiveDots = new int[Grids.generatedPattern.length][5];
        mFinalDotTimes = new int[Grids.generatedPattern.length];
        // On 1080px screen, dots ≈ 50px
        mDotSize = mScreenWidth / 22;
        // On 1080p screen, buffer = 20px
        mDotPressBuffer = mDotSize + (mScreenWidth / 45);
        mDotIndex = 0;

        int index = 0;
        for (int y = startingY; y <= endingY; y += spaceConstant) {
            for (int x = startingX; x <= endingX; x += spaceConstant) {
                if (Grids.generatedPattern[index] == 1) {
                    mActiveDots[Grids.generatedPattern[index] - 1]
                            = new int[]{1, (int) Grids.generatedPattern[index], x, y, 0};
                } else {
                    mActiveDots[Grids.generatedPattern[index] - 1]
                            = new int[]{0, (int) Grids.generatedPattern[index], x, y, 0};
                }
                index++;
            }
        }

        mTimePaint = new Paint();
        mTimePaint.setTextSize(mDotSize * 2);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        mTimeY = startingY - spaceConstant;
        mTimeX = mScreenWidth / 2;

        final SharedPreferences userSettings =
                mContext.getSharedPreferences("user_settings", Context.MODE_PRIVATE);

        mIsVibrate = userSettings.getBoolean("vibrate_on", true);
        final boolean isNightMode = userSettings.getBoolean("night_mode", false);
        mCanvasColor = isNightMode ? Color.BLACK : Color.WHITE;
        mTimePaint.setColor(isNightMode ? Color.WHITE : Color.BLACK);
        if (isNightMode) {
            assert mRestartButton != null;
            mRestartButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        Objects.requireNonNull(mRestartButton).setBounds(mScreenWidth - (mDotSize * 2),
                mScreenHeight - (mDotSize * 2),
                mScreenWidth,
                mScreenHeight);
    }

    private void endGame() {
        mGameOver = true;
        mStopwatch.stop();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < mActiveDots.length; i++) {
            mFinalDotTimes[i] = mActiveDots[i][4];
            str.append(mActiveDots[i][4]).append(",");
        }
        UserSettings.setNewHighScore(mContext, mDotIndex, mActiveDots, str);
        new Handler().postDelayed(() -> cleanFinish(2), 500);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int x = (int) event.getX(actionIndex);
        int y = (int) event.getY(actionIndex);

        if (mGameOn && !mGameOver && (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_DOWN)) {
            if (x >= (mActiveDots[mDotIndex][2] - mDotPressBuffer) &&
                    x <= (mActiveDots[mDotIndex][2] + mDotPressBuffer) &&
                    y >= (mActiveDots[mDotIndex][3] - mDotPressBuffer) &&
                    y <= (mActiveDots[mDotIndex][3] + mDotPressBuffer))
                pressedDot();
            else if (x >= (mScreenWidth - (mDotSize * 2)) &&
                    x <= mScreenWidth &&
                    y >= (mScreenHeight - (mDotSize * 2)) &&
                    y <= mScreenHeight)
                pressedRestart();
        }
        return true;
    }

    private void pressedDot() {
        UserSettings.startClickSound(mContext);
        assert mVibrator != null;
        if (mIsVibrate)
            mVibrator.vibrate(15);
        mActiveDots[mDotIndex][0] = 2;
        mActiveDots[mDotIndex][4] = (int) (SystemClock.elapsedRealtime() - mStopwatch.getBase());
        if (mDotIndex < mActiveDots.length - 1) {
            mDotIndex++;
            mActiveDots[mDotIndex][0] = 1;
        } else {
            endGame();

        }
    }

    private void pressedRestart() {
        UserSettings.startClickSound(mContext);
        cleanFinish(1);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(mCanvasColor);
            canvas.drawText(mTimeCount, mTimeX, mTimeY, mTimePaint);
            if (mGameOn) {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(activeDot[0],
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
                mRestartButton.draw(canvas);

            } else {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(0,
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
            }
        }
    }

    public void update() {
        if (mGameOn) {
            if (!mGameOver)
                mTimeCount = Double.toString((double)
                        (SystemClock.elapsedRealtime() - mStopwatch.getBase()) / 1000.0);
            else
                mTimeCount = Double.toString(mActiveDots[mDotIndex][4] / 1000.0);
        } else {
            mTimeCount = getResources().getString(R.string.game_waiting);
        }
    }

    private void cleanFinish(int next) {
        Intent intent;
        switch (next) {
            case 1:
                Log.d("GAME VIEW", "PLAYING AGAIN, YEAH!");
                intent = new Intent(mContext, DoteosActivity.class);
                intent.putExtra("game_mode", 1);
                mContext.startActivity(intent);
                break;
            case 2:
                Log.d("GAME VIEW", "VIEWING STATS, YEAH!");
                intent = new Intent(mContext, SingleGraphActivity.class);
                intent.putExtra("data", mFinalDotTimes);
                intent.putExtra("from_game", true);
                mContext.startActivity(intent);
                break;
            default:
                Log.d("GAME VIEW", "GOING BACK TO MENU, YEAH!");
                break;
        }
        DoteosActivity.closeMe(mContext);
        Log.d("GAME VIEW", "CLEAN FINISHED, YEAH!!");
    }

    private Drawable getDotSprite(int color, int x, int y) {
        Drawable dot;
        switch (color) {
            case 0:
                dot = mGreyDot;
                break;
            case 1:
                dot = mBlueDot;
                break;
            case 2:
                dot = mGreenDot;
                break;
            default:
                dot = mRedDot;
                break;
        }
        Objects.requireNonNull(dot).setBounds(x - mDotSize,
                y - mDotSize,
                x + mDotSize,
                y + mDotSize);
        return dot;
    }

    public void startTimer() {
        mStopwatch.setBase(SystemClock.elapsedRealtime());
        mStopwatch.start();
    }

    public void setGameOn(boolean gameOn) {
        mGameOn = gameOn;
    }
}
