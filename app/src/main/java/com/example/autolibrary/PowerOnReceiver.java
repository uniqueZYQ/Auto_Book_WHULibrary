package com.example.autolibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,AutoBookService.class);
        context.startService(intent1);
    }
}