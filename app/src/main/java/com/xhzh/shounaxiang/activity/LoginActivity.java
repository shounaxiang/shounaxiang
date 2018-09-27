package com.xhzh.shounaxiang.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.PermUtil;

import org.apache.http.Header;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    ProgressDialog progressDialog_login;
    AutoCompleteTextView tv_user_name;
    EditText et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button ben_login = findViewById(R.id.ben_login);
        Button btn_forgot_register = findViewById(R.id.btn_forgot_register);
        tv_user_name = findViewById(R.id.tv_user_name);
        et_password = findViewById(R.id.tv_password);
        ben_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 测试
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //*/
                String tel = tv_user_name.getText().toString().trim();
                String pwd = et_password.getText().toString().trim();
                progressDialog_login = new ProgressDialog(LoginActivity.this);
                progressDialog_login.setMessage("登录中……");
                progressDialog_login.setCancelable(false);
                progressDialog_login.show();
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(3000);
                String url = "http://112.74.109.111:8080/XHZH/login/signIn";
                RequestParams params = new RequestParams();
                params.put("tel", tel);
                params.put("pwd", pwd);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        try {
                            progressDialog_login.cancel();
                            JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                            boolean flag = json.getBoolean("flag");
                            if (flag) {
                                User user = new Gson().fromJson(json.getString("User"),
                                        new TypeToken<User>(){}.getType());
                                //AppUtils.showShortToast(user.getUser_id(), LoginActivity.this);
                                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                // "User":{"User_id":111,"User_password":"111","User_img":"http://112.74.109.111:8080/images/none_head_img.png",
                                // "User_phone":"111","User_sex":"男","User_age":0}
                                editor.putString("User_id", user.getUser_id());
                                editor.putString("User_password", user.getUser_password());
                                editor.putString("User_img", user.getUser_img());
                                editor.putString("User_phone", user.getUser_phone());
                                editor.putString("User_sex", user.getUser_sex());
                                editor.putString("User_age", user.getUser_age());
                                editor.putString("User_name", user.getUser_name());
                                editor.putString("User_birthday", user.getUser_birthday());
                                editor.putBoolean("LOGIN_STATE", true);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                AppUtils.showShortToast("账户或密码错误", LoginActivity.this);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        progressDialog_login.cancel();
                        AppUtils.showShortToast("网络访问失败", LoginActivity.this);
                    }
                });
            }
        });
        btn_forgot_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
