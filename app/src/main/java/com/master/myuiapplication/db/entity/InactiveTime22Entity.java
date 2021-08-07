package com.master.myuiapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.converter.LocalDateTimeConverter;

/**
 * Entity class that represents the database
 */

@Entity(tableName = "inactivetimes", primaryKeys = {"brushName", "inactiveTime"})
@TypeConverters(LocalDateTimeConverter.class)
public class InactiveTime22Entity {

    @NonNull
    private String brushName;   // brush BLE device name

    @NonNull
    private Timers inactiveTime;    // Start - End inactive time

    public InactiveTime22Entity() {
    }

    @Ignore
    public InactiveTime22Entity(@NonNull String bName, @NonNull Timers iTime) {
        this.brushName = bName;
        this.inactiveTime = iTime;
    }


    public @NonNull String getBrushName() {
        return this.brushName;
    }

    public @NonNull Timers getInactiveTime() {
        return this.inactiveTime;
    }

    public void setBrushName(@NonNull String bName) {
        this.brushName = bName;
    }
    public void setInactiveTime(@NonNull Timers iTime) {
        this.inactiveTime = iTime;
    }

}
