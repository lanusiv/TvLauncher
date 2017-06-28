package com.leray.tvlauncher.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leray.carousel.controls.Carousel;
import com.leray.carousel.controls.CarouselAdapter;
import com.leray.tvlauncher.AppCarouselAdapter;
import com.leray.tvlauncher.ImageLoader;
import com.leray.tvlauncher.R;
import com.leray.tvlauncher.base.BaseFragment;
import com.leray.tvlauncher.model.AppItem;

import java.util.List;

/**
 */
public class AppsFragment extends BaseFragment implements AppsContract.View,
        Carousel.OnItemSelectedListener, CarouselAdapter.OnItemClickListener {

    private static final String TAG = "AppsFragment";

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private ImageView bgImage;

    private Carousel carousel;
    private AppCarouselAdapter adapter;

    private AppsContract.Presenter mPresenter;

    public AppsFragment() {
        // Required empty public constructor
    }

    public static AppsFragment newInstance() {
        AppsFragment fragment = new AppsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apps, container, false);
        carousel = (Carousel) findViewById(R.id.carousel);
        bgImage = (ImageView) findViewById(R.id.background);

        ImageLoader.loadImage(R.drawable.bg_default, bgImage);

        carousel.setOnItemSelectedListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } /*else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setPresenter(AppsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showAppList(List<AppItem> list) {
        Log.d(TAG, "showAppList() called with: list = [" + list + "]");
        adapter = new AppCarouselAdapter(getActivity(), list);
        carousel.setAdapter(adapter);
//        carousel.requestFocus();
        carousel.setOnItemClickListener(this);
    }

    @Override
    public ImageView getBackgroud() {
        return bgImage;
    }

    @Override
    public void onItemSelected(CarouselAdapter<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected() called with: parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
//        view.requestFocus();
        mPresenter.changeBackground(position);
    }

    @Override
    public void onNothingSelected(CarouselAdapter<?> parent) {

    }

    @Override
    public void onItemRotate(View view, float angle) {
        Log.d(TAG, "onItemRotate() called with: view = [" + view + "], angle = [" + angle + "]");
    }

    @Override
    public void onItemClick(CarouselAdapter<?> parent, View view, int position, long id) {
        AppItem app = adapter.getDataItem(position);
        mPresenter.openApp(app);
    }

    /**
     *
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
