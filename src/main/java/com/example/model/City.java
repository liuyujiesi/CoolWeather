package com.example.model;

/**
 * Created by Administrator on 2016/7/27.
 */
public class City {
    private String cityName;
    private String cityCode;
    private String provinceCode;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                '}';
    }
}
