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
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    private Context mContext;
    private boolean mCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mCancelled = false;
        new Handler().postDelayed(() -> {
            if (!mCancelled) {
                goToMainMenu();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCancelled = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCancelled) {
            goToMainMenu();
        }
    }

    private void goToMainMenu() {
        Intent intent = new Intent(mContext, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
