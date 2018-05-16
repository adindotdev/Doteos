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
import com.adinkwok.doteos.menu.DoubleGraphActivity;

import java.util.Objects;

public class DoubleGameView extends SurfaceView implements SurfaceHolder.Callback {
    private final Context mContext;
    private final Chronometer mStopwatch;
    private final Vibrator mVibrator;

    private final int mScreenHeight;
    private final int mScreenWidth;

    private final Drawable mGreyDot;
    private final Drawable mBlueDot;
    private final Drawable mGreenDot;
    private final Drawable mRedDot;
    private final Drawable mRestartButtonTop;
    private final Drawable mRestartButtonBot;

    /**
     * Holds data for all dots on the grid.
     * Dot data array holds the following:
     * 0 - Color
     * 1 - Number in grid
     * 2 - x-coordinate
     * 3 - y-coordinate
     * 4 - Time the dot is tapped
     */
    private int[][] mActiveDots;
    private int[] mFinalDotTimes;

    private int mDotSize;
    private int mDotPressBuffer;

    private int mCanvasColor;
    private boolean mIsVibrate;

    /**
     * The next dots to be tapped.
     */
    private int mDotIndexPlayerOne;
    private int mDotIndexPlayerTwo;

    /**
     * Use this to handle showing blank field before game
     */
    private boolean mGameOn;
    private boolean mPlayerOneDone;
    private boolean mPlayerTwoDone;

    public DoubleGameView(Context context) {
        this(context, 0, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleGameView(Context context, int screenHeight, int screenWidth) {
        super(context);
        getHolder().addCallback(this);
        Log.d("GAME VIEW", "GAME VIEW INITIATED, YEAH!");
        mContext = context;
        mStopwatch = new Chronometer(mContext);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        mScreenHeight = screenHeight;
        mScreenWidth = screenWidth;

        mGreyDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_grey);
        mBlueDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_blue);
        mGreenDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_green);
        mRedDot = AppCompatResources.getDrawable(mContext, R.drawable.ic_dot_sprite_red);
        mRestartButtonTop = AppCompatResources.getDrawable(mContext, R.drawable.ic_restart);
        mRestartButtonBot = AppCompatResources.getDrawable(mContext, R.drawable.ic_restart_flip);

        setupGame();
    }

    private void setupGame() {
        final int spaceConstant = mScreenWidth / 8;
        final int playerOneMidpoint = (mScreenHeight / 2) + (mScreenHeight / 4);
        int startingYBot = playerOneMidpoint - spaceConstant;
        int endingY = playerOneMidpoint + spaceConstant;
        int startingX = (mScreenWidth / 2) - spaceConstant;
        int endingX = (mScreenWidth / 2) + spaceConstant;
        final SharedPreferences userSettings
                = mContext.getSharedPreferences("user_settings", Context.MODE_PRIVATE);

        Grids.createPattern(2);
        mActiveDots = new int[Grids.generatedPattern.length * 2][5];
        mFinalDotTimes = new int[mActiveDots.length];
        // On 1080px screen, dots ≈ 50px
        mDotSize = mScreenWidth / 22;
        // On 1080p screen, buffer = 20px
        mDotPressBuffer = mDotSize + (mScreenWidth / 45);
        mDotIndexPlayerOne = 0;
        mDotIndexPlayerTwo = mActiveDots.length / 2;

        int index = 0;
        for (int y = startingYBot; y <= endingY; y += spaceConstant) {
            for (int x = startingX; x <= endingX; x += spaceConstant) {
                mActiveDots[Grids.generatedPattern[index] - 1]
                        = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                        Grids.generatedPattern[index], x, y, 0};
                index++;
            }
        }
        mActiveDots[Grids.generatedPattern[index] - 1]
                = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                Grids.generatedPattern[index],
                startingX - spaceConstant, playerOneMidpoint, 0};
        index++;
        mActiveDots[Grids.generatedPattern[index] - 1]
                = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                Grids.generatedPattern[index],
                endingX + spaceConstant, playerOneMidpoint, 0};

        final int playerTwoMidpoint = (mScreenHeight / 4);
        int startingYTop = playerTwoMidpoint + spaceConstant;
        endingY = playerTwoMidpoint - spaceConstant;
        startingX = (mScreenWidth / 2) + spaceConstant;
        endingX = (mScreenWidth / 2) - spaceConstant;

        index = 0;
        for (int y = startingYTop; y >= endingY; y -= spaceConstant) {
            for (int x = startingX; x >= endingX; x -= spaceConstant) {
                mActiveDots[Grids.generatedPattern.length + Grids.generatedPattern[index] - 1]
                        = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                        Grids.generatedPattern[index], x, y, 0};
                index++;
            }
        }
        mActiveDots[Grids.generatedPattern.length + Grids.generatedPattern[index] - 1]
                = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                Grids.generatedPattern[index],
                startingX + spaceConstant, playerTwoMidpoint, 0};
        index++;
        mActiveDots[Grids.generatedPattern.length + Grids.generatedPattern[index] - 1]
                = new int[]{(Grids.generatedPattern[index] == 1) ? 1 : 0,
                Grids.generatedPattern[index],
                endingX - spaceConstant, playerTwoMidpoint, 0};

        mIsVibrate = userSettings.getBoolean("vibrate_on", true);
        boolean isNightMode = userSettings.getBoolean("night_mode", false);
        mCanvasColor = isNightMode ? Color.BLACK : Color.WHITE;
        if (isNightMode) {
            assert mRestartButtonTop != null;
            mRestartButtonTop.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            assert mRestartButtonBot != null;
            mRestartButtonBot.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        Objects.requireNonNull(mRestartButtonTop).setBounds(mScreenWidth - (mDotSize * 2),
                mScreenHeight - (mDotSize * 2),
                mScreenWidth,
                mScreenHeight);
        Objects.requireNonNull(mRestartButtonBot).setBounds(0,
                0,
                (mDotSize * 2),
                (mDotSize * 2));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int x = (int) event.getX(actionIndex);
        int y = (int) event.getY(actionIndex);

        if (mGameOn && !(mPlayerOneDone && mPlayerTwoDone)
                && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)) {
            if (x >= (mActiveDots[mDotIndexPlayerOne][2] - mDotPressBuffer) &&
                    x <= (mActiveDots[mDotIndexPlayerOne][2] + mDotPressBuffer) &&
                    y >= (mActiveDots[mDotIndexPlayerOne][3] - mDotPressBuffer) &&
                    y <= (mActiveDots[mDotIndexPlayerOne][3] + mDotPressBuffer))
                playerOnePressedDot();
            else if (x >= (mActiveDots[mDotIndexPlayerTwo][2] - mDotPressBuffer) &&
                    x <= (mActiveDots[mDotIndexPlayerTwo][2] + mDotPressBuffer) &&
                    y >= (mActiveDots[mDotIndexPlayerTwo][3] - mDotPressBuffer) &&
                    y <= (mActiveDots[mDotIndexPlayerTwo][3] + mDotPressBuffer))
                playerTwoPressedDot();
            else if ((x >= (mScreenWidth - (mDotSize * 2)) &&
                    x <= mScreenWidth &&
                    y >= (mScreenHeight - (mDotSize * 2)) &&
                    y <= mScreenHeight)
                    || (x <= (mDotSize * 2) && y <= (mDotSize * 2))) {
                UserSettings.startClickSound(mContext);
                cleanFinish(1);
            }
        }
        return true;
    }

    private void playerOnePressedDot() {
        UserSettings.startClickSound(mContext);
        assert mVibrator != null;
        if (mIsVibrate)
            mVibrator.vibrate(15);
        mActiveDots[mDotIndexPlayerOne][0] = 2;
        mActiveDots[mDotIndexPlayerOne][4] =
                (int) (SystemClock.elapsedRealtime() - mStopwatch.getBase());
        if (mDotIndexPlayerOne < (mActiveDots.length / 2) - 1) {
            mDotIndexPlayerOne++;
            mActiveDots[mDotIndexPlayerOne][0] = 1;
        } else {
            mPlayerOneDone = true;
            mStopwatch.stop();
            if (mPlayerTwoDone) {
                mStopwatch.stop();
                new Handler().postDelayed(() -> cleanFinish(2), 500);
            }
        }
    }

    private void playerTwoPressedDot() {
        UserSettings.startClickSound(mContext);
        assert mVibrator != null;
        if (mIsVibrate)
            mVibrator.vibrate(15);
        mActiveDots[mDotIndexPlayerTwo][0] = 2;
        mActiveDots[mDotIndexPlayerTwo][4]
                = (int) (SystemClock.elapsedRealtime() - mStopwatch.getBase());
        if (mDotIndexPlayerTwo < mActiveDots.length - 1) {
            mDotIndexPlayerTwo++;
            mActiveDots[mDotIndexPlayerTwo][0] = 1;
        } else {
            mPlayerTwoDone = true;
            if (mPlayerOneDone) {
                mStopwatch.stop();
                new Handler().postDelayed(() -> cleanFinish(2), 500);
            }
        }
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
            if (mGameOn) {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(activeDot[0],
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
                mRestartButtonTop.draw(canvas);
                mRestartButtonBot.draw(canvas);
            } else {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(0,
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
            }
        }
    }

    private void cleanFinish(int next) {
        Intent intent;
        switch (next) {
            case 1:
                Log.d("GAME VIEW", "PLAYING AGAIN, YEAH!");
                intent = new Intent(mContext, DoteosActivity.class);
                intent.putExtra("game_mode", 2);
                mContext.startActivity(intent);
                break;
            case 2:
                Log.d("GAME VIEW", "VIEWING STATS, YEAH!");
                for (int i = 0; i < mActiveDots.length; i++) {
                    mFinalDotTimes[i] = mActiveDots[i][4];
                }
                intent = new Intent(mContext, DoubleGraphActivity.class);
                intent.putExtra("data", mFinalDotTimes);
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
