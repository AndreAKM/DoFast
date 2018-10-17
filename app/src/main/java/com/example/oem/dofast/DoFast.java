/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Activity class
 */
public class DoFast extends AppCompatActivity {

    private static final String TAG = DoFast.class.getName();
    public static final String GameName = "DoFaster";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     *  Parameters to initial game Engine
     */
    public static final int FieldSize = 8;  //! <size of game field in game blocks
    public static final int ColorCount = 5; //! <count of blocks color variants

    private static final String FieldState = "FieldState"; //! <

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        T = createEngine(8, 5);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        JSONArray serialyze = new JSONArray();
        startReading();
        for (int x = 0; x < 8; ++x) {
            JSONArray row = new JSONArray();
            for (int y = 0; y < 8; ++y) {
                row.put(getElvalue(T, x, y));
            }
            serialyze.put(row);
        }
        outState.putString(FieldState, serialyze.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume () {
        super.onResume();
        Display display = getWindowManager().getDefaultDisplay();

        int widht = display.getWidth();
        int height = display.getHeight();

        setContentView(new GameView(this, widht, height));
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Log.d(TAG, "onRestoreInstanceState");
        String fss = state.getString(FieldState);
        if (fss != null) {
            Log.d(TAG, "onRestoreInstanceState fss: " + fss);
            try {
                if (T == 0) {
                    Log.d(TAG, "onRestoreInstanceState create Engine ");
                    T = createEngine(FieldSize, ColorCount);
                }
                startChanging(T);
                JSONArray serialyze = new JSONArray(fss);
                for (int x = 0; x < 8; ++x) {
                    JSONArray row = serialyze.getJSONArray(x);
                    for (int y = 0; y < 8; ++y) {
                        setElvalue(T, x, y, row.getInt(y));
                    }
                    serialyze.put(row);
                }
                endChanging(T);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<Integer> color =
            Arrays.asList(Color.BLACK, Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN, Color.LTGRAY);
    /** < block colors collection
     *
     * first element is default color. The color used fpr deleted blocs.
     */

    long T; //!< instance of Engine

    /**
     * Wrappers under native methods
     */

    /**
     * Get of bloc's color
     * @param x x - coordinate
     * @param y y - coordinate
     * @return return color value
     */
    Integer getColor(int x, int y) {
        return color.get(getElvalue(T, x, y));
    }

    /**
     * Get default color
     * @return return color value
     */
    Integer getDefaultColor() {
        return color.get(0);
    }

    /**
     * swap blocs
     * @param x1 x - coordinate of block first
     * @param y1 y - coordinate of block first
     * @param x2 x - coordinate of block second
     * @param y2 y - coordinate of block second
     */
    void swap(int x1, int y1, int x2, int y2) {
        swap(T, x1, y1, x2, y2);
    }

    /**
     * Check that the field was changed
     * @return true if the field was changed
     */
    boolean isCange() {
        return isChange(T);
    }

    /**
     * Inform engine that we start reading data.
     *
     * lock data for reading
     */
    public void startReading() {
        startReading(T);
    }

    /** I
     * nform engine that we finished reading.
     * Unlock data.
     */
    public void endReading() {
        endReading(T);
    }

    /**
     * Get count of blocks which was deleted
     * @return count blocks which was deleted from previous reading field.
     */
    public int getCount(){return getCount(T);}

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private native long createEngine(int fieldSize, int elCount);
    private native void destry(long T);
    private native int getElvalue(long T, int x, int y);
    private native void swap(long T, int x1, int y1, int x2, int y2);
    private native boolean isChange(long T);
    private native void startReading(long T);
    private native void endReading(long T);
    private native void startChanging(long T);
    private native void endChanging(long T);
    private native void setElvalue(long T, int x, int y, int value);
    private native int getCount(long T);
    }
