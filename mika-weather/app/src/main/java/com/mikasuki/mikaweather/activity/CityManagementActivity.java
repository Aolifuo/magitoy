package com.mikasuki.mikaweather.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.adapter.ManageListAdapter;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.RealTimeWeather;
import com.mikasuki.mikaweather.util.HttpUtil;
import com.mikasuki.mikaweather.util.JsonFileHelper;
import com.mikasuki.mikaweather.util.MikaWeatherDB;
import com.mikasuki.mikaweather.util.WeatherInfoHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;

import okhttp3.Call;
import okhttp3.Response;

public class CityManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageListAdapter adapter;
    private List<Area> areaList;
    private List<RealTimeWeather> weatherList = new ArrayList<>();
    private WeatherInfoHelper weatherHelper;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    loadRealTimeWeather(bundle);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return true;
        }
    });;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_management);
        initView();
    }

    private void initView() {
        Button go_back = (Button) findViewById(R.id.back_btn01);
        Button add_city = (Button) findViewById(R.id.add_btn);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityManagementActivity.this, CitySelectorActivity.class);
                startActivityForResult(intent, 2);
            }
        });


        weatherHelper = new WeatherInfoHelper(this, getResources().getString(R.string.qweather_key));
        MikaWeatherDB db = MikaWeatherDB.getInstance(this);
        areaList = db.selectManageCity();
        for (Area area : areaList) {
            weatherHelper.getRealTimeWeather(area, handler);
        }

        adapter = new ManageListAdapter(weatherList);
        recyclerView = (RecyclerView) findViewById(R.id.management_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ManageListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                returnCityData(weatherList.get(position));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Debug", "Code:" + resultCode);

        if (requestCode == 2) {
            if (resultCode == 0) {
                Log.d("R", "cancel");

            } else if (resultCode == 1) {
                assert data != null;
                Bundle bundle = data.getExtras();
                Area area = new Area();
                area.setName(bundle.getString("name"));
                area.setId(bundle.getInt("id"));
                area.setWeather_id(bundle.getString("weather_id"));
                areaList.add(area);
                MikaWeatherDB db = MikaWeatherDB.getInstance(this);
                db.insertManageCity(area);
                weatherHelper.getRealTimeWeather(area, handler);
            }
        }
    }


    private void loadRealTimeWeather(Bundle bundle) {
        Gson gson = new Gson();
        RealTimeWeather realTimeWeather = gson.fromJson(bundle.getString("RealTime"), RealTimeWeather.class);
        realTimeWeather.setId(bundle.getInt("id"));
        realTimeWeather.setName(bundle.getString("name"));
        realTimeWeather.setWeather_id(bundle.getString("weather_id"));
        weatherList.add(realTimeWeather);
    }

    private void returnCityData(Area area) {
        SharedPreferences.Editor editor = getSharedPreferences("curweather", MODE_PRIVATE).edit();
        editor.putString("name", area.getName());
        editor.putString("weatherId", area.getWeather_id());
        editor.apply();
        setResult(1);
        finish();
    }
}