package com.example.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class WeatherInfo {

    /**
     * city : 北京
     * cityid : 101010100
     * temp1 : -2℃
     * temp2 : 16℃
     * weather : 晴
     * img1 : n0.gif
     * img2 : d0.gif
     * ptime : 18:00
     */

    private WeatherinfoBean weatherinfo;

    public static WeatherInfo objectFromData(String str) {

        return new com.google.gson.Gson().fromJson(str, WeatherInfo.class);
    }

    public static WeatherInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new com.google.gson.Gson().fromJson(jsonObject.getString(str), WeatherInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<WeatherInfo> arrayWeatherInfoFromData(String str) {

        Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WeatherInfo>>() {
        }.getType();

        return new com.google.gson.Gson().fromJson(str, listType);
    }

    public static List<WeatherInfo> arrayWeatherInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WeatherInfo>>() {
            }.getType();

            return new com.google.gson.Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public WeatherinfoBean getWeatherinfo() {
        return weatherinfo;
    }

    public void setWeatherinfo(WeatherinfoBean weatherinfo) {
        this.weatherinfo = weatherinfo;
    }

    public static class WeatherinfoBean implements Serializable {
        private String city;
        private String cityid;
        private String temp1;
        private String temp2;
        private String weather;
        private String img1;
        private String img2;
        private String ptime;

        public static WeatherinfoBean objectFromData(String str) {

            return new com.google.gson.Gson().fromJson(str, WeatherinfoBean.class);
        }

        public static WeatherinfoBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new com.google.gson.Gson().fromJson(jsonObject.getString(str), WeatherinfoBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<WeatherinfoBean> arrayWeatherinfoBeanFromData(String str) {

            Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WeatherinfoBean>>() {
            }.getType();

            return new com.google.gson.Gson().fromJson(str, listType);
        }

        public static List<WeatherinfoBean> arrayWeatherinfoBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new com.google.gson.reflect.TypeToken<ArrayList<WeatherinfoBean>>() {
                }.getType();

                return new com.google.gson.Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCityid() {
            return cityid;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getTemp1() {
            return temp1;
        }

        public void setTemp1(String temp1) {
            this.temp1 = temp1;
        }

        public String getTemp2() {
            return temp2;
        }

        public void setTemp2(String temp2) {
            this.temp2 = temp2;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getImg1() {
            return img1;
        }

        public void setImg1(String img1) {
            this.img1 = img1;
        }

        @Override
        public String toString() {
            return "WeatherinfoBean{" +
                    "city='" + city + '\'' +
                    ", cityid='" + cityid + '\'' +
                    ", temp1='" + temp1 + '\'' +
                    ", temp2='" + temp2 + '\'' +
                    ", weather='" + weather + '\'' +
                    ", img1='" + img1 + '\'' +
                    ", img2='" + img2 + '\'' +
                    ", ptime='" + ptime + '\'' +
                    '}';
        }

        public String getImg2() {
            return img2;
        }

        public void setImg2(String img2) {
            this.img2 = img2;
        }

        public String getPtime() {
            return ptime;
        }

        public void setPtime(String ptime) {
            this.ptime = ptime;
        }
    }
}
