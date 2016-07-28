package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.model.City;
import com.example.model.County;
import com.example.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class CoolWeatherDB {
    private static final String DB_NAME = "cool_weather";
    private static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    //构造函数私有化,只能本类调用
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //获取SQLiteDatabase 对象coolWeatherDB
    public static synchronized CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    //向数据库保存 Province对象
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    //获取所有 Province列表
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        //最好用接口, 不然sql语句会犯错误. 尤其是字符串和 int的查询引号问题!!!!
        Cursor cursor = db.rawQuery("select * from Province", null);
       // Cursor cursor = db.query("Province", null, null, null, null, null, null);
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        //@@@@@@@@@@@@new province一定得放在里面, 不然虽然取出来都不一样,但是list里却都是一样的. 都是最后一个 台湾
                        Province province = new Province();
                        province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                        province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                        list.add(province);
                    } while (cursor.moveToNext()); // 光标跳到下一条记录
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    //向数据库保存 City对象
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_code", city.getProvinceCode());
            db.insert("City", null, values);
        }
    }

    //获取 所有City列表
    public List<City> loadCity(String provinceCode) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.rawQuery("select * from City where province_code = ?", new String[]{provinceCode});
        // Log.d("CoolWeatherDB测试:", ""+cursor.getCount()); 为0
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        City city = new City();
                        city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                        city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                        city.setProvinceCode(provinceCode);
                        list.add(city);
                    } while (cursor.moveToNext()); // 光标跳到下一条记录
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    //向数据库保存 County对象
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_code", county.getCityCode());
            db.insert("County", null, values);
        }
    }

    //获取 所有County列表
    public List<County> loadCounty(String cityCode) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_code = ?", new String[]{cityCode}, null, null, null);
        //Cursor cursor = db.rawQuery("select * from County where city_code = ?", new String[]{cityCode});
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        County county = new County();
                        county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                        county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                        county.setCityCode(cityCode);
                        list.add(county);
                    } while (cursor.moveToNext()); // 光标跳到下一条记录
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //Log.d("CoolWeatherDB", "测试LoadCounty: " + list.toString());     //  []空
        return list;
    }
}
