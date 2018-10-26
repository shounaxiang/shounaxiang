package com.xhzh.shounaxiang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GoodsMoreInfoActivity extends AppCompatActivity {
    ImageView iv_back, iv_goods;
    TextView tv_goods_name, tv_goods_id, tv_goods_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_more_info);
        initView();
    }
    private void initView() {
        Intent intent = getIntent();
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickBackListener(this));
        tv_goods_name = findViewById(R.id.tv_goods_name);
        tv_goods_name.setText(intent.getStringExtra("Goods_name"));
        iv_goods = findViewById(R.id.iv_goods);
        Bitmap bitmap = intent.getParcelableExtra("bitmap");
        iv_goods.setImageBitmap(bitmap);
        tv_goods_id = findViewById(R.id.tv_goods_id);
        tv_goods_id.setText(intent.getStringExtra("Goods_id"));
        tv_goods_address = findViewById(R.id.tv_goods_address);
        tv_goods_address.setText(intent.getStringExtra("Goods_address"));
        NiceSpinner niceSpinner = findViewById(R.id.nice_spinner);
        final List<String> dataset = new LinkedList<>(Arrays.asList("未分类"));
        final MyDatabaseHelper helper = new MyDatabaseHelper(this, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            Cursor cursor = db.rawQuery("select Space_name from Space where User_id = "
                    + pref.getString("User_id", "3"), null);
            while (cursor.moveToNext()) {
                dataset.add(cursor.getString(cursor.getColumnIndex("Space_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tv_goods_address.setText(dataset.get(position));
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    db.execSQL("update Goods set Space_name = " + dataset.get(position)
                            + "where Space_id = "
                            + tv_goods_id.getText().toString(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
