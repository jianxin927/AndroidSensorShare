package com.example.androidsensorshare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.androidsensorshare.databinding.ActivityMainBinding;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SensorMonitor myMonitor = new SensorMonitor();
        myMonitor.startMonitor(Sensor.TYPE_LIGHT,this);
        context = this;
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Toast.makeText(MainActivity.this, "value:" + String.valueOf(myMonitor.data()), Toast.LENGTH_SHORT).show();
                UDP_Broadcast udp = null;
                udp = new UDP_Broadcast(context);
                udp.send("light_sensor:" + String.valueOf(myMonitor.data()));
            }
        });

    }

}