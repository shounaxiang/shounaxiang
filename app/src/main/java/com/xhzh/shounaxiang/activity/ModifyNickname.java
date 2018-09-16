package com.xhzh.shounaxiang.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.util.AppUtils;

public class ModifyNickname extends AppCompatActivity {
    EditText editText;
    TextView tv_complete;
    ImageView iv_del;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white));
        editText = findViewById(R.id.et_profile_input);
        tv_complete = findViewById(R.id.tv_complete);
        iv_del = findViewById(R.id.iv_del);
        String nickname = getIntent().getStringExtra(MainActivity.NICKNAME);
        editText.setText(nickname);
        editText.setHint(nickname);
        AppUtils.showSoftInputFromWindow(this, editText);
        if(TextUtils.isEmpty(editText.getText())) {
            tv_complete.setTextColor(getResources().getColor(R.color.font_gray));
            iv_del.setVisibility(View.INVISIBLE);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                work();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
            private void work() {
                if (TextUtils.isEmpty(editText.getText())) {
                    tv_complete.setTextColor(getResources().getColor(R.color.font_gray));
                    iv_del.setVisibility(View.INVISIBLE);
                }
                else {
                    tv_complete.setTextColor(getResources().getColor(R.color.font_black));
                    iv_del.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = editText.getText().toString().trim();
                if(nickname != null && nickname.length() >= 0) {
                    Intent intent = new Intent();
                    //toString must write
                    intent.putExtra(MainActivity.NICKNAME, editText.getText().toString());
                    setResult(MainActivity.MODIFY_NAME, intent);
                    finish();
                }
            }
        });
        findViewById(R.id.ll_top_nav_back).setOnClickListener(new OnClickBackListener(this));
    }

}
