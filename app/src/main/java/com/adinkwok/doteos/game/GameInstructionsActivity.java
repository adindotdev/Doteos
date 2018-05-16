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

package com.adinkwok.doteos.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adinkwok.doteos.R;
import com.adinkwok.doteos.UserSettings;

public class GameInstructionsActivity extends Activity {
    private Context mContext;
    private boolean mBackButtonPressed;
    private boolean mPaused;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("COUNTDOWN", "COUNTDOWN CREATED, YEAH!");
        mContext = this;
        enableImmersiveMode();

        mIntent = new Intent();

        SharedPreferences userSettings = mContext.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        boolean isNightMode = userSettings.getBoolean("night_mode", false);

        int gameMode = getIntent().getIntExtra("game_mode", 1);
        if (gameMode == 1) {
            setContentView(R.layout.game_instructions_single_activity);
            ImageView number = findViewById(R.id.game_instructions_number);
            ImageView dot1 = findViewById(R.id.game_instructions_dot_1);
            ImageView dot2 = findViewById(R.id.game_instructions_dot_2);
            ImageView dot3 = findViewById(R.id.game_instructions_dot_3);
            TextView text = findViewById(R.id.game_instructions_text);
            View rootView = text.getRootView();

            if (isNightMode) {
                number.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
                text.setTextColor(Color.WHITE);
                rootView.setBackgroundColor(Color.BLACK);
            } else {
                rootView.setBackgroundColor(Color.WHITE);
            }

            new Handler().postDelayed(() -> {
                if (!mBackButtonPressed) {
                    playClickSound();
                    number.setImageResource(R.drawable.ic_3);
                    dot1.setImageResource(R.drawable.ic_dot_sprite_blue);
                    new Handler().postDelayed(() -> {
                        if (!mBackButtonPressed) {
                            playClickSound();
                            number.setImageResource(R.drawable.ic_2);
                            dot1.setImageResource(R.drawable.ic_dot_sprite_green);
                            dot2.setImageResource(R.drawable.ic_dot_sprite_blue);
                            new Handler().postDelayed(() -> {
                                if (!mBackButtonPressed) {
                                    playClickSound();
                                    number.setImageResource(R.drawable.ic_1);
                                    dot2.setImageResource(R.drawable.ic_dot_sprite_green);
                                    dot3.setImageResource(R.drawable.ic_dot_sprite_blue);
                                    new Handler().postDelayed(() -> {
                                        if (!mBackButtonPressed) {
                                            playClickSound();
                                            dot3.setImageResource(R.drawable.ic_dot_sprite_green);
                                            Log.d("COUNTDOWN", "COUNTDOWN WENT TO THE END, YEAH!");
                                            setResult(RESULT_OK, mIntent);
                                            finish();
                                        }
                                    }, 1000);
                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }, 1250);
        } else {
            setContentView(R.layout.game_instructions_double_activity);
            ImageView number1 = findViewById(R.id.game_instructions_number_1);
            ImageView number2 = findViewById(R.id.game_instructions_number_2);
            ImageView dot1 = findViewById(R.id.game_instructions_dot_1_double);
            ImageView dot2 = findViewById(R.id.game_instructions_dot_2_double);
            ImageView dot3 = findViewById(R.id.game_instructions_dot_3_double);
            ImageView dot4 = findViewById(R.id.game_instructions_dot_4_double);
            TextView text1 = findViewById(R.id.game_instructions_text_1_double);
            TextView text2 = findViewById(R.id.game_instructions_text_2_double);
            View thisView = text1.getRootView();

            if (isNightMode) {
                number1.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
                number2.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
                text1.setTextColor(Color.WHITE);
                text2.setTextColor(Color.WHITE);
                thisView.setBackgroundColor(Color.BLACK);
            } else {
                thisView.setBackgroundColor(Color.WHITE);
            }

            new Handler().postDelayed(() -> {
                if (!mBackButtonPressed) {
                    playClickSound();
                    number1.setVisibility(View.VISIBLE);
                    number2.setVisibility(View.VISIBLE);
                    dot1.setImageResource(R.drawable.ic_dot_sprite_blue);
                    new Handler().postDelayed(() -> {
                        if (!mBackButtonPressed) {
                            playClickSound();
                            number1.setImageResource(R.drawable.ic_3);
                            number2.setImageResource(R.drawable.ic_3);
                            dot1.setImageResource(R.drawable.ic_dot_sprite_green);
                            dot2.setImageResource(R.drawable.ic_dot_sprite_blue);
                            new Handler().postDelayed(() -> {
                                if (!mBackButtonPressed) {
                                    playClickSound();
                                    number1.setImageResource(R.drawable.ic_2);
                                    number2.setImageResource(R.drawable.ic_2);
                                    dot2.setImageResource(R.drawable.ic_dot_sprite_green);
                                    dot3.setImageResource(R.drawable.ic_dot_sprite_blue);
                                    new Handler().postDelayed(() -> {
                                        if (!mBackButtonPressed) {
                                            playClickSound();
                                            number1.setImageResource(R.drawable.ic_1);
                                            number2.setImageResource(R.drawable.ic_1);
                                            dot3.setImageResource(R.drawable.ic_dot_sprite_green);
                                            dot4.setImageResource(R.drawable.ic_dot_sprite_blue);
                                            new Handler().postDelayed(() -> {
                                                if (!mBackButtonPressed) {
                                                    playClickSound();
                                                    dot4.setImageResource(R.drawable.ic_dot_sprite_green);
                                                    Log.d("COUNTDOWN", "COUNTDOWN WENT TO THE END, YEAH!");
                                                    setResult(RESULT_OK, mIntent);
                                                    finish();
                                                }
                                            }, 1000);
                                        }
                                    }, 1000);
                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }, 1000);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("COUNTDOWN", "COUNTDOWN WENT FOR A PAUSE, YEAH!");
        mPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("COUNTDOWN", "COUNTDOWN DESTROYED, YEAH!");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("COUNTDOWN", "BACK BUTTON PRESSED IN COUNTDOWN, YEAH!");
        UserSettings.startClickSound(mContext);
        mBackButtonPressed = true;
        setResult(RESULT_CANCELED, mIntent);
        finishActivity(-1);
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

    private void playClickSound() {
        if (!mPaused) {
            UserSettings.startClickSound(mContext);
        }
    }
}
