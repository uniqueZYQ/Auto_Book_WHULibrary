package com.example.autolibrary;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AboutTime {
    private static long SingleDayMillis=1000*60*60*24;

    public String getToday(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        return time;
    }

    public String getTomorrow(){
        long timecurrentTimeMillis=System.currentTimeMillis()+SingleDayMillis;
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        return time;
    }

    public int getHour(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("HH", Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        return Integer.valueOf(time).intValue();
    }

    public int getMinute(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("mm", Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        return Integer.valueOf(time).intValue();
    }
}
