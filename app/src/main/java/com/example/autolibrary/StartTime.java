package com.example.autolibrary;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class StartTime extends DataSupport {
    private int id;
    private int hour;
    private int minute;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void Save(int hour,int minute){
        Connector.getDatabase();
        DataSupport.deleteAll(StartTime.class);
        StartTime startTime=new StartTime();
        startTime.setHour(hour);
        startTime.setMinute(minute);
        startTime.save();
    }
}
