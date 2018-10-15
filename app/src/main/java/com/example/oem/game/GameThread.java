package com.example.oem.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private SurfaceHolder myThreadSurfaceHolder;
    private GameView myThreadSurfaceView;
    private boolean myThreadRun = false;

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
            if(myThreadSurfaceView.isDrawing() == false) continue;
            try {

                c = myThreadSurfaceHolder.lockCanvas(null);
                synchronized (myThreadSurfaceHolder) {
                    myThreadSurfaceView.onDraw(c);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    myThreadSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
