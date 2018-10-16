package com.example.oem.game;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class game extends AppCompatActivity {

    private static final String TAG = game.class.getName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final int FieldSize = 8;
    private static final int ColorCount = 5;
    private static final int Sequence = 3;
    private static final String FieldState = "FieldState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        int radius = Math.min(widht, height);
        setContentView(new GameView(this, radius));
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

    List<Integer> color = Arrays.asList(Color.BLACK, Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN, Color.LTGRAY);
    Random rnd = new Random(System.currentTimeMillis());
    long T;

    Integer getColor(int x, int y) {
        return color.get(getElvalue(T, x, y));
    }

    void swap(int x1, int y1, int x2, int y2) {
        swap(T, x1, y1, x2, y2);
    }

    boolean isCange() {
        return isChange(T);
    }
    public void startReading() {
        startReading(T);
    }

    public void endReading() {
        endReading(T);
    }

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
