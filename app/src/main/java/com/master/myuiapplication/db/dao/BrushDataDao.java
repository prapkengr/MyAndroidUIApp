package com.master.myuiapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.master.myuiapplication.db.converter.LocalDateTimeConverter;
import com.master.myuiapplication.db.entity.BrushDataEntity;

import org.threeten.bp.LocalDateTime;

import java.util.List;

@Dao
public interface BrushDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataVal(BrushDataEntity datavalue);

    @Query("SELECT * FROM brushdata")
    LiveData<List<BrushDataEntity>> loadAllData();

    @Query("SELECT DISTINCT brushName FROM brushdata")
    LiveData<List<String>> loadBrushNames();

    @Query("select * from brushdata where localDateTime = :lDateTime")
    @TypeConverters(LocalDateTimeConverter.class)
    LiveData<BrushDataEntity> loadProduct(LocalDateTime lDateTime);

    @Query("SELECT localDateTime FROM brushdata")
    @TypeConverters(LocalDateTimeConverter.class)
    LiveData<List<LocalDateTime>> loadAllDateTime();

    @Query("SELECT localDateTime FROM brushdata WHERE brushName = :bName")
    @TypeConverters(LocalDateTimeConverter.class)
    List<LocalDateTime> loadBrushDateTime(String bName);

    // get the Brush data for a given day for given brush
//    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime > datetime('now', '-1 day')")
//    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime <= datetime(:lDT) and localDateTime >= datetime(:lDT, '-24 hours')")
//    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime >= date(:lDT, 'localtime', '-1 day')")
    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime <= date(:lDT, 'localtime') and localDateTime >= date(:lDT, 'localtime', '-1 day')")
    @TypeConverters(LocalDateTimeConverter.class)
    LiveData<List<BrushDataEntity>> loadDaysBrushData(String bName, LocalDateTime lDT);


    // get only the Brush Values for a week for a given brush name
    @Query("SELECT brushValue FROM brushdata WHERE brushName = :bName and localDateTime > datetime('now', 'localtime', '-7 days')")
    @TypeConverters(LocalDateTimeConverter.class)
    LiveData<List<Integer>> loadWeeksBrushValues(String bName);
//    LiveData<List<byte[]>> loadWeeksBrushValues(String bName);

    // get the Brush Data Entity for a week for a given brush name
    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime > datetime('now', 'localtime', '-7 days')")
    @TypeConverters(LocalDateTimeConverter.class)
    LiveData<List<BrushDataEntity>> loadWeeksBrushData(String bName);
/*
    @Query("SELECT * FROM brushdata WHERE brushName = :bName and localDateTime > datetime('now', '-7 days')")
    List<BrushDataEntity> loadWeeksBrushData(String bName);
*/

    // delete all the data (when stale)
    @Query("DELETE FROM brushdata")
    void deleteAll();

    // delete a particular syncTimestamp row
    @Query("DELETE FROM brushdata WHERE localDateTime = :lDateTime")
    @TypeConverters(LocalDateTimeConverter.class)
    void deleteByTimestamp(LocalDateTime lDateTime);

    // delete all the rows older than 7 days
    @Query("DELETE FROM brushdata WHERE localDateTime <= date('now', 'localtime', '-7 days')")
    @TypeConverters(LocalDateTimeConverter.class)
    void deleteByTimeWeekOld();

/*
    @Delete
    void deleteByTimestamp(LocalDateTime lDateTime);
*/

}
