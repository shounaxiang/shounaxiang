package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.UploadImage;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.logging.Handler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wjsay on 2018/10/13
 * Describe:
 */
public class AddGoods_OnClickListener implements View.OnClickListener {
    private static ProgressDialog progressDialog;
    private static Activity activity;
    private static MyDatabaseHelper helper;
    public AddGoods_OnClickListener() {

    }
    public AddGoods_OnClickListener(Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("添加中……");
            progressDialog.setCancelable(false);
        }
        AddGoods_OnClickListener.activity = activity;
    }
    @Override
    public void onClick(View view) {
        progressDialog.show();
        helper = new MyDatabaseHelper(activity, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        String url = "http://112.74.109.111:8080/XHZH/Goods/add";
        RequestParams params = new RequestParams();
        final SharedPreferences pref = activity.getSharedPreferences("user", MODE_PRIVATE);
        final String User_id = pref.getString("User_id", "");
        final String Goods_name = ((EditText)(activity.findViewById(R.id.et_goods_name))).getText().toString().trim();
        final String image_name = "" + System.currentTimeMillis();
        params.put("User_id", User_id);
        params.put("name", Goods_name); //物品名字。都是字符串
        params.put("image_name", image_name); // 图片名
        params.put("path", "未归位"); // 物品位置
        new UploadImage(((BitmapDrawable)((ImageView)activity.findViewById(R.id.iv_new_goods)).getDrawable()).getBitmap(), image_name).execute();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                progressDialog.cancel();
                try {
                    JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                    boolean flag = json.getBoolean("flag");
                    try {
                        String Goods_id = json.getString("Goods_id");
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.execSQL("insert into Goods (Goods_id, User_id, Goods_name, Goods_img, Goods_path) values(?, ?, ?, ?, ?)",
                                new String[]{Goods_id, User_id, Goods_name, image_name, "未归位"});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (flag) Toast.makeText(activity, "添加成功", Toast.LENGTH_SHORT).show();
                    else AppUtils.showShortToast("插入数据库失败", activity);
                }
                catch (Exception e) {
                    AppUtils.showShortToast("解析异常", activity);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressDialog.cancel();
                // nested exception is org.springframework.dao.DataIntegrityViolationException
                AppUtils.showShortToast("404 or 500, maybe change your goods's name", activity);
                throwable.printStackTrace();
            }
        });
    }
}
