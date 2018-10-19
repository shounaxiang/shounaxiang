package com.xhzh.shounaxiang.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.util.AppUtils;

public class QueryGoodsActivity extends AppCompatActivity {
    ImageView iv_back;
    EditText et_query_goods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_goods);
        initView();
        AppUtils.showSoftInputFromWindow(this, et_query_goods);

    }
    void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickBackListener(this));
        et_query_goods = findViewById(R.id.et_query_goods);
    }
}
