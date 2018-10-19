package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.util.AppUtils;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyAddressActivity extends AppCompatActivity {
    GridView gv_address;
    ImageView iv_back;
    Button btn_add_address;
    private SimpleAdapter addr_adapter;
    private List<Map<String, Object>> addr_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_address);
        initView();
    }
    private void initView() {
        iv_back = this.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickBackListener(this));
        gv_address = findViewById(R.id.gv_address);
        final EditText et_input_address_name = new EditText(this);
        btn_add_address = findViewById(R.id.btn_add_address);
        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_edit();
            }
        });
        initData();

    }
    String[] from;
    private void initData() {
        addr_list = new ArrayList<Map<String, Object>>();
        from = new String[]{"tv_address_name",};
        int[] to = {R.id.tv_address_name};
        String[] addr_name = {"客厅", "卧室", "厨房", "庭院"};
        for (String name: addr_name) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(from[0], name);
            addr_list.add(map);
        }
        addr_adapter = new SimpleAdapter(this, addr_list,
                R.layout.body_modify_address_item, from, to);
        gv_address.setAdapter(addr_adapter);
        gv_address.setOnItemClickListener(new MyListener(this));
    }

    void alert_edit(){
        final EditText et = new EditText(this);
        et.setSingleLine();
        new AlertDialog.Builder(this).setTitle("空间昵称编辑")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        String new_address = et.getText().toString().trim();
                        work(new_address);
                    }
                }).setNegativeButton("取消",null).show();
    }
    void work(String address) {  // 添加位置的回调方法
        if (address == null || address.equals("")) {
            AppUtils.showShortToast("名字不能为空", this);
        }
        else {
            final String new_address = address;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(3000);
            RequestParams params = new RequestParams();
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            params.put("User_id", pref.getString("User_id", null));
            params.put("new_address", new_address);
            String url = null;
            client.post("", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    try {
                        JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                        boolean flag = json.getBoolean("flag");
                        if (flag) {
                            Map map = new HashMap<String, Object>();
                            map.put(from[0], new_address);
                            addr_list.add(0, map);
                            addr_adapter.notifyDataSetChanged();  // 数据变化时，哈哈
                            Toast.makeText(MainActivity.getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(MainActivity.getActivity(), "添加失败了", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class MyListener implements AdapterView.OnItemClickListener {
        private Activity activity;

        public MyListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            final ImageView iv_del = view.findViewById(R.id.iv_delete_address);
            final TextView tv_address = view.findViewById(R.id.tv_address_name);
            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle("提示");
                    String address_name = tv_address.getText().toString().trim();
                    dialog.setMessage("是否删除 " + address_name + " ？删除后，该空间下内所有内容的位置将会归为未定义");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //do nothing
                        }
                    });
                    dialog.show();
                }
            });

            //Toast.makeText(MainActivity.getActivity(), "" + id + " " + position, Toast.LENGTH_SHORT).show();
        }
    }

}
