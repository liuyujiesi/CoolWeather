package com.example.model;

/**
 * Created by Administrator on 2016/7/27.
 */
public class County {
    private String countyName;
    private String countyCode;
    private String cityCode;

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "County{" +
                "countyName='" + countyName + '\'' +
                ", countyCode='" + countyCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                '}';
    }
}
