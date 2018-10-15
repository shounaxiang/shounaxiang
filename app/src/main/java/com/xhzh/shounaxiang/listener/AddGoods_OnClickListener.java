package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.UploadImage;

import org.apache.http.Header;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wjsay on 2018/10/13
 * Describe:
 */
public class AddGoods_OnClickListener implements View.OnClickListener {
    private static ProgressDialog progressDialog;
    private static String image_name, path;
    private static EditText editText;
    private static Bitmap bitmap;
    private static Activity activity;
    public AddGoods_OnClickListener() {

    }
    public AddGoods_OnClickListener(Activity activity, EditText editText, String image_name, Bitmap bitmap, String path) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("添加中……");
            progressDialog.setCancelable(false);
        }
        AddGoods_OnClickListener.activity = activity;
        AddGoods_OnClickListener.editText = editText;
        AddGoods_OnClickListener.path = path;
        AddGoods_OnClickListener.image_name = image_name;
        AddGoods_OnClickListener.bitmap = bitmap;
    }
    @Override
    public void onClick(View view) {
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(3000);
        String url = "http://112.74.109.111:8080/XHZH/Goods/add";
        RequestParams params = new RequestParams();
        SharedPreferences pref = activity.getSharedPreferences("user", MODE_PRIVATE);
        params.put("User_id", pref.getString("User_id", ""));
        params.put("name", editText.getText().toString().trim()); //物品名字。都是字符串
        params.put("image_name", image_name); // 图片名
        params.put("path", path); // 物品位置
        new UploadImage(bitmap, image_name).execute();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                progressDialog.cancel();
                try {
                    JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                    boolean flag = json.getBoolean("flag");
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
