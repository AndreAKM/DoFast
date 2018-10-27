/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.education.geometry.dofast;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Class of player action processing thread
 */
public class GameThread extends Thread {
    private static final String TAG = GameThread.class.getName();

    private SurfaceHolder myThreadSurfaceHolder;
    private GameView myThreadSurfaceView;
    private boolean myThreadRun = false;

    /**
     * constructor
     * @param surfaceHolder reference to surface holder
     * @param surfaceView reference to the game view
     */
    public GameThread(SurfaceHolder surfaceHolder,
                        GameView surfaceView) {
        myThreadSurfaceHolder = surfaceHolder;
        myThreadSurfaceView = surfaceView;
    }

    public void setRunning(boolean b) {
        myThreadRun = b;
    }

    @Override
    public void run() {
        // super.run();
        while (myThreadRun) {
            Canvas c = null;
            if(myThreadSurfaceView.isDrawing() == false && myThreadSurfaceView.mooving == false) continue;
            try {
                c = myThreadSurfaceHolder.lockCanvas(null);
                synchronized (myThreadSurfaceHolder) {
                    myThreadSurfaceView.onDraw(c);
                }
            }
            catch (Exception e) {
                Logger.Debug(TAG, "Exception: " + e.toString());
            }
            finally {
                if (c != null) {
                    myThreadSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
