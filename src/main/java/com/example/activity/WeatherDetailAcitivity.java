package com.example.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.WeatherInfo;
import com.example.receiver.AutoRefreshReceiver;
import com.example.service.AutoRefreshService;
import com.example.util.HttpCallbackListener;
import com.example.util.HttpUtils;
import com.example.util.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WeatherDetailAcitivity extends BaseActivity {
    @Bind(R.id.tv_area)
    TextView tv_area;
    @Bind(R.id.btn_search)
    ImageButton btn_search;
    @Bind(R.id.btn_menu)
    ImageButton btn_menu;
    @Bind(R.id.tv_ptime)
    TextView tv_ptime;
    @Bind(R.id.tv_temp)
    TextView tv_temp;
    @Bind(R.id.btn_last)
    ImageButton btn_last;
    @Bind(R.id.iv_weather)
    ImageView iv_weather;
    @Bind(R.id.btn_next)
    ImageButton btn_next;
    @Bind(R.id.tv_county)
    TextView tv_county;
    @Bind(R.id.iv_left_dot)
    ImageView iv_left_dot;
    @Bind(R.id.iv_center_dot)
    ImageView iv_center_dot;
    @Bind(R.id.iv_right_dot)
    ImageView iv_right_dot;
    @Bind(R.id.iv_choose)
    ImageView iv_choose;
    @Bind(R.id.tv_choose)
    TextView tv_choose;
    private WeatherInfo.WeatherinfoBean weatherinfoBean;
    private String countyCode;
    private ServiceConnection serviceConnection = new ServiceConnection() {// (虚拟的)实例化接口
        // 复写抽象函数
        //BindService()时候调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AutoRefreshService.MyBinder myBinder = (AutoRefreshService.MyBinder) service;
            //通过Binder获取service实例对象@@@@@@@@@@@@
            AutoRefreshService myService = myBinder.getService();
            Log.d("WeatherDetailAcitivity", "myService实例获取完毕:" + myService);
            //由于是在多线程中调用,所以会返回null
            //weatherinfoBean = myService.refreshWeatherInfo();
            Log.d("WeatherDetailAcitivity", "myService.refreshWeatherInfo()绑定后获取完毕" + weatherinfoBean);
            showWeather(weatherinfoBean);
            Log.d("WeatherDetailAcitivity", "onServiceConnected方法执行完毕" + weatherinfoBean);
        }
        //Onbindservice()时候调用,肯定会执行service的onDestroy()方法的.
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }
    };


    //启动方法
    public static void startAction(Context context, WeatherInfo.WeatherinfoBean weatherInfoBean) {
        Intent intent = new Intent(context, WeatherDetailAcitivity.class);
        intent.putExtra("weatherinfoBean", weatherInfoBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail_acitivity);
        ButterKnife.bind(this);
        weatherinfoBean = PreferenceUtils.getWeatherFromPref(WeatherDetailAcitivity.this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();//隐藏标题栏
        }
        //第一次
        if (weatherinfoBean == null) {
            iv_weather.setVisibility(View.GONE);
            btn_last.setVisibility(View.GONE);
            btn_next.setVisibility(View.GONE);
        } else {
            iv_choose.setVisibility(View.GONE);
            tv_choose.setVisibility(View.GONE);
            //第一次選擇完城市
            String lastRequstDate = weatherinfoBean.getPtime(); //上次更新时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.CHINA);
            String currentDate = simpleDateFormat.format(new Date());
            Log.d("WeatherDetailAcitivity", "lastRequstDate" + Integer.parseInt(currentDate.substring(11, 16)));
            Log.d("WeatherDetailAcitivity", "currentDate" + Integer.parseInt(lastRequstDate.substring(11, 16)));
            //计算距离上次间隔时间
            int intervalTime = Integer.parseInt(currentDate.substring(11, 16)) - Integer.parseInt(lastRequstDate.substring(11, 16));
            //如果间隔小于8个小时
            Log.d("WeatherDetailAcitivity", "intervalTime:" + intervalTime);
            // if (intervalTime<8 * 60 * 60 * 1000) {
            if (false) {
                Log.d("WeatherDetailAcitivity", "小于八小时");
                showWeather(weatherinfoBean);
                startAutoRefreshService();
            } else {
                Log.d("WeatherDetailAcitivity", "大于八小时");
                //网络请求
                countyCode = weatherinfoBean.getCityid();
                Log.d("WeatherDetailAcitivity", "countyCode是: " + countyCode);
                bindAutoRefreshService();
            }
        }
    }

    private void refreshWeatherInfo(String countyCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + countyCode + ".html";
        HttpUtils.sendHttpRequestByGetWithHttpClient(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                if (response != null) {
                    weatherinfoBean = PreferenceUtils.handleWeatherResponse(WeatherDetailAcitivity.this, response);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                //回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    public void showWeather(WeatherInfo.WeatherinfoBean weatherinfoBean) {
        tv_area.setText(weatherinfoBean.getCity());
        tv_county.setText(weatherinfoBean.getCity());
        tv_ptime.setText("更新时间: " + weatherinfoBean.getPtime());
        tv_temp.setText(weatherinfoBean.getTemp1() + "°  ~   " + weatherinfoBean.getTemp2() + "°");
        iv_weather.setImageResource(R.drawable.sunny);
    }

    private void bindAutoRefreshService() {
        Log.d("WeatherDetailAcitivity", "bindAutoRefreshService执行完毕");
        Intent intent = new Intent(WeatherDetailAcitivity.this, AutoRefreshService.class);
        intent.putExtra("countyCode", countyCode);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("WeatherDetailAcitivity", "bindAutoRefreshService里面的bindService方法执行完毕");
    }

    private void startAutoRefreshService() {
        Intent intent = new Intent(WeatherDetailAcitivity.this, AutoRefreshService.class);
        intent.putExtra("countyCode", countyCode);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unbindService(serviceConnection);
    }

    @butterknife.OnClick({R.id.iv_choose, R.id.btn_search, R.id.btn_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_choose:
                ChooseAreaActivity.startAction(WeatherDetailAcitivity.this);    //点击添加后跳转到选择城市 界面
                finish();
                break;
            case R.id.btn_search:
                refreshWeatherInfo(countyCode);
                showWeather(weatherinfoBean);
                break;
            case R.id.btn_menu:
                ChooseAreaActivity.startAction(WeatherDetailAcitivity.this);
                finish();
                break;
        }
    }


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
            long triggerAtTime = SystemClock.elapsedRealtime() +  60 * 1000; //八个小时
            //        long triggerAtTime = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000; //八个小时
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
}
