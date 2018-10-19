package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.xhzh.shounaxiang.activity.QueryGoodsActivity;

/**
 * Created by wjsay on 2018/10/19
 * Describe:
 */
public class Query_OnClickListener implements View.OnClickListener {
    private Activity activity;
    public Query_OnClickListener(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(activity, QueryGoodsActivity.class);
        activity.startActivity(intent);
    }
}
