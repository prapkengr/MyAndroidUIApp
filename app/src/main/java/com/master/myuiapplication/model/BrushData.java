package com.master.myuiapplication.model;


import org.threeten.bp.LocalDateTime;

public interface BrushData {
    LocalDateTime getLocalDateTime();

    String getBrushName();

//    byte[] getBrushValue();
    Integer getBrushValue();
}
