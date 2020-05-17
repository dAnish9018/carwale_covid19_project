package com.app.baseproject.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RereshCastReceiver extends BroadcastReceiver {

    private RefreshInterface wishlistInterface;

    public RereshCastReceiver(RefreshInterface wishlistInterface) {
        this.wishlistInterface = wishlistInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        wishlistInterface.onRefresh();
    }
}
