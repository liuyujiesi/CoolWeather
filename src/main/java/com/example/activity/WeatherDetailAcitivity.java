package com.example.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.WeatherInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WeatherDetailAcitivity extends ActionBarActivity {
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
    private WeatherInfo.WeatherinfoBean weatherinfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail_acitivity);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();//隐藏标题栏
        }
        weatherinfoBean = (WeatherInfo.WeatherinfoBean) getIntent().getSerializableExtra("weatherinfoBean");
        Log.d("WeatherDetailAcitivity", weatherinfoBean.toString());
        tv_area.setText(weatherinfoBean.getCity());
        tv_county.setText(weatherinfoBean.getCity());
        tv_ptime.setText("更新时间: "+weatherinfoBean.getPtime());
        tv_temp.setText(weatherinfoBean.getTemp1()+"°  ~   "+weatherinfoBean.getTemp2()+"°");
        iv_weather.setImageResource(R.drawable.sunny);
        weatherinfoBean.getWeather();

    }

    public static void startAction(Context context, WeatherInfo.WeatherinfoBean weatherInfoBean) {
        Intent intent = new Intent(context, WeatherDetailAcitivity.class);
        intent.putExtra("weatherinfoBean", weatherInfoBean);
        context.startActivity(intent);
    }
}
