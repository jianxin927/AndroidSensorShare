package com.example.androidsensorshare;

import android.app.Application;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class globalAppClass extends Application {
    public
    static Context globalContext;
    static NetSenderActivity netSenderActivity;
    private static Ringtone mRingtone;
    public static LoopTransfer sender;
    public static boolean ringNotify;
    public static int alarmtime;
    public void onCreate(){
        super.onCreate();
        globalContext = getApplicationContext();

        Uri mUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //获取系统默认的notification提示音,Uri:通用资源标志符
        mRingtone = RingtoneManager.getRingtone(globalContext, mUri);
    }
    public static void PlayNotificationSound() {
       if(ringNotify)
           mRingtone.play();
    }

}
