package com.app.candiceyu.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.candiceyu.coolweather.R;
import com.app.candiceyu.coolweather.db.CoolWeatherDB;
import com.app.candiceyu.coolweather.model.City;
import com.app.candiceyu.coolweather.model.Country;
import com.app.candiceyu.coolweather.model.Province;
import com.app.candiceyu.coolweather.util.HttpCallbackListener;
import com.app.candiceyu.coolweather.util.HttpUtil;
import com.app.candiceyu.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by candiceyu on 16/5/27.
 */
public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;


    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;
    private int currentLevel;

    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("selected_city", false) && !isFromWeatherActivity){
            Intent intent=new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCountries();
                }else if(currentLevel==LEVEL_COUNTRY){
                    String countryCode=countryList.get(position).getCountryCode();
                    Intent intent=new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("country_code", countryCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvices();
    }

    private void queryProvices(){
        provinceList=coolWeatherDB.getProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p: provinceList){
                dataList.add(p.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null, "province");
        }

    }

    private void queryCities(){
        cityList=coolWeatherDB.getCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for (City c: cityList){
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCountries(){
        countryList=coolWeatherDB.getCountries(selectedCity.getId());
        if(countryList.size()>0){
            dataList.clear();
            for (Country c: countryList){
                dataList.add(c.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTRY;
        }else{
            queryFromServer(selectedCity.getCityCode(), "country");
        }
    }

    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handlerProvincesResponse(coolWeatherDB, response);
                }else if("city".equals(type)){
                    result=Utility.handlerCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                }else if("country".equals(type)){
                    result=Utility.handlerCountriesResponse(coolWeatherDB, response, selectedCity.getId());
                }

                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closedProgressDialog();
                            if("province".equals(type)){
                                queryProvices();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closedProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closedProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        if(currentLevel==LEVEL_CITY){
            queryProvices();
        }else if(currentLevel==LEVEL_COUNTRY){
            queryCities();
        }else{

            if(isFromWeatherActivity){
                Intent intent=new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
