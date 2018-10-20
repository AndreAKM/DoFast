/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.dofast;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


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
    private static final String CounterState = "CounterState";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Display display = getWindowManager().getDefaultDisplay();

        engine = new Engine(8, 5);

        int widht = display.getWidth();
        int height = display.getHeight();
        sharedPreferences = getSharedPreferences(GameName, Context.MODE_PRIVATE);
        counter = new Counter(this);
        restore();
        gameView= new GameView(this, widht, height, counter);
        setContentView(R.layout.activity_game);

        LinearLayout surface = (LinearLayout)findViewById(R.id.middleSurface);
        surface.addView(gameView);
    }

    SharedPreferences sharedPreferences = null;
    GameView gameView = null;
    Counter counter = null;
    Engine engine = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        Log.d(TAG, "save state");
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(FieldState, engine.saveState());
        e.putString(CounterState, counter.saveState());
        e.apply();
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onResume () {
        super.onResume();
        gameView.setDrawing(true);

        Log.d(TAG, "onResume");
    }

    public Engine getEngine() {
        return engine;
    }

    protected void restore() {
        Log.d(TAG, "restore");
        String fss = sharedPreferences.getString(FieldState, "");

        counter.loadState(sharedPreferences.getString(CounterState,""));
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
    }
