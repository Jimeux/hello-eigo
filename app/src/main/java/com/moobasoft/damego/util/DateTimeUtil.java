package com.moobasoft.damego.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    @NonNull
    public static String formatShortDate(Date date) {
        // Ugly regex to remove the year from the short date
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(
                DateFormat.SHORT, Locale.getDefault());
        sdf.applyPattern(
                sdf.toPattern().replaceAll("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""));
        return sdf.format(date);
    }

}
