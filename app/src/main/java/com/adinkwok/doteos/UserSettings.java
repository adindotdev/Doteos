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

package com.adinkwok.doteos;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

public class UserSettings {
    private static MediaPlayer mMenuMusic;
    private static MediaPlayer mClickSound;
    private static boolean mSoundEnabled;

    public static void startMenuMusic(Context context) {
        if (mMenuMusic == null) {
            mMenuMusic = MediaPlayer.create(context, R.raw.doteos_menu);
            mMenuMusic.setLooping(true);
        }
        if (!mMenuMusic.isPlaying() && mSoundEnabled)
            mMenuMusic.start();
    }

    public static void pauseMenuMusic() {
        if (mMenuMusic != null) {
            if (mMenuMusic.isPlaying() && mSoundEnabled)
                mMenuMusic.pause();
        }
    }

    public static void startClickSound(Context context) {
        if (mClickSound == null) {
            mClickSound = MediaPlayer.create(context, R.raw.doteos_click);
        }
        if (mSoundEnabled)
            mClickSound.start();
    }

    public static void setSoundEnable(boolean enable) {
        mSoundEnabled = enable;
    }

    public static void setNewHighScore(Context context, int dotIndex, int[][] activeDots, StringBuilder str) {
        final SharedPreferences savedScores = context.getSharedPreferences("saved_scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedScores.edit();
        int score1 = savedScores.getInt("score1", 0);
        int score2 = savedScores.getInt("score2", 0);
        int score3 = savedScores.getInt("score3", 0);
        String score1set = savedScores.getString("score1set", "");
        String score2set = savedScores.getString("score2set", "");
        if (activeDots[dotIndex][4] < score1 || score1 == 0) {
            editor.putInt("score3", score2);
            editor.putInt("score2", score1);
            editor.putInt("score1", activeDots[dotIndex][4]);
            editor.putString("score3set", score2set);
            editor.putString("score2set", score1set);
            editor.putString("score1set", str.toString());
            editor.apply();
        } else if (activeDots[dotIndex][4] != score1) {
            Log.d("GAME VIEW", "GAME FINISHED, YEAH!");
            if (activeDots[dotIndex][4] < score2 || score2 == 0) {
                editor.putInt("score3", score2);
                editor.putInt("score2", activeDots[dotIndex][4]);
                editor.putString("score3set", score2set);
                editor.putString("score2set", str.toString());
                editor.apply();
            } else if ((activeDots[dotIndex][4] != score2
                    && activeDots[dotIndex][4] < score3) || score3 == 0) {
                editor.putInt("score3", activeDots[dotIndex][4]);
                editor.putString("score3set", str.toString());
                editor.apply();
            }
        }
    }
}
