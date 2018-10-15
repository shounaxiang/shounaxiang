package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.xhzh.shounaxiang.activity.ModifyAddressActivity;

/**
 * Created by wjsay on 2018/10/13
 * Describe:
 */
public class ModifyAddress_OnClickListener implements View.OnClickListener {
    private static Activity activity;
    public ModifyAddress_OnClickListener (Activity activity) {
        ModifyAddress_OnClickListener.activity = activity;
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(activity, ModifyAddressActivity.class);
        activity.startActivity(intent);
    }
}
