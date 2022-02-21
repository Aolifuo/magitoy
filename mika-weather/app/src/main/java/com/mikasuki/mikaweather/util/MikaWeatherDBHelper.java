package com.mikasuki.mikaweather.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MikaWeatherDBHelper extends SQLiteOpenHelper {
    private final static String CREATE_PROVINCE_TABLE = "create table Province(" +
            "id integer primary key," +
            "name varchar(50))";

    private final static String CREATE_CITY_TABLE = "create table City(" +
            "id integer primary key," +
            "province_id integer," +
            "name varchar(50))";

    private final static String CREATE_DISTRICT_TABLE = "create table District(" +
            "id integer primary key," +
            "province_id integer," +
            "city_id integer," +
            "name varchar(50)," +
            "weather_id varchar(50))";

    private final static String CREATE_MANAGEMENT_TABLE = "create table Management(" +
            "id integer primary key," +
            "name varchar(50)," +
            "weather_id varchar(50))";

    private final static String CREATE_CURRENT_TABLE = "create table Current(" +
            "id integer primary key," +
            "name varchar(50)," +
            "weather_id varchar(50))";

    public MikaWeatherDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE_TABLE);
        db.execSQL(CREATE_CITY_TABLE);
        db.execSQL(CREATE_DISTRICT_TABLE);
        db.execSQL(CREATE_MANAGEMENT_TABLE);
        db.execSQL(CREATE_CURRENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_CURRENT_TABLE);
    }
}
