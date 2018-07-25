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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adinkwok.doteos.R;
import com.adinkwok.doteos.UserSettings;
import com.adinkwok.doteos.game.DoteosActivity;

import java.util.Arrays;

public class DoubleGraphActivity extends Activity {
    private Context mContext;
    private boolean mToMenu;

    public DoubleGraphActivity() {
        // empty
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_graph_activity);
        mContext = this;
        enableImmersiveMode();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        TextView msgTop = findViewById(R.id.double_graph_message_top);
        TextView msgBot = findViewById(R.id.double_graph_message_bottom);
        LinearLayout layoutTop = findViewById(R.id.double_graph_layout_top);
        LinearLayout layoutBot = findViewById(R.id.double_graph_layout_bottom);
        ImageView imageTop = findViewById(R.id.double_graph_image_top);
        ImageView imageBot = findViewById(R.id.double_graph_image_bottom);
        TextView timeTop = findViewById(R.id.double_graph_time_top);
        TextView timeBot = findViewById(R.id.double_graph_time_bottom);
        TextView avgTop = findViewById(R.id.double_graph_avg_top);
        TextView avgBot = findViewById(R.id.double_graph_avg_bottom);
        TextView fastTop = findViewById(R.id.double_graph_fast_top);
        TextView fastBot = findViewById(R.id.double_graph_fast_bottom);
        TextView slowTop = findViewById(R.id.double_graph_slow_top);
        TextView slowBot = findViewById(R.id.double_graph_slow_bottom);
        Button playGameTop = findViewById(R.id.double_graph_play_again_top);
        Button playGameBot = findViewById(R.id.double_graph_play_again_bottom);
        Button backMenuTop = findViewById(R.id.double_graph_back_to_menu_top);
        Button backMenuBot = findViewById(R.id.double_graph_back_to_menu_bottom);
        View rootView = slowBot.getRootView();

        int[] data = getIntent().getIntArrayExtra("data");
        int[] playerTop = Arrays.copyOfRange(data, data.length / 2, data.length);
        int[] playerBot = Arrays.copyOfRange(data, 0, data.length / 2);

        double finalTimeTop = playerTop[playerTop.length - 1] / 1000.0;
        timeTop.setText(String.format(getString(R.string.graph_total_time), finalTimeTop));
        avgTop.setText(String.format(getString(R.string.graph_average_rate),
                playerTop.length / finalTimeTop));
        fastTop.setText(String.format(getString(R.string.graph_double_fastest),
                getFastestTime(playerTop) / 1000.0));
        slowTop.setText(String.format(getString(R.string.graph_double_slowest),
                getSlowestTime(playerTop) / 1000.0));

        double finalTimeBot = playerBot[playerBot.length - 1] / 1000.0;
        timeBot.setText(String.format(getString(R.string.graph_total_time), finalTimeBot));
        avgBot.setText(String.format(getString(R.string.graph_average_rate),
                playerBot.length / finalTimeBot));
        fastBot.setText(String.format(getString(R.string.graph_double_fastest),
                getFastestTime(playerBot) / 1000.0));
        slowBot.setText(String.format(getString(R.string.graph_double_slowest),
                getSlowestTime(playerBot) / 1000.0));

        // Show the winner/loser message, hide the stats
        msgTop.setVisibility(View.VISIBLE);
        msgBot.setVisibility(View.VISIBLE);
        layoutTop.setVisibility(View.GONE);
        layoutBot.setVisibility(View.GONE);

        if (finalTimeTop < finalTimeBot) {
            imageTop.setImageResource(R.drawable.big_win);
            imageBot.setImageResource(R.drawable.loser);
            msgTop.setText(R.string.end_message_winner);
            msgBot.setText(R.string.end_message_loser);
        } else if (finalTimeTop > finalTimeBot) {
            imageTop.setImageResource(R.drawable.loser);
            imageBot.setImageResource(R.drawable.big_win);
            msgTop.setText(R.string.end_message_loser);
            msgBot.setText(R.string.end_message_winner);
        } else {
            imageTop.setVisibility(View.GONE);
            imageBot.setVisibility(View.GONE);
            msgTop.setText(R.string.end_message_tie);
            msgBot.setText(R.string.end_message_tie);
        }

        SharedPreferences userSettings = mContext.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        boolean isNightMode = userSettings.getBoolean("night_mode", false);

        if (isNightMode) {
            msgTop.setTextColor(Color.WHITE);
            msgBot.setTextColor(Color.WHITE);
            imageTop.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
            imageBot.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
            timeTop.setTextColor(Color.WHITE);
            timeBot.setTextColor(Color.WHITE);
            avgTop.setTextColor(Color.WHITE);
            avgBot.setTextColor(Color.WHITE);
            fastTop.setTextColor(Color.WHITE);
            fastBot.setTextColor(Color.WHITE);
            slowTop.setTextColor(Color.WHITE);
            slowBot.setTextColor(Color.WHITE);
            playGameTop.setTextColor(Color.WHITE);
            backMenuTop.setTextColor(Color.WHITE);
            playGameBot.setTextColor(Color.WHITE);
            backMenuBot.setTextColor(Color.WHITE);
            rootView.setBackgroundColor(Color.BLACK);
        } else {
            rootView.setBackgroundColor(Color.WHITE);
        }

        playGameTop.setOnClickListener(view -> {
            Intent doublePlayerGame = new Intent(mContext, DoteosActivity.class);
            doublePlayerGame.putExtra("game_mode", 2);
            UserSettings.startClickSound(mContext);
            startActivity(doublePlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        playGameBot.setOnClickListener(view -> {
            Intent doublePlayerGame = new Intent(mContext, DoteosActivity.class);
            doublePlayerGame.putExtra("game_mode", 2);
            UserSettings.startClickSound(mContext);
            startActivity(doublePlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        backMenuTop.setOnClickListener(view -> {
            mToMenu = true;
            exit();
        });

        backMenuBot.setOnClickListener(view -> {
            mToMenu = true;
            exit();
        });

        Animation animationFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        Animation animationFadeOutTop = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        Animation animationFadeOutBot = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);

        animationFadeOutTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                msgTop.setVisibility(View.GONE);
                msgBot.setVisibility(View.GONE);
                layoutTop.setVisibility(View.VISIBLE);
                layoutBot.setVisibility(View.VISIBLE);
                layoutTop.setAnimation(animationFadeIn);
                layoutBot.setAnimation(animationFadeIn);
                UserSettings.startMenuMusic(mContext);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        new Handler().postDelayed(() -> {
            msgTop.startAnimation(animationFadeOutTop);
            msgBot.startAnimation(animationFadeOutBot);
        }, 2250);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    private int getSlowestTime(int[] array) {
        int tempSlowest = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] - array[i - 1] > tempSlowest) {
                tempSlowest = array[i] - array[i - 1];
            }
        }
        return tempSlowest;
    }

    private int getFastestTime(int[] array) {
        int tempSlowest = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] - array[i - 1] < tempSlowest) {
                tempSlowest = array[i] - array[i - 1];
            }
        }
        return tempSlowest;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mToMenu) {
            UserSettings.pauseMenuMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserSettings.startMenuMusic(mContext);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    private void exit() {
        UserSettings.startClickSound(mContext);
        finish();
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
