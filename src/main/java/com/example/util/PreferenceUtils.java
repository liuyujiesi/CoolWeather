package com.example.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.model.WeatherInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/7/29.
 */
public class PreferenceUtils {

    public synchronized static WeatherInfo.WeatherinfoBean handleWeatherResponse(Context context, String response) {
        WeatherInfo weatherInfo = WeatherInfo.objectFromData(response);
        saveWeatherToPref(context, weatherInfo);
        return weatherInfo.getWeatherinfo();
    }

    private static void saveWeatherToPref(Context context, WeatherInfo weatherInfo) {
        //注：每个应用都有一个默认的配置文件preferences.xml，使用getDefaultSharedPreferences获取。
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Log.d("PreferenceUtils", date.format(new Date()));
        editor.putBoolean("isSaved",true);
        editor.putString("city", weatherInfo.getWeatherinfo().getCity());
        editor.putString("cityid", weatherInfo.getWeatherinfo().getCityid());
        editor.putString("temp1", weatherInfo.getWeatherinfo().getTemp1());
        editor.putString("temp2", weatherInfo.getWeatherinfo().getTemp2());
        editor.putString("weather", weatherInfo.getWeatherinfo().getWeather());
        editor.putString("ptime", date.format(new Date())+"  " + weatherInfo.getWeatherinfo().getPtime());
        editor.putBoolean(weatherInfo.getWeatherinfo().getCityid(), true);       //储存过的标志位
        editor.apply();         //apply在后台提交, 而commit 马上提交 建议在后台提交.
    }

    public synchronized static  WeatherInfo.WeatherinfoBean getWeatherFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSaved = pref.getBoolean("isSaved", false);
        Log.d("PreferenceUtils", "isSaved:" + isSaved);
        if (isSaved) {
            WeatherInfo.WeatherinfoBean weatherinfoBean = new WeatherInfo.WeatherinfoBean();
            weatherinfoBean.setCity(pref.getString("city", ""));
            weatherinfoBean.setCityid(pref.getString("cityid", "'"));
            weatherinfoBean.setTemp1(pref.getString("temp1", "'"));
            weatherinfoBean.setTemp2(pref.getString("temp2", "'"));
            weatherinfoBean.setWeather(pref.getString("weather", "'"));
            weatherinfoBean.setPtime(pref.getString("ptime", "'"));

            Log.d("PreferenceUtils", "weatherinfoBean:" + weatherinfoBean.toString());

            return weatherinfoBean;
        } else {
            return null;
        }
    }
}