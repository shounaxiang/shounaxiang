package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.view.View;

public class OnClickBackListener implements View.OnClickListener {
    private  Activity activity;
    public OnClickBackListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.finish();
    }
}
