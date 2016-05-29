package com.app.candiceyu.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.app.candiceyu.coolweather.db.CoolWeatherDB;
import com.app.candiceyu.coolweather.model.City;
import com.app.candiceyu.coolweather.model.Country;
import com.app.candiceyu.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by candiceyu on 16/5/26.
 */
public class Utility {

    public synchronized  static boolean handlerProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){
                for (String p: allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
            }
            return  true;
        }
        return false;
    }

    public  synchronized  static boolean handlerCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null && allCities.length>0){
                for (String c: allCities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
            }
            return  true;
        }
        return false;
    }

    public  synchronized static boolean handlerCountriesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCountries=response.split(",");
            if (allCountries!=null && allCountries.length>0){
                for (String c:allCountries){
                    String[] array=c.split("\\|");
                    Country country=new Country();
                    country.setCountryName(array[1]);
                    country.setCountryCode(array[0]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
            }
            return  true;
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String reponse){
        try {
            JSONObject jsonObject=new JSONObject(reponse);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");

            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
                                       String temp2, String weatherDesp, String publishTime){


        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", simpleDateFormat.format(new Date()));
        editor.commit();
    }

}
