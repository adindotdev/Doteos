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

package com.adinkwok.doteos.game.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.adinkwok.doteos.game.view.DoubleGameView;
import com.adinkwok.doteos.game.view.SingleGameView;

public class GameThread extends Thread {

    private final SurfaceHolder mSurfaceHolder;
    private final SingleGameView mSingleGameView;
    private final DoubleGameView mDoubleGameView;
    private boolean mIsRunning;

    public GameThread(SurfaceHolder surfaceHolder, SingleGameView singleGameView) {
        super();
        mSurfaceHolder = surfaceHolder;
        mSingleGameView = singleGameView;
        mDoubleGameView = null;
    }

    public GameThread(SurfaceHolder surfaceHolder, DoubleGameView doubleGameView) {
        super();
        mSurfaceHolder = surfaceHolder;
        mDoubleGameView = doubleGameView;
        mSingleGameView = null;
    }

    @Override
    public void run() {
        while (mIsRunning) {
            Canvas canvas = this.mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                synchronized (mSurfaceHolder) {
                    if (mDoubleGameView == null) {
                        assert this.mSingleGameView != null;
                        this.mSingleGameView.update();
                        this.mSingleGameView.draw(canvas);
                    } else {
                        this.mDoubleGameView.draw(canvas);
                    }
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean isRunning) {
        mIsRunning = isRunning;
    }
}
