package com.example.falldetector;

import java.util.ArrayList;

public class DecisionTree {
    private float ax_var;
    private float ax_mean;
    private float ay_var;
    private float ay_mean;
    private float az_var;
    private float az_mean;
    private float gx_var;
    private float gx_mean;
    private float gy_var;
    private float gy_mean;
    private float gz_var;
    private float gz_mean;
    private float a_mag_mean;
    private float g_mag_mean;
    private float a_mag_var;
    private float g_mag_var;
    private float a_max_mag;

    public DecisionTree(){
        ax_var = 0;
        ax_mean = 0;
        ay_var = 0;
        ay_mean = 0;
        az_var = 0;
        az_mean = 0;
        gx_var = 0;
        gx_mean = 0;
        gy_var = 0;
        gy_mean = 0;
        gz_var = 0;
        gz_mean= 0;
        a_mag_mean = 0;
        g_mag_mean = 0;
        a_mag_var = 0;
        g_mag_var = 0;
        a_max_mag = 0;
    }

    public void features(ArrayList<float[]> accel, ArrayList<float[]>  gyro){
        float[] accelMean = mean(accel);
        ax_mean = accelMean[0];
        ay_mean = accelMean[1];
        az_mean = accelMean[2];

        float[] gyroMean = mean(gyro);
        gx_mean = gyroMean[0];
        gy_mean = gyroMean[1];
        gz_mean = gyroMean[2];

        float[]
    }

    private float[] var(ArrayList<float[]> arr){
        float[] mean = mean(arr);

        float[] var = {0,0,0};

        //outer loop for x y and z
        for( int i = 0; i < 3; ++i){
            //inner loop for all the rows in arr
            for(int j = 0; j < arr.size(); ++j){
                float diff = arr.get(j)[i] - mean[i];
                float square = diff * diff;
                var[i]+=square;
            }
            var[i] = var[i]/(arr.size()-1);
        }
        return var;
    }

    private float mag_var(float[] arr){
        float mean = mag_mean(arr);

        float var =0;
        for(int i = 0; i < arr.length; ++i){
            float diff = arr[i] - mean;
            float square = diff * diff;
            var += square;
        }
        var = var/(arr.length-1);

        return var;
    }

    private float[] mean(ArrayList<float[]> arr){
        float xsum = 0;
        float ysum = 0;
        float zsum = 0;
        for(int i = 0; i < arr.size(); i++){
            xsum += arr.get(i)[0];
            ysum += arr.get(i)[1];
            zsum += arr.get(i)[2];
        }
        float xmean = xsum/arr.size();
        float ymean = ysum/arr.size();
        float zmean = zsum/arr.size();
        float[] temp = {xmean, ymean, zmean};
        return temp;
    }

    private float mag_mean(float[] arr){
        float sum = 0;
        for(int i = 0; i < arr.length; i++){
           sum += arr[i];
        }
        float mean = sum/arr.length;
        return mean;
    }

    private float[] magnitude(ArrayList<float[]> arr){
        float[] temp = new float[arr.size()];
        for(int j = 0; j < arr.size(); ++j){
            float x = arr.get(j)[0];
            float y = arr.get(j)[1];
            float z = arr.get(j)[2];

            float mag = (float) Math.sqrt((x*x) + (y*y) + (z*z));
            temp[j] = (mag);
        }
        return temp;
    }
    public String predict(){
        return "PLACEHOLDER";
    }
}
