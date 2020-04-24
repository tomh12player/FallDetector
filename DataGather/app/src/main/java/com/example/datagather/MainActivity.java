package com.example.datagather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    String fileName;
    EditText fileInput;
    Button submitButton;
    Switch durationSwitch;
    int duration;

    Vibrator vibrator;

    boolean recording;
    ArrayList<float[]> acc_data;
    ArrayList<float[]> gyro_data;

    private static final String TAG = "MainActivity";
    
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //when the app is opened we want to clear the data so that it doesn't become bloated
        File dir = getFilesDir();
        clearFile(dir);

        //for the accelerometer sensor
        Log.d(TAG,"onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(MainActivity.this,accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(MainActivity.this,gyroscope,SensorManager.SENSOR_DELAY_GAME);
        Log.d(TAG,"onCreate: Registered accelerometer listener");


        //for the input and stuff
        fileInput = (EditText) findViewById(R.id.fileInput);
        submitButton = (Button) findViewById(R.id.submitButton);
        durationSwitch = (Switch) findViewById(R.id.duration_switch);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(!recording){
                    fileName = fileInput.getText().toString();
                    //showToast(fileName);
                    recording = true;

                    //start a timer so you have time to put it in your pocket
                    new CountDownTimer(3000, 2000) {

                        public void onTick(long millisUntilFinished) {


                        }

                        public void onFinish() {
                            //after the first amount of time we then start the timer that will collect the data

                            vibrator.vibrate(500);

                            new CountDownTimer(duration, 3000) {

                                public void onTick(long millisUntilFinished) {

                                    showToast("Recording");
                                }

                                public void onFinish() {
                                    recording = false;
                                    showToast("Done");
                                    export(view);
                                    vibrator.vibrate(500);

                                }

                            }.start();

                        }

                    }.start();

                }

            }
        });

        durationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    duration = 300000;
                }
                else{
                    duration = 10000;
                }
            }
        });

        //this is how long the switch will stay active
        duration = 10000;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //for the recording and saving
        recording = false;
        acc_data = new ArrayList<>();
        gyro_data = new ArrayList<>();


    }


    private void showToast(String text){
        Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            if(recording){
                addData(x,y,z,gyro_data);

                //Log.d(TAG,"onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
            }
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            if(recording){
                addData(x,y,z,acc_data);

                //Log.d(TAG,"onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);
            }
        }


    }

    void addData(float x, float y, float z, ArrayList<float[]> list){
        float[] temp = {x,y,z};
        list.add(temp);

    }

    public void export(View view){
        StringBuilder datum = new StringBuilder();
        datum.append("Acceleration x,Acceleration y,Acceleration z,Rotation x, Rotation y, Rotation z");
        //there better be the same number of data in acc_data and gyro_data or this won't work, but there has to be
        int max = acc_data.size();
        if(max > gyro_data.size()){
            max = gyro_data.size();
        }
        for(int i = 0; i <max; i ++){
            float[] temp = acc_data.get(i);
            float[] temp2 = gyro_data.get(i);

            datum.append("\n"+String.valueOf(temp[0])+","+String.valueOf(temp[1])+","+String.valueOf(temp[2])+","+String.valueOf(temp2[0])+","+String.valueOf(temp2[1])+","+String.valueOf(temp2[2]));
        }
        try {
            FileOutputStream out = openFileOutput(fileName+".csv",Context.MODE_PRIVATE);
            out.write((datum.toString().getBytes()));
            out.close();

            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), fileName+".csv");
            Uri path = FileProvider.getUriForFile(context,"com.example.datagather.FileProvider",filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName+".csv");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(fileIntent,"Send mail"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //now delete the file so it doesn't clutter up space


    }

    public void clearFile(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = files.length-1; i > -1; --i) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // do something here with the file
                    boolean deleted = file.delete();
                    Log.d(TAG,"String deleted = " + deleted);

                }
            }
        }
    }





}
