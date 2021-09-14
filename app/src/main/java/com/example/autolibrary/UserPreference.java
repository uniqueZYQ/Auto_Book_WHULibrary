package com.example.autolibrary;

import android.content.Context;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class UserPreference extends DataSupport {
    private int id;
    private String sub_id;
    private String pw;
    private int room;
    private int seat;
    private int start;
    private int end;
    private int date;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnd() {
        return end;
    }

    public int getRoom() {
        return room;
    }

    public int getStart() {
        return start;
    }

    public int getSeat() {
        return seat;
    }

    public String getPw() {
        return pw;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSub_id() {
        return sub_id;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public void Save(String sub_id, String pw, int room, int seat, int start, int end, int date,Context context){
        Connector.getDatabase();
        UserPreference userPreference=new UserPreference();
        userPreference.setEnd(end);
        userPreference.setPw(pw);
        userPreference.setRoom(room);
        userPreference.setSeat(seat);
        userPreference.setStart(start);
        userPreference.setSub_id(sub_id);
        userPreference.setDate(date);
        userPreference.save();
        Toast.makeText(context,"保存成功!",Toast.LENGTH_SHORT).show();
    }
}
