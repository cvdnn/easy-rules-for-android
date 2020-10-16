package com.cvdnn.demo;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class App extends Application {
    public static Context Impl;
    public static Resources Res;

    public static Handler Loople;
    public static ScheduledThreadPoolExecutor Task = new ScheduledThreadPoolExecutor(16);

    @Override
    public void onCreate() {
        super.onCreate();
        Impl = getApplicationContext();

        Res = getResources();

        Loople = new Handler(Looper.getMainLooper());
    }
}
