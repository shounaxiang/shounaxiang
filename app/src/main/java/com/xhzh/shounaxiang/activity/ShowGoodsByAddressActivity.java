package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowGoodsByAddressActivity extends AppCompatActivity {
    GridView gv_query_goods;
    private SimpleAdapter goods_adapter;
    private List<Map<String, Object>> goods_list;
    private MyDatabaseHelper helper;
    SharedPreferences pref;
    Intent intent;
    TextView tv_classify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_goods_by_address);
        initView();
        initData();
    }
    private void initView() {
        intent = getIntent();
        tv_classify = findViewById(R.id.tv_classify);
        tv_classify.setText("分类查看-" + intent.getStringExtra("address_name"));
        goods_list = new ArrayList<Map<String, Object>>();
        helper = new MyDatabaseHelper(this, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        gv_query_goods = findViewById(R.id.gv_query_goods);
        pref = getSharedPreferences("user", MODE_PRIVATE);
    }
    private void initData() {
        goods_list.clear();
        List<String> imgs = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        List<String> addrs = new ArrayList<String>();
        List<String> goods_id = new ArrayList<String>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Goods where User_id = '"
                    + pref.getString("User_id", "3")
                    + "' and Goods_path = '" + intent.getStringExtra("address_name") + "'", new String[]{});
            while(cursor.moveToNext()) {
                imgs.add(cursor.getString(cursor.getColumnIndex("Goods_img")));
                names.add(cursor.getString(cursor.getColumnIndex("Goods_name")));
                addrs.add(cursor.getString(cursor.getColumnIndex("Goods_path")));
                goods_id.add(cursor.getString(cursor.getColumnIndex("Goods_id")));
            }
            db.close();
            // Toast.makeText(this, cursor.getCount() + "", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] from = {"iv_query_goods", "tv_query_goods_name", "tv_query_goods_address", "tv_goods_id"};
        for (int i = 0; i < names.size(); ++i) {
            addData2GoodsList(imgs.get(i), names.get(i), addrs.get(i), goods_id.get(i));
        }

        int[] to = {R.id.iv_query_goods, R.id.tv_query_goods_name, R.id.tv_query_goods_address, R.id.tv_goods_id};
        goods_adapter = new SimpleAdapter(this, goods_list,
                R.layout.body_query_goods_item, from, to);
        goods_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String arg2) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                } if(view instanceof ImageView && data instanceof String) {
                    ImageView iv = (ImageView)view;
                    String url = "http://139.199.38.177/php/XHZH/PicturesProfile/" + data + ".JPG";
                    Glide.with(getActivity())
                            .load(url)
                            .into(iv);
                    return true;
                } else {
                    return false;
                }

            }
        });
        gv_query_goods.setAdapter(goods_adapter);
        gv_query_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_goods_id = view.findViewById(R.id.tv_goods_id);
                TextView tv_goods_name = view.findViewById(R.id.tv_query_goods_name);
                TextView tv_goods_address = view.findViewById(R.id.tv_query_goods_address);
                ImageView iv_query_goods = view.findViewById(R.id.iv_query_goods);
                Toast.makeText(getActivity(), tv_goods_id.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), GoodsMoreInfoActivity.class);
                intent.putExtra("Goods_id", tv_goods_id.getText().toString());
                intent.putExtra("Goods_name", tv_goods_name.getText().toString());
                intent.putExtra("Goods_address", tv_goods_address.getText().toString());
                //intent.putExtra("Goods_img", ((BitmapDrawable)iv_query_goods.getDrawable()).getBitmap());
                Bitmap bitmap = ((GlideBitmapDrawable)iv_query_goods.getDrawable()).getBitmap();
                intent.putExtra("bitmap", bitmap);
                getActivity().startActivity(intent);
            }
        });

    }
    private void addData2GoodsList(String img_name, String Goods_name, String Goods_path, String Goods_id) {
        String[] from = {"iv_query_goods", "tv_query_goods_name", "tv_query_goods_address", "tv_goods_id"};
        Map<String, Object> map=new HashMap<String, Object>();
        map.put(from[0], img_name);
        map.put(from[1], Goods_name);
        map.put(from[2], Goods_path);
        map.put(from[3], Goods_id);
        goods_list.add(0, map);
    }

    private Activity getActivity() {
        return this;
    }
}
