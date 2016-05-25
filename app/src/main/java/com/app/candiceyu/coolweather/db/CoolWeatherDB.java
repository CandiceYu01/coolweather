package com.app.candiceyu.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.candiceyu.coolweather.model.City;
import com.app.candiceyu.coolweather.model.Country;
import com.app.candiceyu.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by candiceyu on 16/5/25.
 */
public class CoolWeatherDB {

    public static final String DB_NAME="cool_weather";
    public static final int VERSION=1;

    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db=dbHelper.getWritableDatabase();
    }

    public synchronized  static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null)coolWeatherDB=new CoolWeatherDB(context);
        return  coolWeatherDB;
    }

    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> getProvinces(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        if(cursor!=null)cursor.close();
        return list;
    }

    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    public List<City> getCities(){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City", null, null, null, null, null,null);
        if(cursor.moveToFirst()){
            do {
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getString(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
        }

        if (cursor!=null)cursor.close();
        return list;
    }

    public void saveCountry(Country country){
        if(country!=null){
            ContentValues values=new ContentValues();
            values.put("country_code", country.getCountryCode());
            values.put("country_name", country.getCountryName());
            values.put("city_id", country.getCityId());
            db.insert("Country", null, values);
        }
    }

    public List<Country> getCountries(){
        List<Country> list=new ArrayList<Country>();
        Cursor cursor=db.query("Country", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                Country country=new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                list.add(country);
            }while (cursor.moveToNext());
        }

        if (cursor!=null)cursor.close();
        return list;
    }
}
