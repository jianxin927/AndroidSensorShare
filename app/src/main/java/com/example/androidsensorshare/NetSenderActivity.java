package com.example.androidsensorshare;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetSenderActivity extends AppCompatActivity {
    public
    String TAG = "######";
    Context mainActivity;
    CheckBox ckbox_autoip;
    EditText edit_targetip;
    Switch sw;
    TextView iplist;
    UDP_Broadcast udp = null;

    loopsend sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_sender);

        String [] permissions = {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.FOREGROUND_SERVICE,
        };
        requestPermissions(permissions, 1);

        iplist = findViewById(R.id.iplist);
        sw = findViewById(R.id.switch_enable);
        edit_targetip = findViewById(R.id.edit_targetip);
        ckbox_autoip = findViewById(R.id.checkBox_autoip);

        mainActivity = this;

        udp = new UDP_Broadcast(51996);

        iplist.setText(udp.ip_info);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    udp.assignip(edit_targetip.getText().toString());
                    sender = new loopsend();//auto destroy when complete
                    sender.needstop = false;
                    sender.start();
                }else{
                    sender.needstop = true;
                }
            }
        });

        ckbox_autoip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                udp.setautoip(b);
                if(b) {
                    udp.retrieveipList();
                    udp.updateTarget();
                    //byte [] bb = udp.group.getAddress();
                    edit_targetip.setText(udp.ip_final_dest);
                }else {
                    udp.assignip(edit_targetip.getText().toString());
                }
            }
        });


    }

    private  class loopsend extends Thread  implements SensorEventListener{
        SensorMonitor myMonitor = new SensorMonitor();
        public Boolean needstop = false;
        float lastSensorValue, newSensorValue;
        public loopsend(){
            lastSensorValue = 0;
            newSensorValue = 0;
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
        @Override
        public void run() {
            super.run();
            myMonitor.listener = this;
            myMonitor.startMonitor(Sensor.TYPE_LIGHT,globalAppClass.globalContext);
            lastSensorValue = -100;//make sure first value valid.
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
        }
    }
}