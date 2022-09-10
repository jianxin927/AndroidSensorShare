package com.example.androidsensorshare;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.os.PowerManager;

public  class LoopTransfer extends Thread  implements SensorEventListener {
    SensorMonitor myMonitor = new SensorMonitor();
    private Boolean needstop = false;
    float lastSensorValue, newSensorValue;
    UDP_Broadcast udp;
    boolean running = false;
    public LoopTransfer(UDP_Broadcast _udp){
        lastSensorValue = 0;
        newSensorValue = 0;
        udp = _udp;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        newSensorValue = sensorEvent.values[0];
        //if(abs(newSensorValue-lastSensorValue) > 50 ){
        //    lastSensorValue = newSensorValue;
        //}
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(sensor == ((SensorManager) globalAppClass.globalContext.getSystemService(Context.SENSOR_SERVICE)).
                getDefaultSensor(Sensor.TYPE_LIGHT)){
            switch (i) {
                case 0:System.out.println("Unreliable");break;
                case 1:System.out.println("Low Accuracy");break;
                case 2:System.out.println("Medium Accuracy");break;
                case 3://开始monitor的时候会触发一次该函数，当前精度是High
                    System.out.println("High Accuracy");break;
            }
        }
    }
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void run() {
        Looper.prepare();
        super.run();
        myMonitor.listener = this;
        myMonitor.startMonitor(Sensor.TYPE_LIGHT,globalAppClass.globalContext);
        lastSensorValue = -100;//make sure first value valid.
        running = true;
        while(!needstop){
            if(abs(newSensorValue-lastSensorValue) > 50 ){
                lastSensorValue = newSensorValue;
                //udp.send("light_sensor:" + String.valueOf(myMonitor.data()));
                udp.send("light_sensor:" + String.valueOf(lastSensorValue));
            }
            try {
                //Thread.sleep(1000*3);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        myMonitor.stopMonitor();
        Looper.loop();
        running = false;
    }

    public void shouldStop(){
        needstop = true;
    }
}
