package com.mikasuki.mikaweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.DailyWeather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.ViewHolder>{
    private List<DailyWeather.Daily> dailyList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTime;
        TextView week;
        TextView low;
        TextView high;

        public ViewHolder(View itemView) {
            super(itemView);
            dayTime = (TextView) itemView.findViewById(R.id.day_time);
            week = (TextView) itemView.findViewById(R.id.week);
            low = (TextView) itemView.findViewById(R.id.low_temp);
            high = (TextView) itemView.findViewById(R.id.high_temp);
        }
    }

    public DailyListAdapter(List<DailyWeather.Daily> list) {
        dailyList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyWeather.Daily daily = dailyList.get(position);
        holder.dayTime.setText(daily.getFxDate().substring(5));
        holder.low.setText(daily.getTempMin());
        holder.high.setText(daily.getTempMax());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String week = "";
        try {
            Date date = format.parse(daily.getFxDate());
            SimpleDateFormat sdf = new SimpleDateFormat("E");
            week = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.week.setText(week);
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

}
