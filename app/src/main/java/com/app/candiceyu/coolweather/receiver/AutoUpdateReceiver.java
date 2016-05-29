package com.app.candiceyu.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.candiceyu.coolweather.service.AutoUpdateService;

/**
 * Created by candiceyu on 16/5/29.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
