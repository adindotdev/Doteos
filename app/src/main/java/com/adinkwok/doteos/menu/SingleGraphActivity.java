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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adinkwok.doteos.Grids;
import com.adinkwok.doteos.R;
import com.adinkwok.doteos.UserSettings;
import com.adinkwok.doteos.game.DoteosActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SingleGraphActivity extends Activity {
    private Context mContext;
    private boolean mFromGame;
    private boolean mToMenu;

    public SingleGraphActivity() {
        // empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_graph_activity);
        mContext = this;
        enableImmersiveMode();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        TextView messageView = findViewById(R.id.graph_message);
        LinearLayout graphLayout = findViewById(R.id.graph_menu);
        ImageView stats = findViewById(R.id.stats_image);
        View rootView = stats.getRootView();
        TextView totalTime = findViewById(R.id.total_time);
        TextView avgRate = findViewById(R.id.avg_rate);
        GraphView graph = findViewById(R.id.graph);
        TextView horizontalTitle = findViewById(R.id.graph_horizontal_title);
        Button playGame = findViewById(R.id.play_game_graph);
        Button backMenu = findViewById(R.id.back_to_menu);

        int[] data = getIntent().getIntArrayExtra("data");
        mFromGame = getIntent().getBooleanExtra("from_game", false);

        double finalTime = data[Grids.singleGridSize - 1] / 1000.0;
        totalTime.setText(String.format(getString(R.string.graph_total_time), finalTime));
        avgRate.setText(String.format(getString(R.string.graph_average_rate), Grids.singleGridSize
                / finalTime));

        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        gridLabelRenderer.setVerticalAxisTitle(getString(R.string.graph_vertical_title));
        DataPoint[] points = new DataPoint[25];
        for (int i = 0; i < data.length; i++) {
            int previousTime;
            if (i != 0) previousTime = data[i - 1];
            else previousTime = 0;
            double reactionTime = (data[i] - previousTime) / 1000.0;
            points[i] = new DataPoint(i + 1, reactionTime);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setDrawDataPoints(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(Grids.singleGridSize + 1);
        graph.addSeries(series);

        SharedPreferences userSettings = mContext.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        boolean isNightMode = userSettings.getBoolean("night_mode", false);

        if (isNightMode) {
            messageView.setTextColor(Color.WHITE);
            rootView.setBackgroundColor(Color.BLACK);
            stats.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
            totalTime.setTextColor(Color.WHITE);
            avgRate.setTextColor(Color.WHITE);
            gridLabelRenderer.setGridColor(Color.WHITE);
            gridLabelRenderer.setVerticalAxisTitleColor(Color.WHITE);
            gridLabelRenderer.setVerticalLabelsColor(Color.WHITE);
            gridLabelRenderer.setHorizontalLabelsColor(Color.WHITE);
            graph.getViewport().setBackgroundColor(Color.BLACK);
            horizontalTitle.setTextColor(Color.WHITE);
            playGame.setTextColor(Color.WHITE);
            backMenu.setTextColor(Color.WHITE);
        } else {
            rootView.setBackgroundColor(Color.WHITE);
        }

        if (mFromGame)
            playGame.setText(R.string.play_again);
        playGame.setOnClickListener(view -> {
            Intent singlePlayerGame = new Intent(mContext, DoteosActivity.class);
            singlePlayerGame.putExtra("game_mode", 1);
            UserSettings.startClickSound(mContext);
            startActivity(singlePlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        backMenu.setOnClickListener(view -> {
            mToMenu = true;
            exit();
        });

        Animation animationFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        Animation animationFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);

        if (finalTime > 15) {
            messageView.setText(R.string.end_message_slowest);
        } else if (finalTime > 13) {
            messageView.setText(R.string.end_message_slow);
        } else if (finalTime > 12) {
            messageView.setText(R.string.end_message_mediocre);
        } else if (finalTime > 11) {
            messageView.setText(R.string.end_message_aight);
        } else if (finalTime > 10) {
            messageView.setText(R.string.end_message_good);
        } else if (finalTime > 9) {
            messageView.setText(R.string.end_message_pre_good);
        } else if (finalTime > 8) {
            messageView.setText(R.string.end_message_wow);
        } else {
            messageView.setText(R.string.end_message_no_way);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                messageView.setVisibility(View.GONE);
                graphLayout.setVisibility(View.VISIBLE);
                if (graph.getHeight() > graph.getWidth()) {
                    LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            graph.getWidth(), 0.0f);
                    graphParams.setMargins((int) Math.ceil(35 * logicalDensity), 0,
                            (int) Math.ceil(60 * logicalDensity), (int) Math.ceil(15 * logicalDensity));
                    graph.setLayoutParams(graphParams);
                }
                graphLayout.setAnimation(animationFadeIn);
                UserSettings.startMenuMusic(mContext);
                mFromGame = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        new Handler().postDelayed(() -> messageView.startAnimation(animationFadeOut), 2250);
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
        if (!mToMenu) {
            UserSettings.pauseMenuMusic();
        }
        mToMenu = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mFromGame)
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
