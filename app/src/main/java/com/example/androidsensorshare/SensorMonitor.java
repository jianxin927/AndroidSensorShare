package com.example.androidsensorshare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;


public class SensorMonitor implements SensorEventListener{
    private SensorManager lightSensorManager;
    float sensorValue = 0;
    public void startMonitor(int sensorType, Context context){
        lightSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = lightSensorManager.getDefaultSensor(/*Sensor.TYPE_LIGHT*/ sensorType);
        if (lightSensor == null) {
            Toast.makeText(context, "No sensor", Toast.LENGTH_SHORT).show();
            //System.out.println();
        }else{
            lightSensorManager.registerListener((SensorEventListener) this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    public void stopMonitor(){
        lightSensorManager.unregisterListener((SensorEventListener) this);
    }
    public float data(){
        return sensorValue;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //The sensor’s current value
        sensorValue = sensorEvent.values[0];
        //float currentValue = sensorEvent.values[0];
        //String value = String.valueOf(currentValue);
        //Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        //Retrieve the “light_sensor” string, insert the new value and display it to the user//
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
