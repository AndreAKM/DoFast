package com.example.oem.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = GameView.class.getName();
    public GameView(Context context,int i_radius) {
        super(context);
        main = (game) context;
        radius = (i_radius /elCount) * elCount;
        Log.d(TAG, String.format("GameView: (0, 0) - (%d, %d)",radius, radius));
        init();
    }
    int elCount = 8;
    game main;
    private GameThread thread;
    private float initX, initY, endX, endY;
            int radius;
    private boolean drawing = true;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    boolean isDrawing() { return drawing || main.isCange();}

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (isDrawing()) {
            drawField(canvas);
        }
    }

    void drawField(Canvas canvas) {
        int step = radius / 8;
        for (int y = 0; y < radius; y += step){
            for (int x = 0; x < radius; x += step){
                Log.d(TAG, String.format("onDraw: (%d, %d) - (%d, %d)", x, y, x + step, y + step));
                //paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(main.getColor(x / step, y / step));
                canvas.drawRect(x, y, x + step, y + step, paint);
            }
        }
    }
    int idField(float x) {
        int step = radius / 8;
        return (int) (x/step);
    }
    void swap() {
        int x1= idField(initX);
        int y1 = idField(initY);
        int x2 = x1;
        int y2 = y1;
        if(endX < initX) --x2;
        if(endX > initX) ++x2;
        if(endY < initY) --y2;
        if(endY > initY) ++y2;
        main.swap(x1, y1, x2, y2);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            endX = event.getX();
            endY = event.getY();

        } else if (action == MotionEvent.ACTION_DOWN) {
            initX = event.getX();
            initY = event.getY();
            //radius = 1;
            drawing = true;
        } else if (action == MotionEvent.ACTION_UP) {
            swap();
            drawing = false;
        }

        return true;
    }

    public GameView(Context context, AttributeSet attrs ) {
        super(context, attrs);
        init();
    }


    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private SurfaceHolder mSurfaceHolder;
    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //Canvas c = mSurfaceHolder.lockCanvas();
        //drawField(c);
        //mSurfaceHolder.unlockCanvasAndPost(c);
        setFocusable(true); // make sure we get key events

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                               int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       //
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}

