/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.util.Pair;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Counter {
    private static final String TAG = Counter.class.getName();

    private Integer count = new Integer(0);
    private static final String BestResultSP = "BEST-RESULT";
    private SharedPreferences sharedPreferences;
    DoFast main;

    public Counter(DoFast main) {
        this.main = main;
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
    public int currentCounterColore = Color.YELLOW;
    private Integer currentcount = new Integer(0);

    private List<Pair<Double,Double>> actionResults = new ArrayList<>();
    private double summ = 0.f;
    private long prevCount;
    private double timeInterval = 0.f;
    private static final String SUMM = "SUMM";
    private static final String TIME = "TIME";
    private static final String DATA = "DATA";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String RESULT = "RESULT";
    private static final String COUNTER = "COUNTER";

    /**
     * serialize counter
     * @return return jason which contain necessary data to continue measure
     */
    public  String saveState() {
        JSONObject state = new JSONObject();
        try {
            state.put(SUMM, summ);
            state.put(TIME, timeInterval);
            state.put(COUNTER, currentcount);
            JSONArray data = new JSONArray();
            for (Pair<Double, Double> d: actionResults) {
                JSONObject result = new JSONObject();
                result.put(TIMESTAMP, d.first);
                result.put(RESULT, d.second);
                data.put(result);
            }
            state.put(DATA, data);
        } catch (JSONException e) {
            Log.d(TAG, "couldn't save counter state: " + e.toString());

            e.printStackTrace();
        }
        Log.d(TAG, "success save counter state: ");

        return state.toString();
    }

    /**
     * initialise counter to continue measure
     * @param state json with necessary data
     */
    public void loadState(String state) {
        prevCount = Calendar.getInstance().getTimeInMillis();
        bestResult = sharedPreferences.getInt(BestResultSP,0);
        if(state.isEmpty() == true) return;
        try {
            JSONObject st = new JSONObject(state);

            summ = st.getDouble(SUMM);
            timeInterval = st.getDouble(TIME);
            currentcount = st.getInt(COUNTER);
            JSONArray data = st.getJSONArray(DATA);
            actionResults.clear();
            int size = data.length();
        for (int i = 0; i != size; ++i) {
            JSONObject result = data.getJSONObject(i);
            actionResults.add(new Pair<>(result.getDouble(TIMESTAMP), result.getDouble(RESULT)));
        }
        } catch (JSONException e) {
            Log.d(TAG, "couldn't load counter state: " + e.toString());
            e.printStackTrace();
        }
        Log.d(TAG, "success loading counter state");

    }
    /**
     * counting play results
     * counting how mach block player could deleted during last 15 seconds
     */
    public void counting() {
        synchronized (count) {
            long ct = Calendar.getInstance().getTimeInMillis();
            double interval = (ct - prevCount) / 1000;

            if (interval > 30) {
                count = 0;
                prevCount = ct;
                return;
            }
            if (main.getCount() == 0) return;
            count += main.getCount();
            if (interval < 1.f) {
                return;
            }

            currentCounterColore = Color.CYAN;
            timeInterval += interval;
            double current = count;//((float) (count) / interval);
            actionResults.add(new Pair<>(interval, current));
            summ += current;
            prevCount = ct;
            currentcount = (int) (summ * 60 /timeInterval) ;
            Log.d(TAG, String.format("Counter: count %d, interval %f, curent %d sum %f, collect size %d", count, timeInterval, currentcount, summ, actionResults.size()));
            count = 0;
            if (actionResults.size() >= 15) {
                currentCounterColore = Color.MAGENTA;
                Pair<Double, Double> el = actionResults.remove(0);
                timeInterval -= el.first;
                summ -= el.second;
                if (bestResult < currentcount) {
                    bestResult = currentcount;
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putInt(BestResultSP, bestResult);
                    e.apply();
                }
            }

        }

    }
}
