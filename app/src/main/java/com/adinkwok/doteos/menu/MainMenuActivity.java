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

package com.adinkwok.doteos.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adinkwok.doteos.Grids;
import com.adinkwok.doteos.R;
import com.adinkwok.doteos.UserSettings;
import com.adinkwok.doteos.game.DoteosActivity;

import java.util.StringTokenizer;

public class MainMenuActivity extends Activity {
    private Context mContext;
    private View mRootView;
    private ImageView mLogo;
    private Button mPlayGameSingle;
    private Button mUnlockMoreStuff;
    private Button mCheckMeOut;
    private TextView mFastestScore;
    private TextView mFastestScore1;
    private TextView mFastestScore2;
    private TextView mFastestScore3;
    private ImageButton mSound;
    private ImageButton mVibrate;
    private ImageButton mNightMode;
    private int[] mScore1Set;
    private int[] mScore2Set;
    private int[] mScore3Set;
    private boolean mIsGraphLaunch;
    private boolean mIsSoundOn;
    private boolean mIsVibrateOn;
    private boolean mIsNightMode;
    private int mTextColor;
    private SharedPreferences mUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        mContext = this;
        enableImmersiveMode();

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mFastestScore = findViewById(R.id.fastest_score);
        mFastestScore1 = findViewById(R.id.fastest_score_1);
        mFastestScore2 = findViewById(R.id.fastest_score_2);
        mFastestScore3 = findViewById(R.id.fastest_score_3);

        mTextColor = getColor(R.color.primaryTextColor);

        UserSettings.startMenuMusic(this);

        mLogo = findViewById(R.id.logo);

        mUserSettings = this.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        mIsSoundOn = mUserSettings.getBoolean("sound_on", true);
        UserSettings.setSoundEnable(mIsSoundOn);
        mIsVibrateOn = mUserSettings.getBoolean("vibrate_on", true);
        mIsNightMode = mUserSettings.getBoolean("night_mode", false);

        mPlayGameSingle = findViewById(R.id.play_game_single);
        mPlayGameSingle.setOnClickListener(view -> {
            Intent singlePlayerGame = new Intent(mContext, DoteosActivity.class);
            singlePlayerGame.putExtra("game_mode", 1);
            UserSettings.startClickSound(mContext);
            startActivity(singlePlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        mRootView = mPlayGameSingle.getRootView();

        mUnlockMoreStuff = findViewById(R.id.unlock_more_stuff);
        mUnlockMoreStuff.setOnClickListener(view -> {
            Intent twoPlayerGame = new Intent(mContext, DoteosActivity.class);
            twoPlayerGame.putExtra("game_mode", 2);
            UserSettings.startClickSound(mContext);
            startActivity(twoPlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        mCheckMeOut = findViewById(R.id.launch_website);
        mCheckMeOut.setOnClickListener(view -> {
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
            websiteIntent.setData(Uri.parse("https://adinkwok.com"));
            UserSettings.startClickSound(mContext);
            startActivity(websiteIntent);
        });

        mFastestScore1.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore1Set);
            startActivity(dataGraph);
        });

        mFastestScore2.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore2Set);
            startActivity(dataGraph);
        });

        mFastestScore3.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore3Set);
            startActivity(dataGraph);
        });

        mSound = findViewById(R.id.sound);
        mSound.setImageResource(mIsSoundOn ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
        mSound.setOnClickListener(v -> {
            mIsSoundOn = !mIsSoundOn;
            mSound.setImageResource(mIsSoundOn ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
            if (mIsSoundOn) {
                UserSettings.setSoundEnable(mIsSoundOn);
                UserSettings.startClickSound(mContext);
                UserSettings.startMenuMusic(mContext);
            } else {
                UserSettings.pauseMenuMusic();
                UserSettings.setSoundEnable(mIsSoundOn);
            }
            SharedPreferences.Editor editor = mUserSettings.edit();
            editor.putBoolean("sound_on", mIsSoundOn);
            editor.apply();
        });

        mVibrate = findViewById(R.id.vibrate);
        mVibrate.setImageResource(mIsVibrateOn ? R.drawable.ic_vibrate_on : R.drawable.ic_vibrate_off);
        mVibrate.setOnClickListener(v -> {
            mIsVibrateOn = !mIsVibrateOn;
            UserSettings.startClickSound(mContext);
            mVibrate.setImageResource(mIsVibrateOn ? R.drawable.ic_vibrate_on : R.drawable.ic_vibrate_off);
            if (mIsVibrateOn) {
                assert vb != null;
                vb.vibrate(15);
            }
            SharedPreferences.Editor editor = mUserSettings.edit();
            editor.putBoolean("vibrate_on", mIsVibrateOn);
            editor.apply();
        });

        mNightMode = findViewById(R.id.night_mode);
        mNightMode.setOnClickListener(v -> {
            mIsNightMode = !mIsNightMode;
            UserSettings.startClickSound(mContext);
            SharedPreferences.Editor editor = mUserSettings.edit();
            editor.putBoolean("night_mode", mIsNightMode);
            editor.apply();
            updateColors();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsGraphLaunch) {
            UserSettings.pauseMenuMusic();
        }
        mIsGraphLaunch = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScores();
        updateColors();
        UserSettings.startMenuMusic(mContext);
    }

    private void refreshScores() {
        SharedPreferences savedScores = this.getSharedPreferences("saved_scores", Context.MODE_PRIVATE);
        double score1 = savedScores.getInt("score1", 0) / 1000.0;
        if (score1 == 0) {
            mFastestScore.setVisibility(View.GONE);
            mFastestScore1.setVisibility(View.GONE);
            mFastestScore2.setVisibility(View.GONE);
            mFastestScore3.setVisibility(View.GONE);
        } else {
            double rate1 = Grids.singleGridSize / score1;
            mFastestScore.setVisibility(View.VISIBLE);
            mFastestScore1.setVisibility(View.VISIBLE);
            String score1Text = "1. " + String.format(getString(R.string.fastest_score_show), score1, rate1);
            mFastestScore1.setText(score1Text);
            mScore1Set = convertStringToIntArray(savedScores.getString("score1set", ""));

            double score2 = savedScores.getInt("score2", 0) / 1000.0;
            if (score2 == 0) {
                mFastestScore2.setVisibility(View.GONE);
                mFastestScore3.setVisibility(View.GONE);
                mFastestScore.setText(R.string.fastest_score_text_single);
            } else {
                double rate2 = Grids.singleGridSize / score2;
                mFastestScore2.setVisibility(View.VISIBLE);
                mFastestScore.setText(R.string.fastest_score_text);
                String score2Text = "2. " + String.format(getString(R.string.fastest_score_show), score2, rate2);
                mFastestScore2.setText(score2Text);
                mScore2Set = convertStringToIntArray(savedScores.getString("score2set", ""));

                double score3 = savedScores.getInt("score3", 0) / 1000.0;
                if (score3 == 0) {
                    mFastestScore3.setVisibility(View.GONE);
                } else {
                    double rate3 = Grids.singleGridSize / score3;
                    mFastestScore3.setVisibility(View.VISIBLE);
                    String score3Text = "3. " + String.format(getString(R.string.fastest_score_show), score3, rate3);
                    mFastestScore3.setText(score3Text);
                    mScore3Set = convertStringToIntArray(savedScores.getString("score3set", ""));
                }
            }
        }
    }

    private int[] convertStringToIntArray(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        int[] intArray = new int[Grids.singleGridSize];
        for (int i = 0; i < Grids.singleGridSize; i++) {
            intArray[i] = Integer.parseInt(st.nextToken());
        }
        return intArray;
    }

    private void updateColors() {
        Drawable[] layers = new Drawable[2];
        layers[0] = ContextCompat.getDrawable(this, R.drawable.logo_dot);
        layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text);
        if (mIsNightMode) {
            layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text_white);
            mPlayGameSingle.setTextColor(Color.WHITE);
            mUnlockMoreStuff.setTextColor(Color.WHITE);
            mCheckMeOut.setTextColor(Color.WHITE);
            mFastestScore.setTextColor(Color.WHITE);
            mFastestScore1.setTextColor(Color.WHITE);
            mFastestScore2.setTextColor(Color.WHITE);
            mFastestScore3.setTextColor(Color.WHITE);
            mSound.setColorFilter(Color.WHITE);
            mVibrate.setColorFilter(Color.WHITE);
            mNightMode.setColorFilter(Color.WHITE);
            mRootView.setBackgroundColor(Color.BLACK);
        } else {
            layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text);
            mPlayGameSingle.setTextColor(Color.BLACK);
            mUnlockMoreStuff.setTextColor(Color.BLACK);
            mCheckMeOut.setTextColor(Color.BLACK);
            mFastestScore.setTextColor(mTextColor);
            mFastestScore1.setTextColor(mTextColor);
            mFastestScore2.setTextColor(mTextColor);
            mFastestScore3.setTextColor(mTextColor);
            mSound.setColorFilter(Color.BLACK);
            mVibrate.setColorFilter(Color.BLACK);
            mNightMode.setColorFilter(Color.BLACK);
            mRootView.setBackgroundColor(Color.WHITE);
        }
        mLogo.setImageDrawable(new LayerDrawable(layers));
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
