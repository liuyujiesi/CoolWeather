package com.example.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.controller.ActivityController;
import com.example.db.CoolWeatherDB;
import com.example.model.City;
import com.example.model.County;
import com.example.model.Province;
import com.example.model.WeatherInfo;
import com.example.util.HttpCallbackListener;
import com.example.util.HttpUtils;
import com.example.util.PreferenceUtils;
import com.example.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ChooseAreaActivity extends BaseActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    @butterknife.Bind(R.id.tv_area)
    TextView tv_area;
    @butterknife.Bind(R.id.btn_search)
    ImageButton btn_search;
    @butterknife.Bind(R.id.btn_menu)
    ImageButton btn_menu;
    @butterknife.Bind(R.id.lv_area)
    ListView lv_area;
    private ProgressDialog progressDialog;
    private CoolWeatherDB coolWeatherDB;
    private ArrayAdapter<String> adpter;
    private List<String> dataList = new ArrayList<>();
    // 省 市 县 列表
    private List<Province> privinceList;
    private List<City> cityList;
    private List<County> countyList;
    //选中的省市
    private Province selectedProvince;
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    private StringBuilder cityCodeOfCounty = new StringBuilder();
    private WeatherInfo.WeatherinfoBean weatherinfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();//隐藏标题栏
        }
        adpter = new ArrayAdapter<>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, dataList);
        lv_area.setAdapter(adpter);
        coolWeatherDB = CoolWeatherDB.getInstance(ChooseAreaActivity.this);
        queryProvinces();
        //查询省级数据
    }

    public void queryProvinces() {
        privinceList = coolWeatherDB.loadProvinces();
        if (privinceList.size() > 0) {
            //在数据库查询
            dataList.clear();
            for (Province province : privinceList) {
                dataList.add(province.getProvinceName());
            }
            adpter.notifyDataSetChanged();
            lv_area.setSelection(0);
            tv_area.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            //网络查询
            queryFromServer(null, "province");
        }
    }

    private void queryCities(String provinceCode) {
        cityList = coolWeatherDB.loadCity(provinceCode);
        //////log.d("ChooseAreaActivity", "测试queryCities: " + cityList.toString());
        if (cityList.size() > 0) {
            //在数据库中查询
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adpter.notifyDataSetChanged();
            lv_area.setSelection(0);
            tv_area.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            //网络查询
            queryFromServer(provinceCode, "city");
        }
    }

    private void queryCounties(String cityCode) {
        //10105  01
        countyList = coolWeatherDB.loadCounty(cityCode);
        //@@@@@@@@@不正常 为空的  size是0;
        //log.d("ChooseAreaActivity", "countyList:" + countyList + " cityCode: " + cityCode + "selectedCity.getCityCode(): " + selectedCity.getCityCode());
        if (countyList.size() > 0) {
            //在数据库中查询
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adpter.notifyDataSetChanged();
            lv_area.setSelection(0);
            tv_area.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            //网络查询
            queryFromServer(cityCode, "county");
        }
    }

    public void queryFromServer(final String code, final String type) {
        String address;
        if (TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/city3jdata/china.html";
        } else if ("city".equals(type)) {
            address = "http://www.weather.com.cn/data/city3jdata/provshi/" + code + ".html";
        } else {
            address = "http://www.weather.com.cn/data/city3jdata/station/" + code + ".html";
        }
        showProgressDialog();
        ////log.d("ChooseAreaActivity", "出错地址是: "+address);
        HttpUtils.sendHttpRequestByGetWithHttpClient(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type))
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, code);
                    ////log.d("ChooseAreaActivity", "result:" + result);
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(coolWeatherDB, response, code);
                }
                if (result) {
                    //通过runOnUiThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities(code);
                            } else if ("county".equals(type)) {
                                queryCounties(code);        //问题在这 出现死循环
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                //回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //查询天气信息
    private void queryWeatherInfo(String code) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + code + ".html";
        showProgressDialog();
        weatherinfoBean = PreferenceUtils.getWeatherFromPref(ChooseAreaActivity.this);
        //如果本地有, 那么直接本地取.
        if (weatherinfoBean != null) {
            WeatherDetailAcitivity.startAction(ChooseAreaActivity.this, weatherinfoBean);
        } else {
            //本地没有,网络请求获取,
            //HttpURLConnection 有BUG 报EOF异常,我们使用HttpClient来请求网络
            HttpUtils.sendHttpRequestByGetWithHttpClient(address, new HttpCallbackListener() {

                @Override
                public void onFinish(String response) {
                    if (response != null) {
                        closeProgressDialog();
                        //weatherInfo = WeatherInfo.objectFromData(response);
                        //存到本地sharedPreference
                        PreferenceUtils.handleWeatherResponse(ChooseAreaActivity.this, response);
                        //log.d("ChooseAreaActivity", "weatherInfo是:: " + weatherInfo.getWeatherinfo().toString());
                        WeatherDetailAcitivity.startAction(ChooseAreaActivity.this, weatherinfoBean);
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    //回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


    public static void showProgressDialog() {
    }

    public static void closeProgressDialog() {
    }

    @OnItemClick(R.id.lv_area)
    void onItemClick(int position) {
        if (currentLevel == LEVEL_PROVINCE) {
            selectedProvince = privinceList.get(position);
            //privinceList正常 selectedProvince正常 provinceCode正常
            String provinceCode = selectedProvince.getProvinceCode();
            //查询市级数据
            queryCities(provinceCode);
        } else if (currentLevel == LEVEL_CITY) {
            selectedCity = cityList.get(position);
            cityCodeOfCounty.append(selectedProvince.getProvinceCode()).append(selectedCity.getCityCode());
            //得到完整的CountyCode 了
            //查询县级数据
            queryCounties(cityCodeOfCounty.toString());
            //log.d("ChooseAreaActivity", selectedCity.getCityName() + cityCodeOfCounty);
            cityCodeOfCounty.delete(0, cityCodeOfCounty.length());
            //清空
        } else if (currentLevel == LEVEL_COUNTY) {
            County selectedCounty = countyList.get(position);
            queryWeatherInfo("101010100");
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities(selectedProvince.getProvinceCode());
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            //finish();
            //保险起见 销毁全部活动
            ActivityController.finishAll();
        }
    }
    public static void startAction(Context context) {
        Intent intent = new Intent(context, ChooseAreaActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @butterknife.OnClick({R.id.btn_search, R.id.btn_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                break;
            case R.id.btn_menu:
                break;
        }
    }
}
