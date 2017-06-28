package com.leray.tvlauncher.main;

import android.widget.ImageView;

import com.leray.tvlauncher.base.BasePresenter;
import com.leray.tvlauncher.base.BaseView;
import com.leray.tvlauncher.model.AppItem;

import java.util.List;

/**
 * Created by leray on 2017/4/5.
 */

public interface AppsContract {

    interface Presenter extends BasePresenter {
        void queryAppList();

        void openApp(AppItem app);

        void changeBackground(int position);
    }

    interface View extends BaseView<Presenter> {
        void showAppList(List<AppItem> list);

        ImageView getBackgroud();
    }
}
