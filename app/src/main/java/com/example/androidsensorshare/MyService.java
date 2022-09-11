package com.example.androidsensorshare;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private AlarmManager alarmManager;
    private PendingIntent pi;
    Intent intentAlarm;
    public MyService() {
        //对于service，alarm会触发onStartCommand，对于Active会触发OnCreate
        intentAlarm = new Intent(globalAppClass.globalContext, MyService.class);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pi = PendingIntent.getService(this, 0, intentAlarm, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + globalAppClass.alarmtime , pi);
        globalAppClass.PlayNotificationSound();
        if(globalAppClass.sender.running){
            //Toast.makeText(globalAppClass.globalContext, "Already started",
            //        Toast.LENGTH_SHORT).show();
            globalAppClass.sender.forceUploadSensorValue();
            return super.onStartCommand(intent, flags, startId);
        }

        globalAppClass.sender.start();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1,getNotification());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        globalAppClass.sender.shouldStop();
        alarmManager.cancel(pi);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private NotificationManager notificationManager;
    private String notificationId = "serviceid";
    private String notificationName = "servicename";

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.myicon_normal)
                .setContentTitle("LightSensorShare")
                .setContentText("Running");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

}