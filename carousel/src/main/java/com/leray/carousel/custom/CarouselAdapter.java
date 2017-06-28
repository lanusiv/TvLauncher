package com.leray.carousel.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.leray.carousel.controls.Carousel;
import com.leray.carousel.controls.CarouselItem;

import java.util.ArrayList;
import java.util.List;

//Image adapter class for the Carousel
public abstract class CarouselAdapter extends BaseAdapter {

    private Context mContext;

    List<CarouselItem> mData;

    public CarouselAdapter(Context c) {
        mContext = c;
    }

    public void initData(List<CarouselItem> list) {
        this.mData = list;
    }


    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
//        CarouselItem item = getCarouselItem(position, convertView, parent);
//        mData.add(item);
        return mData.get(position % mData.size());
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public abstract CarouselItem getCarouselItem(int position, View convertView, ViewGroup parent);


    protected Context getContext() {
        return mContext;
    }

    @NonNull
    protected Bitmap getMirrorBitmap(int reflectionGap, Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit
        // reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Config.RGB_565);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
                null);

        // Create a shader that is a linear gradient that covers the
        // reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap,
                0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width,
                bitmapWithReflection.getHeight() + reflectionGap, paint);

        originalImage = bitmapWithReflection;
        return originalImage;
    }
}