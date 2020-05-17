package com.app.baseproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.app.baseproject.home.HomeActivity;
import com.app.baseproject.home.HomePresenter;

public class RequestCallingService extends Service {
    HomePresenter presenter;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d("Testing", "Service got created");
        //presenter = new HomePresenter(this);

       // Toast.makeText(this, "ServiceClass.onCreate()", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Toast.makeText(this, "Auto Refresh", Toast.LENGTH_LONG).show();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(HomeActivity.REFRESH);
        this.sendBroadcast(broadcastIntent);

        Log.d("Testing", "Service got started");
    }
}
