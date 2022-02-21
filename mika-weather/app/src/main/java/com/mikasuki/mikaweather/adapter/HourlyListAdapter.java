package com.mikasuki.mikaweather.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.HourlyWeather;

import java.util.List;

public class HourlyListAdapter extends RecyclerView.Adapter<HourlyListAdapter.ViewHolder> {
    private List<HourlyWeather.Hourly> hourlyList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView temp;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temp = (TextView) itemView.findViewById(R.id.hourly_temp);
            time = (TextView) itemView.findViewById(R.id.hourly_time);
        }
    }

    public HourlyListAdapter(List<HourlyWeather.Hourly> list) {
        hourlyList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HourlyWeather.Hourly hourly = hourlyList.get(position);
        holder.temp.setText(hourly.getTemp() + "℃");
        holder.time.setText(hourly.getFxTime().substring(11, 16));

    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }


}
