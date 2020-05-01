package com.example.falldetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    EditText phoneNumber;
    TextView serviceText;
    Button save;
    Button call;
    Switch serviceSwitch;

    boolean makeCall;

    static boolean falseAlarm;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Phone = "phoneKey";
    private static final int REQUEST_CALL = 1;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.editText);
        serviceText = findViewById(R.id.serviceText);
        save = findViewById(R.id.button);
        serviceSwitch = findViewById(R.id.switch1);
        call = findViewById(R.id.button2);

        makeCall = true;

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final Intent intent = new Intent(this, AccelerometerService.class);
//        final Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9784894787"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number  = phoneNumber.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                
                editor.putString(Phone, number);
                editor.commit();
                Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();


            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((Switch) v).isChecked();
                if (on) {
                    //Do something when switch is on/checked
                    serviceText.setText("Service is on....");
                    startService(intent);
                } else {
                    //Do something when switch is off/unchecked
                    serviceText.setText("Service is off....");
                    stopService(intent);
                }
            }
        });

        final Handler handler = new Handler();
        int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(AccelerometerService.makeCall && makeCall){
                    makePhoneCall();
                    makeCall = false;
                }
                //Log.d(TAG,acc_data.size() +":::" +  gyro_data.size());


                handler.postDelayed(this, 1000);
            }
        }, delay);

    }

    private void makePhoneCall() {
//        String number = "9784894787";
        String number = sharedpreferences.getString(Phone, null);
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
