package com.xhzh.shounaxiang.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.xhzh.shounaxiang.R;

public class ModifyAddressActivity extends AppCompatActivity {
    GridView gv_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_address);
        initView();
    }
    private void initView() {
        gv_address = findViewById(R.id.gv_address);

    }
}
