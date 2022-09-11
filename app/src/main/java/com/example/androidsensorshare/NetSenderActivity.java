package com.example.androidsensorshare;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class NetSenderActivity extends AppCompatActivity {
    public
    String TAG = "######";
    CheckBox ckbox_autoip;
    EditText edit_targetip;
    EditText edit_targetport;
    Switch sw;
    TextView iplist;
    UDP_Broadcast udp;
    Intent startIntent;

    static int backPressCount=0;
    static long lastBackClickTime=0;
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
                Manifest.permission.WAKE_LOCK,

        };
        requestPermissions(permissions, 1);

        iplist = findViewById(R.id.iplist);
        sw = findViewById(R.id.switch_enable);
        edit_targetip = findViewById(R.id.edit_targetip);
        edit_targetport = findViewById(R.id.editTextUDPport);
        ckbox_autoip = findViewById(R.id.checkBox_autoip);

        edit_targetport.setText("51996");

        ((EditText)findViewById(R.id.editTextAlarmtime)).setText("600000");//10 min

        globalAppClass.netSenderActivity = this;

        udp = new UDP_Broadcast();

        startIntent = new Intent(this, MyService.class);

        iplist.setText(udp.ip_info);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    globalAppClass.sender = new LoopTransfer(udp);//auto destroy when complete
                    udp.assignport(Integer.parseInt(edit_targetport.getText().toString()));
                    globalAppClass.ringNotify = ((CheckBox)findViewById(R.id.checkBox_ringNotify)).isChecked();
                    globalAppClass.alarmtime = Integer.parseInt(((EditText)findViewById(R.id.editTextAlarmtime)).getText().toString());
                    startService(startIntent);
                }else{
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

    @Override
    public void onBackPressed() {
        backPressCount++;
        if(backPressCount==1) {
            lastBackClickTime = System.currentTimeMillis();
        }else if(backPressCount==2 && (System.currentTimeMillis() - lastBackClickTime) < 2000) {
            super.onBackPressed();
            backPressCount = 0;//如果不清，再次运行程序会出现count==3的情况
            return;
        }else {
            backPressCount = 0;
        }
        Toast.makeText(this, "Double click back to Exit !", Toast.LENGTH_SHORT).show();
    }
}