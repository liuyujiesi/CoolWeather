
package com.example.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/7/26.
 */
public class MyApplication extends Application {

    public  static Context contex;

    @Override
    public void onCreate() {
        super.onCreate();
        //程序一启动就获取 全局Context
        contex= getApplicationContext();
    }

    public static Context getContex() {
        return contex;
    }

    public static void setContex(Context contex) {
        MyApplication.contex = contex;
    }
}