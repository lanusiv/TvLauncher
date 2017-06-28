package com.leray.tvlauncher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.leray.carousel.controls.CarouselItem;
import com.leray.carousel.custom.CarouselAdapter;
import com.leray.tvlauncher.model.AppItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by leray on 2017/4/4.
 */

public class AppCarouselAdapter extends CarouselAdapter {
    private static final String TAG = "AppCarouselAdapter";

    Bitmap originalImage;

    private List<AppItem> mData;

    public AppCarouselAdapter(Context context, List<AppItem> data) {
        super(context);
        mData = data;
        initData(getData(data));

        TypedArray images = getContext().getResources().obtainTypedArray(R.array.entries);
        originalImage = ((BitmapDrawable) images.getDrawable(0)).getBitmap();
    }

    public List<CarouselItem> getData(List<AppItem> data) {
        List<CarouselItem> carouselItems = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            final AppItem item = data.get(i);

            final CarouselItem carouselItem = new CarouselItem(getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1200, 1000);
            carouselItem.setLayoutParams(params);
//            carouselItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListener != null) {
//                        mListener.onItemClick(carouselItem, item.getPosition(), item, carouselItem.isSelected());
//                    }
//                }
//            });

            carouselItem.setIndex(i);
//            carouselItem.setBackgroundColor(getColor());
//            carouselItem.setImageBitmap(originalImage);
            String imageUrl = item.getImageUrl();
            ImageView imageView = carouselItem.getImageView();
            imageView.setLayoutParams(new FrameLayout.LayoutParams(1200, 800));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            TextView textView = carouselItem.getTextView();
            textView.setLayoutParams(new FrameLayout.LayoutParams(1200, 200, Gravity.BOTTOM));
            if (item.getIconDrawable() != null) {
                ImageLoader.loadImage(item.getIconDrawable(), imageView);
            } else {
                ImageLoader.loadImage(imageUrl, imageView);
            }
            carouselItem.setText(item.getName());

            carouselItems.add(carouselItem);
        }
        return carouselItems;
    }

    public AppItem getDataItem(int position) {
        if (mData == null) {
            return null;
        }
        if (position < 0 || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    private int getColor() {
        Random random = new Random();
        int index = random.nextInt(COLORS.length);
        return COLORS[index];
    }

    final int[] COLORS = { R.color.red, R.color.amber, R.color.blue, R.color.cyan,
            R.color.deep_orange, R.color.deep_purple, R.color.green,
            R.color.orange, R.color.pink, R.color.teal, R.color.lime };

//
//    public void setOnItemClickListener(OnItemClickListener mListener) {
//        this.mListener = mListener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position, AppItem item, boolean selected);
//    }

    /*@Override
    public CarouselItem getCarouselItem(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getCarouselItem() called with: position = [" + position + "], convertView = [" + convertView + "], parent = [" + parent + "]");
        AppItem appItem = mData.get(position);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(30, 40);
        CarouselItem item = new CarouselItem(getContext()*//*, params*//*);


        item.setIndex(position);
        item.setImageBitmap(originalImage);
        String imageUrl = appItem.getImageUrl();
        ImageView imageView = item.getImageView();
//        imageView.setLayoutParams(new FrameLayout.LayoutParams(480, 800));
//        ImageLoader.loadImage(imageUrl, imageView);
        item.setText(appItem.getName());
        return item;
    }*/
}
