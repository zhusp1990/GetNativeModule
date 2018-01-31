package com.getnativemodule;

import android.app.Activity;
import android.app.Dialog;

import java.lang.ref.WeakReference;

public class SplashScreen {
    private static Dialog mSplashDialog;
    private static WeakReference<Activity> mActivity;

    public static void show(final Activity activity, final boolean fullScreen) {
        if (activity != null) {
            mActivity=new WeakReference<>(activity);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!activity.isFinishing()) {
                        mSplashDialog = new Dialog(activity);
                        mSplashDialog.setContentView(R.layout.layout_splash_screen);
                        mSplashDialog.setCancelable(false);
                        if (!mSplashDialog.isShowing()) {
                            mSplashDialog.show();
                        }
                    }
                }
            });
        }
    }

    /**
     * 关闭启动屏
     */
    public static void hide(Activity activity) {
        if (activity == null)
            activity = mActivity.get();

        if (activity == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSplashDialog != null && mSplashDialog.isShowing()) {
                    mSplashDialog.dismiss();
                }
            }
        });
    }

}
