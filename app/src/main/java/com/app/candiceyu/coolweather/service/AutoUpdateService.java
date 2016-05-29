package com.app.candiceyu.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.app.candiceyu.coolweather.util.HttpCallbackListener;
import com.app.candiceyu.coolweather.util.HttpUtil;
import com.app.candiceyu.coolweather.util.Utility;

/**
 * Created by candiceyu on 16/5/29.
 */
public class AutoUpdateService  extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int strtId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        Long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent intent1=new Intent(this, AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent1, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent,  flags, strtId);

    }

    private void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weahterCode=preferences.getString("weather_code", "");
        String address="http://www.weather.com.cn/data/cityinfo/"+weahterCode+".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
