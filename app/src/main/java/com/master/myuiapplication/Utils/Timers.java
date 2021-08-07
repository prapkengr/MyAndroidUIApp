package com.master.myuiapplication.Utils;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.LocalDateTime;


public class Timers {

  private String startTime;
  private String endTime;

  public Timers(String startTime, String endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

    public String getEndTime() {
    return endTime;
    }

    public void setEndTime(String endTime) {
    this.endTime = endTime;
    }

    @Override public String toString() {
    return startTime + " - " + endTime; //e.g.   12:00 - 13:58
    }


}
