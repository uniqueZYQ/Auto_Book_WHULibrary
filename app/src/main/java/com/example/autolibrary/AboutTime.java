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
}
