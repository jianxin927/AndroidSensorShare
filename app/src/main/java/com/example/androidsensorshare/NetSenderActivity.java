package com.example.androidsensorshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class NetSenderActivity extends AppCompatActivity {

    Context thisContext;
    String targetip;
    UDP_Broadcast udp = null;

    private  class loopsend extends Thread {
        SensorMonitor myMonitor = new SensorMonitor();
        Context context;
        public Boolean needstop = false;
        public loopsend(Context _context){
            context = _context;
        }
        @Override
        public void run() {
            super.run();
            while(!needstop){
                myMonitor.startMonitor(Sensor.TYPE_LIGHT,context);
                //Toast.makeText(context, "value:" + String.valueOf(myMonitor.data()), Toast.LENGTH_SHORT).show();
                udp.send("light_sensor:" + String.valueOf(myMonitor.data()));
                try {
                    Thread.sleep(1000*3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            myMonitor.stopMonitor();
        }
    }
    loopsend sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_sender);

        thisContext = this;
        udp = new UDP_Broadcast(thisContext, 51996);


        Switch sw = findViewById(R.id.switch_enable);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sender = new loopsend(thisContext);//auto destroy when complete
                    sender.needstop = false;
                    sender.start();
                }else{
                    sender.needstop = true;
                }
            }
        });

        EditText edit = findViewById(R.id.edit_targetip);
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                targetip = String.valueOf(textView.getText());
                if(targetip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
                    udp.setip(targetip);
                }else
                    udp.setip("");
                return false;
            }
        });

        CheckBox ck = findViewById(R.id.checkBox_autoip);
        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                udp.setautoip(b);
            }
        });


    }
}