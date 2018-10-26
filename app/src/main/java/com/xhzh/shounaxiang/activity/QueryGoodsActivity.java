package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.bumptech.glide.Glide;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.AudioSoundRecognizer;
import com.xhzh.shounaxiang.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class QueryGoodsActivity extends AppCompatActivity {
    ImageView iv_back, iv_query, iv_goods;
    EditText et_query_goods;
    LinearLayout ll_query_goods_item;
    TextView tv_query_goods_name, tv_goods_id;
    TextView tv_query_goods_address;
    Button btn_voice_input;
    AudioSoundRecognizer recognizer;
    MyDatabaseHelper helper;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_goods);
        initView();
        //AppUtils.showSoftInputFromWindow(this, et_query_goods);

    }
    void initView() {
        activity = this;
        helper = new MyDatabaseHelper(this, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        recognizer = getRecognizer();
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickBackListener(this));
        et_query_goods = findViewById(R.id.et_query_goods);
        iv_query = findViewById(R.id.iv_query);
        iv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = helper.getReadableDatabase();
                InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                try {
                    String goods_name = et_query_goods.getText().toString().trim();
                    Cursor cursor = db.rawQuery("select * from Goods where Goods_name = '" + goods_name + "'", null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String id = cursor.getString(cursor.getColumnIndex("Goods_id"));
                        tv_goods_id.setText(id);
                        String address = cursor.getString(cursor.getColumnIndex("Goods_path"));
                        tv_query_goods_address.setText(address);
                        String img_name = cursor.getString(cursor.getColumnIndex("Goods_img")) + ".JPG";
                        tv_query_goods_name.setText(img_name);
                        Glide.with(activity).load("http://139.199.38.177/php/XHZH/PicturesProfile/"
                                + img_name).into(iv_goods);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }

            }
        });
        ll_query_goods_item = findViewById(R.id.ll_query_goods_item);
        iv_goods = ll_query_goods_item.findViewById(R.id.iv_query_goods);
        tv_query_goods_name = ll_query_goods_item.findViewById(R.id.tv_query_goods_name);
        tv_goods_id = ll_query_goods_item.findViewById(R.id.tv_goods_id);
        tv_query_goods_address = ll_query_goods_item.findViewById(R.id.tv_query_goods_address);
        btn_voice_input = findViewById(R.id.btn_voice_input);
        btn_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recognizer.isRecognizing()) {
                    btn_voice_input.setText("语音输入");
                    recognizer.stopRecognize();
                }
                else {
                    btn_voice_input.setText("识别中...点击停止");
                    recognizer.startRecognize();
                }
            }
        });
    }

    public AudioSoundRecognizer getRecognizer() {
        if (recognizer == null) {
            recognizer = new AudioSoundRecognizer(
                    this,
                    new NlsListener() {
                        @Override
                        public void onRecognizingResult(int i, NlsListener.RecognizedResult recognizedResult) {
                            if (i == 0)
                                try {
                                    JSONObject jsonObject = new JSONObject(recognizedResult.asr_out);
                                    String tmp = jsonObject.getString("result"); // 识别出来的的字符串
                                    et_query_goods.setText(tmp);
                                    //sendRecorder(tmp);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            //else Log.e(TAG, "onRecognizingResult: error: " + i);
                        }
                    },
                    new StageListener() {
                        @Override
                        public void onVoiceVolume(int i) {
                            //micDrawable.setLevel(i * 50 + LEVEL_INIT);
                        }
                    });
        }
        return recognizer;
    }
}
