package com.example.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.db.CoolWeatherDB;
import com.example.model.City;
import com.example.model.County;
import com.example.model.Province;

/**
 * Created by Administrator on 2016/7/27.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            response = response.replace("{", "");
            response = response.replace("}", "");
            String[] divided = response.split(",");
            if (divided != null & divided.length > 0) {
                for (int i = 0; i < divided.length; i++) {
                    Province province = new Province();
                    divided[i] = divided[i].replace("\"", "");
                    province.setProvinceCode(divided[i].substring(0, divided[i].indexOf(":")));
                    province.setProvinceName(divided[i].substring(divided[i].indexOf(":") + 1, divided[i].length()));
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, String provinceCode) {
        if (!TextUtils.isEmpty(response)) {
            response = response.replace("{", "");
            response = response.replace("}", "");
            String[] divided = response.split(",");
            if (divided != null & divided.length > 0) {
                for (int i = 0; i < divided.length; i++) {
                    City city = new City() ;
                    divided[i] = divided[i].replace("\"", "");
                    city.setCityCode(divided[i].substring(0, divided[i].indexOf(":")));
                    city.setCityName(divided[i].substring(divided[i].indexOf(":") + 1, divided[i].length()));
                    city.setProvinceCode(provinceCode);
                    coolWeatherDB.saveCity(city);
                   // Log.d("Utility", "city:" + city);
                }
                return true;
            }
        }
        return false;
    }
    //将县区信息储存到数据库
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, String cityCode) {
        if (!TextUtils.isEmpty(response)) {
            response = response.replace("{", "");
            response = response.replace("}", "");
            String[] divided = response.split(",");
            if (divided != null & divided.length > 0) {
                for (int i = 0; i < divided.length; i++) {
                    County county = new County();
                    divided[i] = divided[i].replace("\"", "");
                    county.setCountyCode(divided[i].substring(0, divided[i].indexOf(":")));
                    county.setCountyName(divided[i].substring(divided[i].indexOf(":") + 1, divided[i].length()));
                    county.setCityCode(cityCode);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }else{
            return false;
        }

    }
}
