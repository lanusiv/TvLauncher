package com.leray.tvlauncher.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

public class HttpServerService extends Service {

    public static void start(Context context) {
        Intent i = new Intent(context, HttpServerService.class);
        context.startService(i);
    }

    public HttpServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HttpServer.start(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        HttpServer.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
