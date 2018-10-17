/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    public GameView(Context context,int i_screanW, int i_screanH) {
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
        borderRect = new Rect(0, 0, rad, rad);

        float shift = (float) step;
        int t = (int) (rad + shift);
        highttext = step + shiftY * 2;

        currentCountBorderRect  = new Rect((int)shift, t, (rad) - (int)shift, t + highttext);
        t = highttext / 4 + currentCountBorderRect.bottom;
        bestCountBorderRect = new Rect((int)shift, t, (rad - (int)shift), t + highttext);
        border = BitmapFactory.decodeResource(getResources(), R.drawable.border);
        background = BitmapFactory.decodeResource(
                getResources(), R.drawable.background);
        fullScreanRect = new Rect(0, 0, screanW, screanH);
        counter =  new Counter();
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
    Rect borderRect;
    Rect currentCountBorderRect;
    Rect bestCountBorderRect;
    Rect fullScreanRect;

    /**
     * check should draw the game field
     * @return true if the field should be re drown
     */
    boolean isDrawing() { return drawing||main.isCange();}

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if(mooving||isDrawing()){
            backgroundDrowing(canvas);
        }
        if(mooving) {
            fieldDrowing(canvas);
            moveDrowing(canvas);
        }
        if (isDrawing()) {
            fieldDrowing(canvas);
            counter.counting();
            drawing = false;
        }
    }
    Counter counter = null; //!< counter instance

    /**
     * class counter
     */
    class Counter {

        private Integer count = new Integer(0);
        private static final String BestResultSP = "BEST-RESULT";
        private SharedPreferences sharedPreferences;

        public Counter() {
            prevCount = Calendar.getInstance().getTimeInMillis();
            sharedPreferences = main.getSharedPreferences(DoFast.GameName, Context.MODE_PRIVATE);
            bestResult = sharedPreferences.getInt(BestResultSP,0);
        }

        public Integer getBestResult() {
            return bestResult;
        }

        public Integer getCurrentcount() {
            return currentcount;
        }

        private Integer bestResult = null;
        private Integer currentcount = new Integer(0);

        private List<Float> lastMinResults = new ArrayList<>();
        private float summ = 0.f;
        private long prevCount;

        /**
         * counting play results
         * counting how mach block player could deleted during last 15 seconds
         */
        public void counting() {
            if (main.getCount() == 0) return;

            synchronized (count) {
                long ct = Calendar.getInstance().getTimeInMillis();
                long interval = (ct - prevCount) / 1000;
                count += main.getCount();
                if (interval < 1) return;
                float current = ((float) (count) / interval);
                Log.d(TAG, String.format("Counter: count %d, interval %d, curent %f, sum %f, collect size %d", count, interval, current, summ, lastMinResults.size()));
                lastMinResults.add(current);
                summ += current;
                count = 0;
                prevCount = ct;
                currentcount = (int) summ;
                if (lastMinResults.size() >= 15) {
                    summ -= lastMinResults.remove(0);
                }
                if (bestResult < currentcount) {
                    bestResult = currentcount;
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putInt(BestResultSP, bestResult);
                    e.apply();
                }
            }

        }
    }

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
    void backgroundDrowing(Canvas canvas){
        canvas.drawBitmap(background,null , fullScreanRect, paint);
        canvas.drawBitmap(border, null , borderRect, paint);
        canvas.drawBitmap(border, null , currentCountBorderRect, paint);
        canvas.drawBitmap(border, null , bestCountBorderRect, paint);
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setTextSize(highttext);
        shadowPaint.setStrokeWidth(highttext/10);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        shadowPaint.setColor(Color.MAGENTA);

        canvas.drawText(counter.getCurrentcount().toString(),
                startOftext(currentCountBorderRect, counter.getCurrentcount().toString(), highttext /2),
                currentCountBorderRect.bottom -shiftY, shadowPaint);
        shadowPaint.setColor(Color.GREEN);
        canvas.drawText(counter.getBestResult().toString(),
                startOftext(bestCountBorderRect, counter.getBestResult().toString(), highttext /2),
                bestCountBorderRect.bottom - shiftY, shadowPaint);
    }

    /**
     * draw flying block which follows to user finger
     * @param canvas - canvas
     */
    void moveDrowing(Canvas canvas) {
        int x= idField(initX, shiftX);
        int y = idField(initY, shiftY);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(main.getDefaultColor());
        float xx = shiftX + x*step;
        float yy = shiftY + y*step;
        canvas.drawRect(xx, yy, xx + step, yy + step, paint);
        float x1 = endX - (step/2);
        float y1 = endY - (step/2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(main.getColor(x , y));
        canvas.drawRect(x1, y1, x1 + step, y1 + step, paint);

    }

    /**
     * draw game field
     * @param canvas - canvas
     */
    void fieldDrowing(Canvas canvas) {
        
        main.startReading();
        for (int y = shiftY; y < radius; y += step){
            for (int x = shiftX; x < radius; x += step){
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(main.getColor(x / step, y / step));
                canvas.drawRect(x, y, x + step, y + step, paint);
            }
        }
        main.endReading();
    }

    /**
     * get id of block from screen coordinate
     * @param x - screen coordinate
     * @param shift - start shifting of the game field
     * @return return id of the block
     */
    int idField(float x, float shift) {
        return (int) ((x - shift)/step);
    }

    /**
     * swap first and last touched blocs
     */
    void swap() {
        int x1= idField(initX, shiftX);
        int y1 = idField(initY, shiftY);
        int x2 = idField(endX, shiftX);
        int y2 = idField(endY, shiftY);

        if(x2 < x1) x2 = x1 - 1;
        if(x2 > x1) x2 = x1 + 1;
        if(y2 < y1) y2 = y1 - 1;
        if(y2 > y1) y2 = y1 + 1;
        main.swap(x1, y1, x2, y2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            endX = event.getX();
            endY = event.getY();
            mooving = true;
        } else if (action == MotionEvent.ACTION_DOWN) {
            initX = event.getX();
            initY = event.getY();
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

