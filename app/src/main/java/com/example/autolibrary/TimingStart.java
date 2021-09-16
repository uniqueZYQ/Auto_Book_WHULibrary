package com.example.autolibrary;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TimingStart {
    private Dialog dialog;
    private int hour_i=0,minute_i=0,hasTiming=0,init=0;
    private List<UserPreference> userPreferenceList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startTiming(Context context){
        FrameLayout root=(FrameLayout) LayoutInflater.from(context).inflate(R.layout.timing_start,null);
        //初始化视图
        dialog=new Dialog(context,R.style.dialog);
        dialog.setContentView(root);
        Window dialogWindow=dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        Spinner hour=root.findViewById(R.id.spin_hour);
        Spinner minute=root.findViewById(R.id.spin_minute);
        Button saveTiming=root.findViewById(R.id.button_SaveTiming);
        Button deleteTiming=root.findViewById(R.id.button_DeleteTiming);
        Button loopTiming=root.findViewById(R.id.button_LoopTiming);

        String CHANNEL_ONE_ID = "AutoLibrary";
        String CHANNEL_ONE_NAME = "Channel Two";
        NotificationChannel notificationChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        List<StartTime> startTimeList= DataSupport.order("id desc").find(StartTime.class);
        if(startTimeList.size()!=0)
            hasTiming=1;

        hour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasTiming==1&&init<2){
                    parent.setSelection(startTimeList.get(0).getHour());
                    init++;
                }
                hour_i=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        minute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(hasTiming==1&&init<2){
                    parent.setSelection(startTimeList.get(0).getMinute());
                    init++;
                }
                minute_i=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        loopTiming.setOnClickListener(v->{
            Toast.makeText(context, "该服务正在开发中,敬请期待", Toast.LENGTH_SHORT).show();//todo
        });

        saveTiming.setOnClickListener(v->{
            StartTime startTime=new StartTime();
            startTime.Save(hour_i,minute_i);
            userPreferenceList=DataSupport.order("id desc").find(UserPreference.class);
            if(userPreferenceList.size()!=0) {
                Toast.makeText(context, "定时抢设置成功!", Toast.LENGTH_SHORT).show();

                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                Notification notification = new Notification.Builder(context)
                        .setChannelId(CHANNEL_ONE_ID)
                        .setContentTitle("制霸图书馆")
                        .setContentText("定时抢座功能已启动,当前定时" + hour_i + ":" + minute_i)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .build();
                manager.notify(1, notification);
            }
            else{
                Toast.makeText(context, "请先保存偏好!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteTiming.setOnClickListener(v->{
            DataSupport.deleteAll(StartTime.class);
            Toast.makeText(context,"定时删除成功!",Toast.LENGTH_SHORT).show();

            NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

            Notification notification=new Notification.Builder(context)
                    .setChannelId(CHANNEL_ONE_ID)
                    .setContentTitle("制霸图书馆")
                    .setContentText("定时抢座功能已启动,当前暂无定时")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                    .build();
            manager.notify(1,notification);
        });

        dialog.show();
    }
}
