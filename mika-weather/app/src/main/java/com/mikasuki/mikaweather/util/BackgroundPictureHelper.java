package com.mikasuki.mikaweather.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.mikasuki.mikaweather.R;

public class BackgroundPictureHelper {
    public static Drawable loadPicture(Context context, String timeStr) {
        int time = Integer.parseInt(timeStr);

        if (time >= 6 && time < 11) {
            return ContextCompat.getDrawable(context, R.drawable.morning);
        }

        if (time >= 11 && time < 13) {
            return ContextCompat.getDrawable(context, R.drawable.noon);
        }

        if (time >= 13 && time < 16) {
            return ContextCompat.getDrawable(context, R.drawable.afternoon);
        }

        if (time >= 16 && time < 18) {
            return ContextCompat.getDrawable(context, R.drawable.evening);
        }

        if (time >= 18 && time < 22) {
            return ContextCompat.getDrawable(context, R.drawable.night1);
        }

        if (time >= 22 && time < 24) {
            return ContextCompat.getDrawable(context, R.drawable.night2);
        }

        return ContextCompat.getDrawable(context, R.drawable.night3);

    }
}
