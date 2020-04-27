package com.example.falldetector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    private Sensor mGyroscope;

    NotificationHelper helper = new NotificationHelper(this);
    //
    ArrayList<float[]> acc_data;
    ArrayList<float[]> gyro_data;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI, new Handler());

        //also add gyroscope stuff
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_GAME);


        //initialize the arrays
        acc_data = new ArrayList<>();
        gyro_data = new ArrayList<>();


        final Handler handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                DecisionTree tree = new DecisionTree();
                tree.features(acc_data, gyro_data);
                String test = tree.predict();


                    //Log.d(TAG,acc_data.size() +":::" +  gyro_data.size());
                helper.createNotification("Did you fall", "" + test);
                
                handler.postDelayed(this, 10000);
            }
        }, delay);

        return START_STICKY;
        //return START_NOT_STICKY;


    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            float[] temp = {x,y,z};
            gyro_data.add(temp);
            //679 is the length of the window we used for training
            if(gyro_data.size() > 679){
                gyro_data.remove(0);
            }
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float[] temp = {x,y,z};
            acc_data.add(temp);
            if(acc_data.size() > 679){
                acc_data.remove(0);
            }
        }



        /*
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 11) {
            NotificationHelper helper = new NotificationHelper(this);
            helper.createNotification("Fall Detector Running", "To stop, close in app");
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onDestroy(){
        helper.removeNotification();
    }



}
