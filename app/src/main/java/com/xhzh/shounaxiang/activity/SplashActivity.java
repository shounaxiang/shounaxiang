package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xhzh.shounaxiang.R;


public class SplashActivity extends Activity {
    private ImageView iv_spalsh;
    //延迟时间
    private static final int DELAY_TIME = 1000;
    Intent intent;

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
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                boolean login_state = pref.getBoolean("LOGIN_STATE", false);
                if (login_state) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                if (!isFinishing()) startActivity(intent);
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
