package com.app.candiceyu.coolweather.util;

import android.text.TextUtils;

import com.app.candiceyu.coolweather.db.CoolWeatherDB;
import com.app.candiceyu.coolweather.model.City;
import com.app.candiceyu.coolweather.model.Country;
import com.app.candiceyu.coolweather.model.Province;

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
                    city.setCityCode(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
            }
            return  true;
        }
        return false;
    }

    public  synchronized static boolean handlerCountriesReponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
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
}
