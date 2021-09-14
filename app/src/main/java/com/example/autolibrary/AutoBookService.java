package com.example.autolibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoBookService extends Service {
    public AutoBookService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}