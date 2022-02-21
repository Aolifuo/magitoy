package com.mikasuki.mikaweather.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.RealTimeWeather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class WeatherInfoHelper {

    private String key;
    private Context m_context;
    private Gson gson = new Gson();

    public WeatherInfoHelper(Context context, String k) {
        m_context = context;
        key = k;
    }

    public void getRealTimeWeather(Area area, Handler handler) {
        String location = area.getWeather_id().substring(2);
        HttpUtil.sendOkHttpRequest(m_context.getResources().getString(R.string.realtime_weather_address) + location + "&key=" + key, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();

                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("RealTime", response_data);
                bundle.putInt("id", area.getId());
                bundle.putString("name", area.getName());
                bundle.putString("weather_id", area.getWeather_id());
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

    public void getHourlyWeather(Area area, Handler handler) {
        String location = area.getWeather_id().substring(2);
    /*
        HttpUtil.sendOkHttpRequest(m_context.getResources().getString(R.string.hourly_weather_address) + location + "&key=" + key, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();

                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("Hourly", response_data);
                msg.setData(bundle);
                if (null != handler)
                    handler.sendMessage(msg);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    */
        Message msg = new Message();
        msg.what =  2;
        Bundle bundle = new Bundle();
        bundle.putString("Hourly", JsonFileHelper.getJsonString(m_context, "data1"));
        bundle.putString("name", area.getName());
        msg.setData(bundle);
        if (null != handler)
            handler.sendMessage(msg);
    }

    public void getDailyWeather(Area area, Handler handler) {
        String location = area.getWeather_id().substring(2);
        HttpUtil.sendOkHttpRequest(m_context.getResources().getString(R.string.daily_weather_address) + location + "&key=" + key, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String response_data = response.body().string();

                Message msg = new Message();
                msg.what = 3;
                Bundle bundle = new Bundle();
                bundle.putString("Daily", response_data);
                bundle.putString("name", area.getName());
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
