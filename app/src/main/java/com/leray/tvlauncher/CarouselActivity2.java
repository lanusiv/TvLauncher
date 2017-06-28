package com.leray.tvlauncher;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;

import com.leray.carousel.controls.Carousel;
import com.leray.carousel.controls.CarouselAdapter;
import com.leray.tvlauncher.model.AppItem;

import java.util.ArrayList;
import java.util.List;

public class CarouselActivity2 extends FragmentActivity implements Carousel.OnItemSelectedListener {

    private Carousel carousel;
//    private CarouselImageAdapter carouselImageAdapter;
    private AppCarouselAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);

        carousel = (Carousel) findViewById(R.id.carousel);

        carousel.setOnItemSelectedListener(this);

        adapter = new AppCarouselAdapter(getApplicationContext(), getAppList());
        carousel.setAdapter(adapter);

        carousel.requestFocus();

//        carousel.setSelection();
    }

    @Override
    public void onItemSelected(CarouselAdapter<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(CarouselAdapter<?> parent) {

    }

    @Override
    public void onItemRotate(View view, float angle) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
//                    moveToPrevious();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
//                    moveToNext();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void moveToPrevious() {
        if (carousel != null) {
            int selection = carousel.getSelectedItemPosition() + 1;
            if (selection >= carousel.getCount()) {
                selection = 0;
            }
            carousel.scrollToChild(selection);
        }
    }

    private void moveToNext() {
        if (carousel != null) {
            int selection = carousel.getSelectedItemPosition() - 1;
            if (selection < 0) {
                selection = carousel.getCount() - 1;
            }
            carousel.scrollToChild(selection);
        }
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
