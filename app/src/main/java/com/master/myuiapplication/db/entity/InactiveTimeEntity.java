package com.master.myuiapplication.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.converter.LocalDateTimeConverter;

import org.threeten.bp.LocalTime;

/**
 * Entity class that represents the database
 */

@Entity(tableName = "inactivetimes", primaryKeys = {"brushName", "inactiveTime"})
@TypeConverters(LocalDateTimeConverter.class)
public class InactiveTimeEntity {

    @NonNull
    private String brushName;   // brush BLE device name

    @NonNull
    private Timers inactiveTime;    // Start - End inactive time

    private boolean isEnabled;    // Timer is enabled or not

    public InactiveTimeEntity() {
    }

    @Ignore
    public InactiveTimeEntity(@NonNull String bName, @NonNull Timers iTime, boolean iEnabled) {
        this.brushName = bName;
        this.inactiveTime = iTime;
        this.isEnabled = iEnabled;
    }


    public @NonNull String getBrushName() {
        return this.brushName;
    }

    public @NonNull Timers getInactiveTime() {
        return this.inactiveTime;
    }

    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setBrushName(@NonNull String bName) {
        this.brushName = bName;
    }

    public void setInactiveTime(@NonNull Timers iTime) {
        this.inactiveTime = iTime;
    }

    public void setIsEnabled(boolean iEnabled) {
        this.isEnabled = iEnabled;
    }

}
