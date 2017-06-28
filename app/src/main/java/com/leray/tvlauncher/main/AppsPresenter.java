package com.leray.tvlauncher.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.leray.tvlauncher.ImageLoader;
import com.leray.tvlauncher.R;
import com.leray.tvlauncher.model.AppItem;
import com.leray.tvlauncher.server.HttpServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leray on 2017/4/5.
 */

public class AppsPresenter implements AppsContract.Presenter {

    private static final String TAG = "AppsPresenter";

    private AppsContract.View mView;
    private Context context;

    public AppsPresenter(AppsContract.View mView, Context context) {
        this.mView = mView;
        this.context = context;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        queryAppList();
    }

    @Override
    public void queryAppList() {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppItem> appList = new ArrayList<>();
        List<String> popApps = Arrays.asList(AppConfig.POP_APP_PACKAGES);
        int i = 0;
        for (ApplicationInfo info : appInfoList) {
            String pkg = info.packageName;
            if (context.getPackageName().equals(pkg)) {
                continue;
            }
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            if (intent != null && popApps.contains(pkg)) {
                AppItem app = new AppItem();
                app.setImageUrl(IMAGE_URL[i % IMAGE_URL.length]);
                app.setPackageName(pkg);
                String name = info.loadLabel(pm).toString();
                int iconDrawable = info.icon;
//                int logoDrawable = info.logo;
                Drawable icon = info.loadIcon(pm);
                app.setAppIcon(iconDrawable);
                app.setIconDrawable(icon);
//                Log.i(TAG, "queryAppList: iconDrawable: " + iconDrawable + ", logoDrawable: " + logoDrawable);
                app.setName(name);
                app.setPosition(i++);

                appList.add(app);
//                Log.i(TAG, "queryAppList: app: " + app);
//                Log.i(TAG, "queryAppList: app: " + name + ", package: " + pkg);
                /*if (i == 5) {
                    break;
                }*/
            }
        }

        mView.showAppList(appList);
    }

    @Override
    public void openApp(AppItem app) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        if (intent != null) {
            Log.i(TAG, "openApp: start app: " + app.getPackageName());
            context.startActivity(intent);
        }
    }

    @Override
    public void changeBackground(int position) {
        ImageView imageView = mView.getBackgroud();
        if (imageView == null) {
            return;
        }
//        ImageLoader.loadImageCrossFade(BG_IMAGE_URL[position % BG_IMAGE_URL.length], imageView);
    }


        String[] BG_IMAGE_URL = { "file:///android_asset/images/d1.jpg", "file:///android_asset/images/d2.jpg",
            "file:///android_asset/images/d3.jpg", "file:///android_asset/images/d4.jpg",
            "file:///android_asset/images/d5.jpg", "file:///android_asset/images/d6.jpg" };

    String[] IMAGE_URL = { "file:///android_asset/images/f1.jpg", "file:///android_asset/images/f2.jpg",
            "file:///android_asset/images/f3.jpg", "file:///android_asset/images/f4.jpg",
            "file:///android_asset/images/f5.jpg", "file:///android_asset/images/f6.jpg" };

    final int[] COLORS = { R.color.red, R.color.amber, R.color.blue, R.color.cyan,
            R.color.deep_orange, R.color.deep_purple, R.color.green,
            R.color.orange, R.color.pink, R.color.teal, R.color.lime };


}
