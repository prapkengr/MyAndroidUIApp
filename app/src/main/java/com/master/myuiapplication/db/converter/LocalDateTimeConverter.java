package com.master.myuiapplication.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.master.myuiapplication.Utils.Timers;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class LocalDateTimeConverter {

//    private static DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static LocalDateTime toDateTime(String datetimestamp) {
        return datetimestamp == null ? null : LocalDateTime.parse(datetimestamp);
    }

    @TypeConverter
    public static String toDateTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
//        return dateTime == null ? null : dateTime.format(dtf);
    }

    @TypeConverter
    public static Timers toTime(String timestamp) {
        if(timestamp == null){
            return null;
        } else {
            String[] separated = timestamp.split(" - ");
            return (new Timers(separated[0], separated[1]));
        }
    }

    @TypeConverter
    public static String toTimeString(Timers dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

}
