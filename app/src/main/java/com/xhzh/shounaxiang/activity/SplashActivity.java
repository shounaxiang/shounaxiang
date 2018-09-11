package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xhzh.shounaxiang.R;


public class SplashActivity extends Activity {
    private ImageView iv_spalsh;
    private static final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        iv_spalsh = findViewById(R.id.iv_splash);
        iv_spalsh.setImageResource(R.drawable.iv_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if(!isFinishing()) startActivity(intent);
                finish();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
