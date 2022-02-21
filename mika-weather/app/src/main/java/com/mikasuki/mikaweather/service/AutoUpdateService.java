package com.mikasuki.mikaweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.util.HttpUtil;
import com.mikasuki.mikaweather.util.WeatherInfoHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            SharedPreferences.Editor editor = getSharedPreferences("curweather", MODE_PRIVATE).edit();
            editor.putString("name", bundle.getString("name"));
            switch (msg.what) {
                case 1:
                    editor.putString("now", bundle.getString("RealTime"));
                    break;
                case 2:
                    editor.putString("hourly", bundle.getString("Hourly"));
                    break;
                case 3:
                    editor.putString("daily", bundle.getString("Daily"));
                    break;
                default:
                    break;
            }
            editor.apply();
            return true;
        }
    });

    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int interval = 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + interval;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = getSharedPreferences("curweather", MODE_PRIVATE);
        String weatherId = preferences.getString("weatherId", "");

        if ("".equals(weatherId))
            return;
        String name = preferences.getString("name", "");
        Area area = new Area();
        area.setName(name);
        area.setWeather_id(weatherId);
        WeatherInfoHelper weatherInfo = new WeatherInfoHelper(this, getString(R.string.qweather_key));
        weatherInfo.getRealTimeWeather(area, handler);
        weatherInfo.getHourlyWeather(area, handler);
        weatherInfo.getDailyWeather(area, handler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }


}