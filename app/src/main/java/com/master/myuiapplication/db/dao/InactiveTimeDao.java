package com.master.myuiapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.master.myuiapplication.Utils.Timers;
import com.master.myuiapplication.db.converter.LocalDateTimeConverter;
import com.master.myuiapplication.db.entity.InactiveTimeEntity;

import org.threeten.bp.LocalDateTime;

import java.util.List;

@Dao
public interface InactiveTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInactiveTime(InactiveTimeEntity inactivetime);

    @Query("SELECT * FROM inactivetimes")
    LiveData<List<InactiveTimeEntity>> loadAllTimes();

    @Query("select * from inactivetimes where brushName = :bName")
    LiveData<List<InactiveTimeEntity>> loadBrushInactiveTimes(String bName);

    // delete all the data (when stale)
    @Query("DELETE FROM inactivetimes")
    void deleteAllInactives();

    // delete a particular syncTimes row
/*
    @Query("DELETE FROM inactivetimes WHERE brushName = :bName AND inactiveTime = :iTime")
    @TypeConverters(LocalDateTimeConverter.class)
    void deleteInactiveTimes(String bName, Timers iTime);
*/
    @Delete
    @TypeConverters(LocalDateTimeConverter.class)
    void deleteInactiveTimes(InactiveTimeEntity inactivetime);

}
