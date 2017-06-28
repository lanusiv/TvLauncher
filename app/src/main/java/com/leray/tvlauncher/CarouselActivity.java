package com.leray.tvlauncher;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.leray.carousel.controls.Carousel;
import com.leray.carousel.controls.CarouselAdapter;
import com.leray.tvlauncher.main.AppsFragment;
import com.leray.tvlauncher.main.AppsPresenter;
import com.leray.tvlauncher.model.AppItem;

import java.util.ArrayList;
import java.util.List;

public class CarouselActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);

        AppsFragment appsFragment = AppsFragment.newInstance();
        AppsPresenter appsPresenter = new AppsPresenter(appsFragment, getApplicationContext());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, appsFragment)
                .commit();
//        appsPresenter.start();
    }

//    String[] IMAGE_URL = { "file:///android_asset/images/d1.jpg", "file:///android_asset/images/d2.jpg",
//            "file:///android_asset/images/d3.jpg", "file:///android_asset/images/d4.jpg",
//            "file:///android_asset/images/d5.jpg", "file:///android_asset/images/d6.jpg" };
    String[] IMAGE_URL = { "file:///android_asset/images/f1.jpg", "file:///android_asset/images/f2.jpg",
            "file:///android_asset/images/f3.jpg", "file:///android_asset/images/f4.jpg",
            "file:///android_asset/images/f5.jpg", "file:///android_asset/images/f6.jpg" };

    private List<AppItem> getAppList() {
        List<AppItem> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            AppItem item = new AppItem(IMAGE_URL[i], "Name", "", i);
            list.add(item);
        }
        return list;
    }
}
