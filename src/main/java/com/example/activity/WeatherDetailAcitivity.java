package com.example.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
            //已经选定过城市了.
            String lastRequstDate  = weatherinfoBean.getPtime().substring(0,9); //上次更新时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String currentDate = simpleDateFormat.format(new Date());
            //如果今天更新过.
            if (lastRequstDate.equals(currentDate)) {
                //直接显示本地preference储存的内容.
                showWeather(weatherinfoBean);
            }else{
                //网络请求
                countyCode = weatherinfoBean.getCityid();
                refreshWeatherInfo(countyCode);
                showWeather(weatherinfoBean);

            }
            Log.d("WeatherDetailAcitivity", weatherinfoBean.toString());
        }
    }
    private void refreshWeatherInfo(String countyCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + countyCode + ".html";
        HttpUtils.sendHttpRequestByGetWithHttpClient(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                if (response != null) {
                    weatherinfoBean =  PreferenceUtils.handleWeatherResponse(WeatherDetailAcitivity.this, response);
                    //log.d("ChooseAreaActivity", "weatherInfo是:: " + weatherInfo.getWeatherinfo().toString());
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

    public void showWeather(WeatherInfo.WeatherinfoBean weatherinfoBean){
        tv_area.setText(weatherinfoBean.getCity());
        tv_county.setText(weatherinfoBean.getCity());
        tv_ptime.setText("更新时间: " + weatherinfoBean.getPtime());
        tv_temp.setText(weatherinfoBean.getTemp1() + "°  ~   " + weatherinfoBean.getTemp2() + "°");
        iv_weather.setImageResource(R.drawable.sunny);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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
}
