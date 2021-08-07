
package com.master.myuiapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.master.myuiapplication.db.converter.LocalDateTimeConverter;
import com.master.myuiapplication.model.BrushData;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


@Entity(tableName = "brushdata", primaryKeys = {"localDateTime","brushName"})
@TypeConverters(LocalDateTimeConverter.class)
public class BrushDataEntity implements BrushData {
//    @PrimaryKey
    @NonNull
    private LocalDateTime localDateTime = LocalDateTime.parse(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    // brush value timestamp
    @NonNull
    private String brushName = "DM Brush";   // brush BLE device name
//    private byte[] brushValue;  // brush run-time
    private Integer brushValue;  // brush run-time at this localDateTime hour

    @Override
    public @NonNull LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(@NonNull LocalDateTime  lDateTime) {
        this.localDateTime = lDateTime;
    }

    @Override
    public @NonNull String getBrushName() {
        return brushName;
    }

    public void setBrushName(@NonNull String bName) {
        this.brushName = bName;
    }

/*
    @Override
    public byte[] getBrushValue() {
        return brushValue;
    }

    public void setBrushValue(byte[] bValue) {
        this.brushValue = bValue;
    }
*/

    @Override
    public Integer getBrushValue() {
        return brushValue;
    }

    public void setBrushValue(Integer bValue) {
        this.brushValue = bValue;
    }

    public BrushDataEntity() {
    }

    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */
/*
    @Ignore
    public BrushDataEntity(@NonNull LocalDateTime lDateTime, @NonNull String bName, byte[] bValue) {
        this.localDateTime = lDateTime;
        this.brushName = bName;
        this.brushValue = bValue;
    }
*/
    @Ignore
    public BrushDataEntity(@NonNull LocalDateTime lDateTime, @NonNull String bName, Integer bValue) {
        this.localDateTime = lDateTime;
        this.brushName = bName;
        this.brushValue = bValue;
    }

    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */
    @Ignore
    public BrushDataEntity(BrushData product) {
        this.localDateTime = product.getLocalDateTime();
        this.brushName = product.getBrushName();
        this.brushValue = product.getBrushValue();
    }
}
