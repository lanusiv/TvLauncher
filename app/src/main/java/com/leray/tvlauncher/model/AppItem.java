package com.leray.tvlauncher.model;

import android.graphics.drawable.Drawable;

/**
 * Created by leray on 2017/4/4.
 */

public class AppItem {
    private String imageUrl;
    private String name;
    private String packageName;
    private int position;
    private int appIcon;
    private Drawable iconDrawable;

    public AppItem() {
    }

    public AppItem(String imageUrl, String name, String packageName, int position) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.packageName = packageName;
        this.position = position;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppItem{" +
                "packageName='" + packageName + '\'' +
                '}';
    }
}
