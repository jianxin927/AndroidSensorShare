package com.example.androidsensorshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

class myStartupReceiver extends BroadcastReceiver {

    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(Context context, @NonNull Intent intent) {
        Log.i("Wmx logs::", intent.getAction());
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
            Toast.makeText(context, "Auto startup", Toast.LENGTH_LONG).show();
        }
    }
};
