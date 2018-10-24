package com.xhzh.shounaxiang.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.Goods;
import com.xhzh.shounaxiang.dataclass.Spaces;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.DownloadImage;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends Activity {
    private static ProgressDialog progressDialog_login;
    AutoCompleteTextView tv_user_name;
    EditText et_password;
    private static Activity activity;
    MyDatabaseHelper dbHelper;
    private
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //progressDialog_login.cancel();
            switch (message.what) {
                case 0:
                    SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(3000);
                    String url = "http://112.74.109.111:8080/XHZH/Goods/queryGoodsByUseId";
                    RequestParams params = new RequestParams();
                    params.put("User_id", pref.getString("User_id", "3"));
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                                List<Goods> goods_list = new Gson().fromJson(json.getString("Goods"),
                                        new TypeToken<List<Goods>>(){}.getType());
                                //Log.e("LoginActivity", "onSuccess: " +goods_list.toString());
                                SQLiteDatabase db = dbHelper.getReadableDatabase();
                                for (Goods goods : goods_list) {
                                    try {
                                        db.execSQL("insert into Goods (Space_id, Space_name, User_id, Goods_id, Goods_img, " +
                                                        "Goods_path, Goods_name) values(?, ?, ?, ?, ?, ?, ?)",
                                                new String[]{goods.getSpace_id(), goods.getSpace_name(), goods.getUser_id(), goods.getGoods_id()
                                                    , goods.getGoods_img(), goods.getGoods_path(), goods.getGoods_name()});
                                        //new DownloadImage(goods.getGoods_img()).execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                if (progressDialog_login != null) progressDialog_login.cancel();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "写入数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                    String url_space = "http://112.74.109.111:8080/XHZH/space/querySpacesByUserId";
                    AsyncHttpClient client_space = new AsyncHttpClient();
                    client_space.setTimeout(3000);
                    client_space.post(url_space, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                                List<Spaces> space_list = new Gson().fromJson(json.getString("Spaces"),
                                        new TypeToken<List<Spaces>>() {
                                        }.getType());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                for (Spaces space : space_list) {
                                    try {
                                        db.execSQL("insert into Space (Space_id, Space_name, Space_img," +
                                                " Space_belong, Space_level, User_id) values(?, ?, ?, ?, ?, ?)",
                                                new String[]{space.getSpace_id(), space.getSpace_name(), space.getSpace_img(),
                                                        space.getSpace_belong(), space.getSpace_level(), space.getUser_id()});
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                    break;
                case 100:

                    break;

            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        dbHelper = new MyDatabaseHelper(this, "xhzh.db", null, 3);
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
                final RequestParams params = new RequestParams();
                params.put("tel", tel);
                params.put("pwd", pwd);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Message msg = new Message();
                        try {
                            //progressDialog_login.cancel();
                            JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                            boolean flag = json.getBoolean("flag");
                            if (flag) {
                                User user = new Gson().fromJson(json.getString("User"),
                                        new TypeToken<User>(){}.getType());
                                final String user_id = user.getUser_id();
                                //new DownloadImage(user.getUser_phone()).execute();
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
                                msg.what = 0;
                                handler.sendMessage(msg);
                            }
                            else {
                                if (progressDialog_login !=null) progressDialog_login.cancel();
                                AppUtils.showShortToast("账户或密码错误", LoginActivity.this);
                            }
                        }
                        catch (Exception e) {
                            if (progressDialog_login !=null) progressDialog_login.cancel();
                            AppUtils.showShortToast("异常", LoginActivity.this);
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        if (progressDialog_login !=null) progressDialog_login.cancel();
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
