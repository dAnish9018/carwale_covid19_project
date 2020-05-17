package com.app.baseproject.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        //new HeadsetMonitoringService().onCreate();
        //startService(new Intent(getApplicationContext(), HeadsetMonitoringService.class));
    }
}
