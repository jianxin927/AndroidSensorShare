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
    CheckBox ckbox_autoip;
    EditText edit_targetip;
    Switch sw;
    TextView iplist;
    UDP_Broadcast udp = null;
    LoopTransfer sender;
    Intent startIntent;
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

        globalAppClass.netSenderActivity = this;

        udp = new UDP_Broadcast(51996);
        sender = new LoopTransfer(udp);//auto destroy when complete
        startIntent = new Intent(this, MyService.class);

        iplist.setText(udp.ip_info);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //udp.assignip(edit_targetip.getText().toString());
                    //sender.start();
                    startService(startIntent);
                }else{
                    //sender.needstop = true;
                    stopService(startIntent);
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
}