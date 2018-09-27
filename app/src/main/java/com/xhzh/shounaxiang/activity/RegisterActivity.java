package com.xhzh.shounaxiang.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.util.AppUtils;

import org.apache.http.Header;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    Button btn_user_register;
    EditText et_phone_number;
    EditText et_password;
    EditText et_confirm_password;
    String phone_number;
    String password;
    String confirm_password;
    ProgressDialog progressDialog_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickBackListener(this));
        btn_user_register = findViewById(R.id.btn_user_register);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        et_phone_number.addTextChangedListener(new MyTextWatcher());
        et_password.addTextChangedListener(new MyTextWatcher());
        et_confirm_password.addTextChangedListener(new MyTextWatcher());
        btn_user_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!password.equals(confirm_password)) {
                    return;
                }
                progressDialog_register = new ProgressDialog(RegisterActivity.this);
                progressDialog_register.setMessage("请等待……");
                progressDialog_register.setCancelable(false);
                progressDialog_register.show();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("tel", phone_number);
                params.put("pwd", password);
                String url = "http://112.74.109.111:8080/XHZH/login/register";
                /* 测试
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //*/
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        try {
                            progressDialog_register.cancel();
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
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                AppUtils.showShortToast("注册失败，账户已存在", RegisterActivity.this);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        progressDialog_register.cancel();
                        AppUtils.showShortToast("网络访问失败", RegisterActivity.this);
                    }
                });
            }
        });

    }
    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            phone_number = et_phone_number.getText().toString().trim();
            password = et_password.getText().toString().trim();
            confirm_password = et_confirm_password.getText().toString().trim();
            if (phone_number.equals("") || password.equals("") || confirm_password.equals("")) {
                btn_user_register.setEnabled(false);
                btn_user_register.setBackgroundColor(getResources().getColor(R.color.button_color_gray_green));
            }
            else {
                btn_user_register.setEnabled(true);
                btn_user_register.setBackgroundColor(getResources().getColor(R.color.button_color_green));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
