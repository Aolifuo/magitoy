package com.mikasuki.mikaweather.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikasuki.mikaweather.R;
import com.mikasuki.mikaweather.model.RealTimeWeather;
import com.mikasuki.mikaweather.util.BackgroundPictureHelper;

import java.util.List;

public class ManageListAdapter extends RecyclerView.Adapter<ManageListAdapter.ViewHolder>{
    private List<RealTimeWeather> weatherList;
    private OnItemClickListener listener;

    public ManageListAdapter(List<RealTimeWeather> weatherList) {
        this.weatherList = weatherList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView name;
        TextView temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.smallback);
            name = (TextView) itemView.findViewById(R.id.area);
            temp = (TextView) itemView.findViewById(R.id.temp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RealTimeWeather weather = weatherList.get(position);
        /*
        holder.layout.setBackground(
                BackgroundPictureHelper.loadPicture(
                        holder.itemView.getContext(), weather.getNow().getObsTime().substring(11, 13)));

         */
        holder.name.setText(weather.getName());
        holder.temp.setText(weather.getNow().getTemp() + "℃");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
