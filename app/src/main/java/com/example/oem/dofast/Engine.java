/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class Engine {
    private static final String TAG = Engine.class.getName();

    public Engine(int fieldSize, int elCount) {
        T = createEngine(fieldSize, elCount);
    }

    @Override
    protected void finalize() throws Throwable {
        destry(T);
        super.finalize();
    }

    /**
     * serialize engine
     * @return return jason which contain necessary data to continue measure
     */
    public  String saveState() {
        JSONArray serialyze = new JSONArray();
        startReading();
        for (int x = 0; x < 8; ++x) {
            JSONArray row = new JSONArray();
            for (int y = 0; y < 8; ++y) {
                row.put(getElvalue(x, y));
            }
            serialyze.put(row);
        }
        return serialyze.toString();
    }


    /**
     * initialise engine to continue measure
     * @param state json with necessary data
     */
    public void loadState(String state) {
        if (state.isEmpty() == true) return;

        Log.d(TAG, "onRestoreInstanceState fss: " + state);
        try {
            startChanging();
            JSONArray serialyze = new JSONArray(state);
            for (int x = 0; x < 8; ++x) {
                JSONArray row = serialyze.getJSONArray(x);
                for (int y = 0; y < 8; ++y) {
                    setElvalue(x, y, row.getInt(y));
                }
                serialyze.put(row);
            }
            endChanging();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void startChanging() {
        startChanging(T);
    }

    public void setElvalue(int x, int y, int anInt) {
        setElvalue(T, x, y, anInt);
    }
    private int getElvalue(int x, int y) {
        return getElvalue(T, x, y);
    }

    private void endChanging() {
        endChanging(T);
    }

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