package com.leray.tvlauncher.app;

import android.app.Application;

import com.leray.tvlauncher.server.HttpServer;
import com.leray.tvlauncher.server.HttpServerService;

/**
 * Created by leray on 2017/4/4.
 */

public class TvApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpServerService.start(getApplicationContext());
    }
}
