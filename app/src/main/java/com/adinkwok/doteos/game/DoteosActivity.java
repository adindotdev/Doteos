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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.adinkwok.doteos.UserSettings;
import com.adinkwok.doteos.game.thread.GameThread;
import com.adinkwok.doteos.game.view.DoubleGameView;
import com.adinkwok.doteos.game.view.SingleGameView;

public class DoteosActivity extends Activity {
    private int mGameMode;
    private SingleGameView mSingleGameView;
    private DoubleGameView mDoubleGameView;
    private GameThread mGameThread;
    private Context mContext;

    public static void closeMe(Context context) {
        Intent intent = new Intent("com.adinkwok.doteos.KILL_ME");
        intent.putExtra("action", "close");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GAME", "DOTEOS CREATED, YEAH!");
        super.onCreate(savedInstanceState);
        mContext = this;
        enableImmersiveMode();
        UserSettings.pauseMenuMusic();
        registerKillReceiver();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenHeight = size.y;
        int screenWidth = size.x;
        if (screenWidth > screenHeight) {
            screenHeight = size.x;
            screenWidth = size.y;
        }

        mGameMode = getIntent().getIntExtra("game_mode", 1);
        if (mGameMode == 1) {
            mSingleGameView = new SingleGameView(mContext, screenHeight, screenWidth);
            mGameThread = new GameThread(mSingleGameView.getHolder(), mSingleGameView);
            setContentView(mSingleGameView);
        } else if (mGameMode == 2) {
            mDoubleGameView = new DoubleGameView(mContext, screenHeight, screenWidth);
            mGameThread = new GameThread(mDoubleGameView.getHolder(), mDoubleGameView);
            setContentView(mDoubleGameView);
        }

        startActivityForResult(new Intent(mContext, GameInstructionsActivity.class)
                .putExtra("game_mode", mGameMode), 0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    private void registerKillReceiver() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra("action");
                if (action.equals("close")) {
                    finish();
                }
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver,
                new IntentFilter("com.adinkwok.doteos.KILL_ME"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(0, 0);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Log.d("GAME", "WE MADE IT!");
            new Handler().postDelayed(() -> {
                if (mGameMode == 1) {
                    if (mSingleGameView != null) {
                        mSingleGameView.setGameOn(true);
                        mSingleGameView.startTimer();
                    }
                } else {
                    if (mDoubleGameView != null) {
                        mDoubleGameView.setGameOn(true);
                        mDoubleGameView.startTimer();
                    }
                }
            }, 1000);
        } else {
            Log.d("GAME", "WE DIDN'T MAKE IT...");
            exit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        while (true) {
            try {
                mGameThread.setRunning(false);
                mGameThread.join();
                mGameThread = null;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("GAME", "DOTEOS PAUSED, YEAH!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGameThread == null) {
            if (mGameMode == 1)
                mGameThread = new GameThread(mSingleGameView.getHolder(), mSingleGameView);
            else
                mGameThread = new GameThread(mDoubleGameView.getHolder(), mDoubleGameView);
            mGameThread.setRunning(true);
            mGameThread.start();
        }
        Log.d("GAME", "DOTEOS RESUMED, YEAH!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GAME", "DOTEOS DESTROYED, aw...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("GAME", "BACK PRESSED");
        UserSettings.startClickSound(mContext);
        exit();
    }

    private void exit() {
        Log.d("GAME", "EXITING NOW");
        Intent intent = new Intent(mContext, QuitSplashActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
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
