package com.mikasuki.mikaweather.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mikasuki.mikaweather.model.Area;
import com.mikasuki.mikaweather.model.City;
import com.mikasuki.mikaweather.model.District;
import com.mikasuki.mikaweather.model.Province;
import com.mikasuki.mikaweather.model.RealTimeWeather;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class MikaWeatherDB {

    private final int version = 2;
    private static MikaWeatherDB mika_weather_db;
    private MikaWeatherDBHelper db_helper;
    private SQLiteDatabase db;

    private MikaWeatherDB(Context context) {
        db_helper = new MikaWeatherDBHelper(context, "MikaWeather", null, version);
        db = db_helper.getWritableDatabase();
    } //


    public static MikaWeatherDB getInstance(Context context) {
        if (mika_weather_db== null) {
            mika_weather_db = new MikaWeatherDB(context);
        }
        return mika_weather_db;
    }

    public void insertProvince(Province province) {
        db.execSQL("insert into Province(id, name) select ?, ? where not exists(select id from Province where id=?)",
                new Object[]{province.getId(), province.getName(), province.getId()});
    }

    public void insertCity(City city) {
        db.execSQL("insert into City(id, province_id, name) select ?, ?, ? where not exists(select id from City where id=?)",
                new Object[]{city.getId(), city.getProvince_id(), city.getName(), city.getId()});
    }

    public void insertDistrict(District district) {
        db.execSQL("insert into District(id, province_id, city_id, name, weather_id) select ?, ?, ?, ?, ? where not exists(select id from City where id=?)",
                new Object[]{district.getId(), district.getProvince_id(), district.getCity_id(), district.getName(), district.getWeather_id(), district.getId()});
    }

    public void insertManageCity(Area area) {
        db.execSQL("insert into Management(id, name, weather_id) select ?, ?, ? where not exists(select id from Management where id=?)",
                new Object[]{area.getId(), area.getName(), area.getWeather_id(), area.getId()});
    }

    public void insertCurrent(Area area) {
        db.execSQL("insert into Current(id, name, weather_id) select ?, ?, ? where not exists(select id from Current where id=?)",
                new Object[]{area.getId(), area.getName(), area.getWeather_id(), area.getId()});
    }

    public void updateCurrent(Area area) {
        db.execSQL("update Current set name=?, weather_id=? where id=?",
                new Object[]{area.getName(), area.getWeather_id(), area.getId()});
    }

    public List<Province> selectProvince() {
        List<Province> provinceList = new Vector<>();
        Cursor cursor = db.rawQuery("select * from Province", null);

        while (cursor.moveToNext()) {
            Province province = new Province();
            province.setLevel(0);
            province.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            province.setProvince_id(province.getId());
            province.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            provinceList.add(province);
        }
        cursor.close();

        return provinceList;

    }

    public List<City> selectCity(int provinceId) {
        List<City> cityList = new Vector<>();
        Cursor cursor = db.rawQuery("select * from City where province_id=" + provinceId, null);
        while (cursor.moveToNext()) {
            City city = new City();
            city.setLevel(1);
            city.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            city.setProvince_id(cursor.getInt(cursor.getColumnIndexOrThrow("province_id")));
            city.setCity_id(city.getId());
            city.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }

    public List<District> selectDistrict(int cityId) {
        List<District> districtList = new Vector<>();
        Cursor cursor = db.rawQuery("select * from District where city_id=" + cityId,  null);
        while (cursor.moveToNext()) {
            District district = new District();
            district.setLevel(2);
            district.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            district.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            district.setProvince_id(cursor.getInt(cursor.getColumnIndexOrThrow("province_id")));
            district.setCity_id(cursor.getInt(cursor.getColumnIndexOrThrow("city_id")));
            district.setWeather_id(cursor.getString(cursor.getColumnIndexOrThrow("weather_id")));
            districtList.add(district);
        }
        cursor.close();
        return districtList;
    }

    public List<Area> selectManageCity() {
        List<Area> list = new Vector<>();
        Cursor cursor = db.rawQuery("select * from Management", null);
        while (cursor.moveToNext()) {
            Area area = new RealTimeWeather();
            area.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            area.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            area.setWeather_id(cursor.getString(cursor.getColumnIndexOrThrow("weather_id")));
            list.add(area);
        }
        cursor.close();
        return list;
    }

    public Area selectCurrent() {
        Area area = new Area();
        area.setId(0);
        Cursor cursor = db.rawQuery("select * from Current", null);
        if (cursor.moveToNext()) {
            area.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            area.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            area.setWeather_id(cursor.getString(cursor.getColumnIndexOrThrow("weather_id")));
        }
        return area;
    }
}
