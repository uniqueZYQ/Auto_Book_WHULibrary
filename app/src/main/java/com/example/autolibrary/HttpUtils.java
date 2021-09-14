package com.example.autolibrary;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtils {
    private String lvt,lvpt;

    public void sendLoginRequest(String id,String pw,okhttp3.Callback callback){
        String Url="http://system.lib.whu.edu.cn/libseat-wechat/bundle";
        RequestBody formBody;
        formBody=new FormBody.Builder()
                .add("account",id)
                .add("password",pw)
                .add("linkSign","currentBook")
                .add("type","currentBook")
                .add("msg","")
                .build();

        RandomNum randomNum=new RandomNum();
        String jsessionID=randomNum.initHex();
        lvt=randomNum.initInt(100000000,199999999);
        lvpt=randomNum.initInt(100000000,199999999);

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody)
                .header("Cookie", "JSESSIONID="+jsessionID+";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d="+lvt+";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d="+lvpt).build();

        client.newCall(request).enqueue(callback);
    }

    public void saveSeats(String seatId,String date,String start,String end,String cookie,okhttp3.Callback callback){
        String Url="http://system.lib.whu.edu.cn/libseat-wechat/saveBook?seatId="+seatId+"&date="+date+"&start="+start+"&end="+end+"&type=1";

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).header("Cookie", "JSESSIONID="+cookie+";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d="+lvt+";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d="+lvpt).build();

        client.newCall(request).enqueue(callback);
    }

    public void getCurrentBook(String cookie,okhttp3.Callback callback){
        String Url="http://system.lib.whu.edu.cn/libseat-wechat/currentBook?linkSign=currentBook";

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).header("Cookie", "JSESSIONID="+cookie+";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d="+lvt+";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d="+lvpt).build();

        client.newCall(request).enqueue(callback);
    }

    public void CancelBook(String BookID,String cookie,okhttp3.Callback callback){
        String Url="http://system.lib.whu.edu.cn/libseat-wechat/cancleBook?bookId="+BookID;

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).header("Cookie", "JSESSIONID="+cookie+";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d="+lvt+";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d="+lvpt).build();

        client.newCall(request).enqueue(callback);
    }

    public void StopUse(String BookID,String cookie,okhttp3.Callback callback){
        String Url="http://system.lib.whu.edu.cn/libseat-wechat/stop?bookId="+BookID;

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).header("Cookie", "JSESSIONID="+cookie+";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d="+lvt+";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d="+lvpt).build();

        client.newCall(request).enqueue(callback);
    }

    public void getTomorrowCurrentBook(String cookie,okhttp3.Callback callback) {
        String Url = "http://system.lib.whu.edu.cn/libseat-wechat/getUserBookHistory";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(Url).header("Cookie", "JSESSIONID=" + cookie + ";Hm_lvt_c85582349490ddcb3b30224dc1c3b68d=" + lvt + ";Hm_lpvt_c85582349490ddcb3b30224dc1c3b68d=" + lvpt).build();

        client.newCall(request).enqueue(callback);
    }
}
