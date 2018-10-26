/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Class of the game view
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    String TAG = GameView.class.getName();

    /**
     * constructor
     * @param context reference to DoFast
     * @param i_screanW screen width
     * @param i_screanH screen height
     */
    public GameView(Context context,int i_screanW, int i_screanH, Counter counter) {
        super(context);
        main = (DoFast) context;
        elCount = DoFast.FieldSize;
        screanH = i_screanH;
        screanW = i_screanW;
        int rad = (Math.min(screanH, screanW) );
        step = rad / elCount;
        shiftY = shiftX = (int) (rad / 30.8);

        radius = (Math.min(screanH - shiftY, screanW - (2*shiftX)) /elCount) * elCount;
        step = radius / elCount;
        highttext = step + shiftY * 2;
        int interval = highttext / 4;
        int shift = step;

        targetBorderRect = new Rect(shift / 2, interval, rad -  shift / 2, interval + highttext);

        int t = targetBorderRect.bottom + interval;

        gameFieldborderRect = new Rect(0, t, rad, rad + t);
        t = gameFieldborderRect.bottom + interval;
        currentCountBorderRect  = new Rect(shift, t, rad - shift, t + highttext);
        t = interval + currentCountBorderRect.bottom;
        bestCountBorderRect = new Rect(shift, t, rad - shift, t + highttext);
        border = BitmapFactory.decodeResource(getResources(), R.drawable.border);
        background = BitmapFactory.decodeResource(
                getResources(), R.drawable.sweets);
        fullScreanRect = new Rect(0, 0, screanW, screanH);
        this.counter =  counter;
        init();
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }

    /**
     * parameters which describe the game layout
     */

    int step;
    int highttext;
    private int elCount = 8;
    private static int shiftX = 35;
    private static int shiftY = 35;
    private int screanW = 20;
    private int screanH = 60;
    DoFast main;
    private GameThread thread;
    private float initX, initY, endX, endY;
            int radius;
    private boolean drawing = true;
    public boolean mooving = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Bitmap border;
    Bitmap background;
    Rect gameFieldborderRect;
    Rect targetBorderRect;
    Rect currentCountBorderRect;
    Rect bestCountBorderRect;
    Rect fullScreanRect;

    /**
     * check should draw the game field
     * @return true if the field should be re drown
     */
    boolean isDrawing() { return drawing||main.engine.isCange();}

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if(mooving||isDrawing()){
            backgroundDraw(canvas);
        }
        if(mooving) {
            int x= idFieldX(initX);
            int y = idFieldY(initY);

            fieldDrowing(canvas, x, y);
            moveDrowing(canvas);
        }
        if (isDrawing()) {
            fieldDrowing(canvas, -1, -1);
            counter.counting();
            drawing = false;
        }
    }
    Counter counter = null; //!< counter instance

    /**
     * class counter
     */


    /**
     * Calculate x coordinate to start printing text. It's used to position text in center of border
     * @param rec - rectangle of border
     * @param coutS - count of text symbols
     * @param sizeS - average size of symbol
     * @return return start text position
     */
    float startOftext(Rect rec, String coutS, float sizeS) {
        float center = (rec.right + rec.left)/2;
        return center - (coutS.length() * sizeS / 2 );
    }

    /**
     * drowing all static layout elements
     * @param canvas - canvas
     */
    void backgroundDraw(Canvas canvas){
        canvas.drawBitmap(background,null , fullScreanRect, paint);
        canvas.drawBitmap(border, null , gameFieldborderRect, paint);
        canvas.drawBitmap(border, null , currentCountBorderRect, paint);
        canvas.drawBitmap(border, null , bestCountBorderRect, paint);

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setTextSize(highttext);
        shadowPaint.setStrokeWidth(highttext/10);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        targetDraw(canvas, shadowPaint);
        shadowPaint.setColor(counter.currentCounterColore);

        canvas.drawText(counter.getCurrentcount().toString(),
                startOftext(currentCountBorderRect, counter.getCurrentcount().toString(), highttext /2),
                currentCountBorderRect.bottom -shiftY, shadowPaint);
        shadowPaint.setColor(Color.GREEN);
        canvas.drawText(counter.getBestResult().toString(),
                startOftext(bestCountBorderRect, counter.getBestResult().toString(), highttext /2),
                bestCountBorderRect.bottom - shiftY, shadowPaint);
    }

    /**
     * Draw some hints for user
     * @param canvas - canvas
     */
    void targetWinerDraw(Canvas canvas) {
        if(main.engine.isDone() == false) return;
        int centr = screanW / 2;
        targetBorderRect.left = centr - step / 2;
        targetBorderRect.right = centr + step / 2;
        canvas.drawBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.golden_cup),null , targetBorderRect, new Paint());
    }

    void targetDraw(Canvas canvas, Paint textPaint) {
        if(main.engine.isFinish() == true) {
            targetWinerDraw(canvas);
            return;
        }
        int widhtTarget = main.engine.getTargetSequentSize() * (step + shiftX) - shiftX;
        int y = targetBorderRect.top + shiftY;
        paint.setStyle(Paint.Style.FILL);
        int centr = screanW / 2;
        int start = centr - widhtTarget / 2;
        for (int x = start, count = 0; count < main.engine.getTargetSequentSize(); x += step + shiftX, ++count) {
            int color = main.engine.getTargetColor();
            if (color == main.engine.getDefaultColor()) return;
            paint.setColor(color);
            canvas.drawRoundRect(new RectF(x, y, x + step, y + step), cornrad, cornrad, paint);
        }
    }

    int cornrad = 10;
    /**
     * draw flying block which follows to user finger
     * @param canvas - canvas
     */
    void moveDrowing(Canvas canvas) {
        int x= idFieldX(initX);
        int y = idFieldY(initY);
        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(main.engine.getDefaultColor());
        //float xx = shiftX + gameFieldborderRect.left + x*step;
        //float yy = shiftY + gameFieldborderRect.top + y*step;
        //canvas.drawRoundRect(new RectF(xx, yy, xx + step, yy + step), cornrad, cornrad, paint);
        float x1 = endX - (step/2);
        float y1 = endY - (step/2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(main.engine.getColor(x , y));
        canvas.drawRoundRect(new RectF(x1, y1, x1 + step, y1 + step), cornrad, cornrad, paint);

    }

    /**
     * draw game field
     * @param canvas - canvas
     */
    void fieldDrowing(Canvas canvas, int missx, int missy) {
        
        main.engine.startReading();
        paint.setStyle(Paint.Style.FILL);
        for (int y = gameFieldborderRect.top + shiftY; y < gameFieldborderRect.top + radius; y += step){
            for (int x = gameFieldborderRect.left + shiftX; x < gameFieldborderRect.left + radius; x += step){
                int color = main.engine.getColor(idFieldX(x), idFieldY(y));
                if(color == main.engine.getDefaultColor() || (idFieldX(x) == missx && idFieldY(y) == missy)) continue;
                paint.setColor(color);
                canvas.drawRoundRect(new RectF(x, y, x + step, y + step), cornrad, cornrad, paint);
            }
        }
        main.engine.endReading();
    }

    /**
     * get id of block from screen coordinate
     * @param x - screen coordinate
     * @return return id of the block
     */
    int idFieldX(float x) {
        return (int) ((x - shiftX - gameFieldborderRect.left)/step);
    }
    int idFieldY(float x) {
        return (int) ((x - shiftY - gameFieldborderRect.top)/step);
    }
    /**
     * swap first and last touched blocs
     */
    void swap() {
        int x1= idFieldX(initX);
        int y1 = idFieldY(initY);
        int x2 = idFieldX(endX);
        int y2 = idFieldY(endY);
        float changeX = Math.abs(endX - initX);
        float changeY = Math.abs(endY - initY);
        if(changeX > changeY) {
            if (x2 < x1) x2 = x1 - 1;
            if (x2 > x1) x2 = x1 + 1;
            y2 = y1;
        } else if(changeX < changeY) {
            if (y2 < y1) y2 = y1 - 1;
            if (y2 > y1) y2 = y1 + 1;
            x2 = x1;
        }
        else {
            return;
        }
        main.engine.swap(x1, y1, x2, y2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return super.onTouchEvent(event);
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if(gameFieldborderRect.contains((int)x, (int)y) == false) {
            mooving = false;
            drawing = true;
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            endX = x;
            endY = y;
            if(Math.abs(endX - initX) > 2 *step || Math.abs(endY - initY) > 2 *step) {
                mooving = false;
                drawing = true;
                swap();
            } else mooving = true;
        } else if (action == MotionEvent.ACTION_DOWN) {
            initX = x;
            initY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            mooving = false;
            drawing = true;
            swap();
        }

        return true;
    }

    private SurfaceHolder mSurfaceHolder;
    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);

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
        int rad = Math.min(screanH, screanW);
        Rect brect = new Rect(0, 0, rad, rad);
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

