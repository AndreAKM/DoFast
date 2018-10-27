/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.education.geometry.dofast;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import java.util.Calendar;

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
    public static final int sequenceSize = 3;

    private static final String FieldState = "FieldState"; //! <
    private static final String CounterState = "CounterState";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.Debug(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Display display = getWindowManager().getDefaultDisplay();

        engine = new Engine(FieldSize, ColorCount, sequenceSize);

        int widht = display.getWidth();
        int height = display.getHeight();
        sharedPreferences = getSharedPreferences(GameName, Context.MODE_PRIVATE);
        counter = new Counter(this);
        restore();
        gameView= new GameView(this, widht, height, counter);
        setContentView(R.layout.activity_game);

        LinearLayout surface = (LinearLayout)findViewById(R.id.middleSurface);
        surface.addView(gameView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Logger.Debug(TAG, String.format("BuildConfig.DEBUG %b", BuildConfig.DEBUG));
    }

    SharedPreferences sharedPreferences = null;
    GameView gameView = null;
    Counter counter = null;
    Engine engine = null;
    SensorManager sensorManager;
    private final SensorEventListener sensorListener = new SensorEventListener() {
        long prevTime, time;
        float accelPrevious, accel;
        int count=0;
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            accelPrevious = accel;
            accel = (float) Math.sqrt((double) (x * x + y * y + z * z));

            time = Calendar.getInstance().getTimeInMillis();
            if(time - prevTime > 1000)
                count = 0;
            if (accel - accelPrevious > 4) {
                if (count == 0){
                    prevTime = time;
                }
                if(count >= 1) {
                    if(time - prevTime < 500) {
                        Logger.Debug(TAG, String.format("create new engine" ));
                        engine.flush();
                    }
                }
                Logger.Debug(TAG, String.format("time - %d, count -%d",time - prevTime , count ));
                ++count;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.Debug(TAG, "onSaveInstanceState");

        Logger.Debug(TAG, "save state");
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

        sensorManager.registerListener(
                sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        Logger.Debug(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(sensorListener);
        super.onStop();
    }

    public Engine getEngine() {
        return engine;
    }

    protected void restore() {
        Logger.Debug(TAG, "restore");
        String fss = sharedPreferences.getString(FieldState, "");

        counter.loadState(sharedPreferences.getString(CounterState,""));
        engine.loadState(sharedPreferences.getString(FieldState, ""));
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
