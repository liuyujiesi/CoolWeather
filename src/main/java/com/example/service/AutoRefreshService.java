package com.example.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.model.WeatherInfo;
import com.example.myapplication.MyApplication;
import com.example.receiver.AutoRefreshReceiver;
import com.example.util.HttpCallbackListener;
import com.example.util.HttpUtils;
import com.example.util.PreferenceUtils;

/**
 * Created by Administrator on 2016/7/29.
 */
public class AutoRefreshService extends Service {
    private WeatherInfo.WeatherinfoBean weatherinfoBean = null;
    private MyBinder myBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AutoRefreshService", "onCreate方法执行完毕");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshWeatherInfo();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000;        //八个小时
        // long triggerAtTime = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000; //八个小时
        Intent broadcastIntent = new Intent(this, AutoRefreshReceiver.class);
        //跳转到广播
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        Log.d("AutoRefreshService", "onStartCommand执行完毕");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void refreshWeatherInfo() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String countyCode = pref.getString("cityid", "");
        Log.d("AutoRefreshService", "countyCode是: "+countyCode);
        String address = "http://www.weather.com.cn/data/cityinfo/" + countyCode + ".html";
        HttpUtils.sendHttpRequestByGetWithHttpClient(address, new HttpCallbackListener() {
            //在另一个线程里
            @Override
            public void onFinish(String response) {
                if (response != null) {
                    //测试用
                    weatherinfoBean = PreferenceUtils.handleWeatherResponse(AutoRefreshService.this, response);
                    Log.d("AutoRefreshService", "refreshWeatherInfo()是:weatherinfoBean :" + weatherinfoBean);
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
    public class MyBinder extends Binder {

        public AutoRefreshService getService() {
            Log.d("MyBinder", "MyBindergetService方法执行完毕");
                return AutoRefreshService.this;
        }
    }
}
