package com.example.autolibrary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class AutoBookService extends Service {
    private StartTime startTime=new StartTime();
    private List<StartTime> startTimeList;
    private String contentText,cookie,t_date;
    private int timing_hour,timing_minute,now_hour,now_minute,login_time;
    private static String CHANNEL_ONE_ID = "AutoLibrary";
    private static String CHANNEL_ONE_NAME = "Channel One";
    private HttpUtils httpUtils=new HttpUtils();
    private UserPreference userPreference;
    private Seat2ID seat2ID=new Seat2ID();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.ACTION_TIME_TICK.equals(intent.getAction())){
                judgeAutoBook(context);
            }
        }
    };

    public AutoBookService() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        List<StartTime> startTimeList=DataSupport.order("id desc").find(StartTime.class);
        if(startTimeList.size()!=0)
            contentText="定时抢座功能已启动,当前定时"+startTimeList.get(0).getHour()+":"+startTimeList.get(0).getMinute();
        else
            contentText="定时抢座功能已启动,当前暂无定时";

        NotificationChannel notificationChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        registerReceiver(broadcastReceiver, filter);
        Notification notification=new Notification.Builder(this)
                .setChannelId(CHANNEL_ONE_ID)
                .setContentTitle("制霸图书馆")
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this.getBaseContext(),"AutoLibrary:自动预约服务终止,若需要此服务,请重启App",Toast.LENGTH_LONG).show();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification=new Notification.Builder(this)
                .setChannelId(CHANNEL_ONE_ID)
                .setContentTitle("制霸图书馆")
                .setContentText("定时抢座功能已终止,若需要此服务,请重启App")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        manager.notify(1,notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void judgeAutoBook(Context context){
        startTimeList=DataSupport.order("id desc").find(StartTime.class);
        if(startTimeList.size()!=0) {
            AboutTime aboutTime = new AboutTime();
            now_hour = aboutTime.getHour();
            now_minute = aboutTime.getMinute();
            startTime = startTimeList.get(0);
            timing_hour = startTime.getHour();
            timing_minute = startTime.getMinute();
            if (now_hour == timing_hour && now_minute == timing_minute) {
                userPreference=DataSupport.order("id desc").find(UserPreference.class).get(0);
                BookNow(context,userPreference.getSub_id()
                        ,userPreference.getPw()
                        ,userPreference.getDate()
                        ,seat2ID.getSeatID(userPreference.getRoom(), userPreference.getSeat())
                        ,String.valueOf(420+30*userPreference.getStart())
                        ,String.valueOf(450+30*userPreference.getEnd())
                );
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void BookNow(Context context, String t_id, String t_pw, int date, String t_seat, String t_start, String t_end){
        if(login_time<=2) {
            httpUtils.sendLoginRequest(t_id, t_pw, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context, "系统错误:step 1 work failed", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    AboutTime aboutTime = new AboutTime();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("status") == true) {
                            login_time = 0;
                            cookie = response.headers().get("Set-Cookie").substring(11, 43);
                            if (date == 1)
                                t_date = aboutTime.getToday();
                            else
                                t_date = aboutTime.getTomorrow();
                            httpUtils.saveSeats(t_seat, t_date, t_start, t_end, cookie, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(context, "step 2 work failed", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = response.body().string().replace("\\\"", "\"").substring(1);
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(result);
                                        if (jsonObject1.getString("status").equals("success")) {
                                            JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("data"));
                                            sendNotification("预约成功," + jsonObject2.getString("location") + " " + jsonObject2.getString("onDate") + jsonObject2.getString("begin") + "~" + jsonObject2.getString("end"));
                                        } else {
                                            sendNotification(jsonObject1.getString("message") + "!");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(context, "系统错误:step 2 json parse failed", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            });
                        } else if (jsonObject.getString("content").equals("绑定账号失败，请重试")) {
                            login_time++;
                            BookNow(context,t_id,t_pw,date,t_seat,t_start,t_end);
                        } else {
                            login_time = 0;
                            sendNotification(jsonObject.getString("content"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(context, "系统错误:step 1 json parse failed", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            });
        }
        else {
            sendNotification("连续模拟登录失败,请稍后再试");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(String text){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);

        Notification notification=new Notification.Builder(this)
                .setChannelId(CHANNEL_ONE_ID)
                .setContentTitle("制霸图书馆")
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        manager.notify(1,notification);
    }
}