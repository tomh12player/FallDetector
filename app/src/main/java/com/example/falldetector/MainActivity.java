package com.example.falldetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
    Switch serviceSwitch;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Phone = "phoneKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.editText);
        serviceText = findViewById(R.id.serviceText);
        save = findViewById(R.id.button);
        serviceSwitch = findViewById(R.id.switch1);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final Intent intent = new Intent(this, AccelerometerService.class);


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
    }




}
