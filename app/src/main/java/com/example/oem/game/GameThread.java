package com.example.oem.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private static final String TAG = GameThread.class.getName();
    private SurfaceHolder myThreadSurfaceHolder;
    private GameView myThreadSurfaceView;
    private boolean myThreadRun = false;


    public GameThread(SurfaceHolder surfaceHolder,
                        GameView surfaceView,
                        Bitmap border, Rect borderRect) {
        myThreadSurfaceHolder = surfaceHolder;
        myThreadSurfaceView = surfaceView;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    public void setRunning(boolean b) {
        myThreadRun = b;
    }
    Paint mPaint;
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
                Log.d(TAG, "Exception: " + e.toString());
            }
            finally {
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
