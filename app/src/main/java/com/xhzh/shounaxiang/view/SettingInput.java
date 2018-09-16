package com.xhzh.shounaxiang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.xhzh.shounaxiang.R;


public class SettingInput extends LinearLayout {
    public SettingInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_profile_info, this);
    }
}
