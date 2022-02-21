package com.mikasuki.mikaweather.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.adapter.DailyListAdapter;
import com.mikasuki.mikaweather.adapter.HourlyListAdapter;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.DailyWeather;
import com.mikasuki.mikaweather.model.HourlyWeather;
import com.mikasuki.mikaweather.model.RealTimeWeather;

import com.mikasuki.mikaweather.service.AutoUpdateService;
import com.mikasuki.mikaweather.util.BackgroundPictureHelper;
import com.mikasuki.mikaweather.util.HttpUtil;
import com.mikasuki.mikaweather.util.MikaWeatherDB;
import com.mikasuki.mikaweather.util.WeatherInfoHelper;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;


public class MainActivity extends AppCompatActivity {

    private ConstraintLayout background;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView cityName;
    private TextView temperature;
    private TextView curWeather;
    private TextView sensibleTemp;
    private TextView windForce;
    private TextView windDir;
    private TextView humidity;
    private TextView precip;
    private TextView vis;
    private TextView pressure;
    private Area area = new Area();
    private WeatherInfoHelper weatherHelper;

    private List<HourlyWeather.Hourly> hourlyList = new ArrayList<>();
    private RecyclerView hourlyRecyclerView;
    private HourlyListAdapter hourlyListAdapter;

    private List<DailyWeather.Daily> dailyList = new ArrayList<>();
    private RecyclerView dailyRecyclerView;
    private DailyListAdapter dailyListAdapter;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 1:
                    updateRealTime(bundle.getString("RealTime"), bundle.getString("name"));
                    break;
                case 2:
                    updateHourly(bundle.getString("Hourly"));
                    break;
                case 3:
                    updateDaily(bundle.getString("Daily"));
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        background = (ConstraintLayout) findViewById(R.id.background);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        cityName = (TextView) findViewById(R.id.city_name);
        temperature = (TextView) findViewById(R.id.temperature);
        curWeather = (TextView) findViewById(R.id.weather);
        sensibleTemp = (TextView) findViewById(R.id.sensible_temp);
        windForce = (TextView) findViewById(R.id.wind_force);
        windDir = (TextView) findViewById(R.id.wind_dir);
        humidity = (TextView) findViewById(R.id.humidity);
        precip = (TextView) findViewById(R.id.precip);
        vis = (TextView) findViewById(R.id.vis);
        pressure = (TextView) findViewById(R.id.press);
        Button city_manage = (Button) findViewById(R.id.citymanage_btn);

        city_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityManagementActivity.class);
                startActivityForResult(intent, 1);

            }
        });

        weatherHelper = new WeatherInfoHelper(this, getResources().getString(R.string.qweather_key));
        MikaWeatherDB db = MikaWeatherDB.getInstance(this);

        hourlyListAdapter = new HourlyListAdapter(hourlyList);
        hourlyRecyclerView = (RecyclerView) findViewById(R.id.hourly_list);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        hourlyRecyclerView.setAdapter(hourlyListAdapter);

        dailyListAdapter = new DailyListAdapter(dailyList);
        dailyRecyclerView = (RecyclerView) findViewById(R.id.daily_list);
        dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyRecyclerView.setAdapter(dailyListAdapter);

        SharedPreferences pres = getSharedPreferences("curweather", MODE_PRIVATE);
        String name = pres.getString("name", null);

        if (null != name) {

            updateWeather(false);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateWeather(false);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 0) {

            }
            else if (resultCode == 1) {

                updateWeather(true);
            }
        }
    }

    private void updateWeather(boolean flag) {
        swipeRefreshLayout.setRefreshing(false);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        SharedPreferences pres = getSharedPreferences("curweather", MODE_PRIVATE);
        String name = pres.getString("name", "");
        String weatherId = pres.getString("weatherId", "");

        if (flag == true) {
            Area area = new Area();
            area.setName(name);
            area.setWeather_id(weatherId);
            weatherHelper.getRealTimeWeather(area, handler);
            weatherHelper.getHourlyWeather(area, handler);
            weatherHelper.getDailyWeather(area, handler);
            return;
        }

        if ("".equals(name))
            return;

        String now = pres.getString("now", "");
        if ("".equals(now))
            return;
        updateRealTime(now, name);

        String hourly = pres.getString("hourly", "");
        if ("".equals(hourly))
            return;
        updateHourly(hourly);

        String daily = pres.getString("daily", "");
        if ("".equals(daily))
            return;
        updateDaily(daily);

    }

    private void updateRealTime(String data, String name) {
        cityName.setText(name);
        Gson gson = new Gson();
        RealTimeWeather realTimeWeather = gson.fromJson(data, RealTimeWeather.class);
        background.setBackground(BackgroundPictureHelper.loadPicture(this, realTimeWeather.getNow().getObsTime().substring(11, 13)));

        temperature.setText(realTimeWeather.getNow().getTemp());
        curWeather.setText(realTimeWeather.getNow().getText());

        RealTimeWeather.Now now = realTimeWeather.getNow();
        sensibleTemp.setText(now.getFeelsLike() + "℃");
        windForce.setText(now.getWindScale() + "级");
        windDir.setText(now.getWindDir());
        humidity.setText(now.getHumidity() + "％");
        precip.setText(now.getPrecip());
        vis.setText(now.getVis() + "Km");
        pressure.setText(now.getPressure() + "hPa");

    }

    private void updateHourly(String data) {
        hourlyList.clear();
        Gson gson = new Gson();
        HourlyWeather hourlyWeather = gson.fromJson(data, HourlyWeather.class);
        hourlyList.addAll(hourlyWeather.getHourly());
        hourlyListAdapter.notifyDataSetChanged();

    }

    private void updateDaily(String data) {

        dailyList.clear();
        Gson gson = new Gson();
        DailyWeather dailyWeather = gson.fromJson(data, DailyWeather.class);
        dailyList.addAll(dailyWeather.getDaily());
        dailyListAdapter.notifyDataSetChanged();


    }

}