package com.mikasuki.mikaweather.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.City;
import com.mikasuki.mikaweather.model.District;
import com.mikasuki.mikaweather.model.Province;
import com.mikasuki.mikaweather.adapter.CityListAdapter;
import com.mikasuki.mikaweather.util.CitySelector;
import com.mikasuki.mikaweather.util.MikaWeatherDB;

import java.util.ArrayList;
import java.util.List;

public class CitySelectorActivity extends AppCompatActivity {

    private final int UPDATE_PROVINCE = 0;
    private final int UPDATE_CITY = 1;
    private final int UPDATE_DISTRICT = 2;
    private final int NEXT_LEVEL = 1;
    private final int PREV_LEVEL = -1;

    private String address = "http://guolin.tech/api/china";
    private int currentLevel = 0;
    private RecyclerView recyclerView;
    private List<Area> areaList = new ArrayList<>();
    private CityListAdapter adapter;
    private MikaWeatherDB db;
    private CitySelector citySelector;
    private TextView title;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_PROVINCE:
                    areaList.addAll(db.selectProvince());
                    adapter.notifyDataSetChanged();
                    break;
                case UPDATE_CITY:
                    areaList.addAll(db.selectCity(msg.getData().getInt("ProvinceId")));
                    adapter.notifyDataSetChanged();
                    break;
                case UPDATE_DISTRICT:
                    areaList.addAll(db.selectDistrict(msg.getData().getInt("CityId")));
                    adapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_city_selector);

        initView();
        //getCityDataFromWebToDB();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.textView2);
        Button cancel = (Button) findViewById(R.id.cancel);
        Button back_btn = (Button) findViewById(R.id.back_btn02);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaList.isEmpty()) {
                    finish();
                } else {
                    int currentLevel = areaList.get(0).getLevel() - 1;

                    if (currentLevel == -1) {
                        finish();
                    } else {
                        changeSelectorList(areaList.get(0), PREV_LEVEL);
                        adapter.notifyDataSetChanged();
                    }

                }
            }


        });

        db = MikaWeatherDB.getInstance(this);


        citySelector = new CitySelector(this);
        changeSelectorList(null, 0);
        adapter = new CityListAdapter(areaList);
        recyclerView = (RecyclerView) findViewById(R.id.selector_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CityListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (areaList.get(position).getLevel() == 2) {
                    returnAreaData(areaList.get(position));
                } else {
                    changeSelectorList(areaList.get(position), NEXT_LEVEL);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void changeSelectorList(Area area, int model) {

        if (area == null || model == 0) {
            title.setText("省");
            List<Province> provinceList = db.selectProvince();
            if (provinceList.size() == 0) {
                citySelector.loadProvinceToDB(address, handler);
            } else {
                areaList.addAll(provinceList);
            }
            return;
        }

        int currentLevel = area.getLevel() + model;
        if (currentLevel > 2){

            return;
        }
        areaList.clear();
        if (currentLevel == 0) {
            title.setText("省");
            List<Province> provinceList = db.selectProvince();
            if (provinceList.size() == 0) {
                citySelector.loadProvinceToDB(address, handler);
            } else {
                areaList.addAll(provinceList);
            }
        }
        else if (currentLevel == 1) {
            title.setText("市");
            List<City> cityList = db.selectCity(area.getProvince_id());
            if (cityList.size() == 0) {
                citySelector.loadCityToDB(address + "/" + area.getProvince_id(), handler, area);
            } else {
                areaList.addAll(cityList);
            }
        } else if (currentLevel == 2) {
            title.setText("区");
            List<District> districtList = db.selectDistrict(area.getCity_id());
            if (districtList.size() == 0) {
                citySelector.loadDistrictToDB(address + "/" + area.getProvince_id() + "/" + area.getCity_id(), handler, area);
            } else {
                areaList.addAll(districtList);
            }
        }

    }

    private void returnAreaData(Area area) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("id", area.getId());
        bundle.putString("name", area.getName());
        bundle.putString("weather_id", area.getWeather_id());
        intent.putExtras(bundle);
        setResult(1, intent);
        finish();
    }

}