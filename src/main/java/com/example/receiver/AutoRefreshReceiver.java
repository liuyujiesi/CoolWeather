package com.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.service.AutoRefreshService;

/**
 * Created by Administrator on 2016/7/29.
 */
public class AutoRefreshReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //重新回到 service.
        Intent in = new Intent (context, AutoRefreshService.class);
        context.startService(in);
        Log.d("AutoRefreshReceiver", "一分钟了.廣播得到執行了");
    }
}
