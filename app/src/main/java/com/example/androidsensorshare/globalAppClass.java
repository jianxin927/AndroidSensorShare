package com.example.androidsensorshare;

import android.app.Application;
import android.content.Context;

public class globalAppClass extends Application {
    public
    static Context globalContext;

    public void onCreate(){
        super.onCreate();
        globalContext = getApplicationContext();
    }
}
