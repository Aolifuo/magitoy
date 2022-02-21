package com.mikasuki.mikaweather.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.City;
import com.mikasuki.mikaweather.model.District;
import com.mikasuki.mikaweather.model.Province;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class CitySelector {
    private Context m_context;
    private Gson gson;
    private MikaWeatherDB db;

    public CitySelector(Context context) {
        m_context = context;
        gson = new Gson();
        db = MikaWeatherDB.getInstance(context);
    }

    // http://guolin.tech/api/china

    public void loadProvinceToDB(String address, Handler handler) {
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();
                List<Province> provinceList = gson.fromJson(response_data, new TypeToken<List<Province>>(){}.getType());
                for (Province province : provinceList) {
                    province.setProvince_id(province.getId());
                    db.insertProvince(province);
                }
                Message msg = new Message(); //生产者消费者模型， 线程安全操作
                msg.what = 0;
                if (null != handler) // 防止活动结束导致handle被释放的问题
                    handler.sendMessage(msg);

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadCityToDB(String address, Handler handler, Area province) {
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();
                List<City> cityList = gson.fromJson(response_data, new TypeToken<List<City>>(){}.getType());
                for (City city : cityList) {
                    city.setProvince_id(province.getId());
                    city.setCity_id(city.getId());
                    db.insertCity(city);
                }
                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putInt("ProvinceId", province.getId());
                msg.setData(bundle);
                if (null != handler)
                    handler.sendMessage(msg);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadDistrictToDB(String address, Handler handler, Area city) {
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();
                List<District> districtList = gson.fromJson(response_data, new TypeToken<List<District>>(){}.getType());
                for (District district : districtList) {
                    district.setProvince_id(city.getProvince_id());
                    district.setCity_id(city.getId());
                    db.insertDistrict(district);
                }
                Message msg = new Message();
                msg.what = 2;
                Bundle bundle = new Bundle();
                bundle.putInt("ProvinceId", city.getProvince_id());
                bundle.putInt("CityId", city.getCity_id());
                msg.setData(bundle);
                if (null != handler)
                    handler.sendMessage(msg);

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }


}
