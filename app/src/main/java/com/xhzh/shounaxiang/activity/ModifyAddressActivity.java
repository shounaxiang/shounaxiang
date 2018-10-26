package com.xhzh.shounaxiang.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.listener.BottomMenuOnClinkListener;
import com.xhzh.shounaxiang.listener.OnClickBackListener;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.Constant;
import com.xhzh.shounaxiang.util.SaveGoodsImage;
import com.xhzh.shounaxiang.util.UploadImage;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifyAddressActivity extends AppCompatActivity {
    GridView gv_address;
    ImageView iv_back;
    int pos = 0;
    Button btn_add_address;
    private SimpleAdapter addr_adapter;
    private List<Map<String, Object>> addr_list;
    MyDatabaseHelper helper;
    Activity activity;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.CHANGE_BG:
                try {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    String name = System.currentTimeMillis() + "";
                    new UploadImage(bitmap, name).execute();
                    View view = gv_address.getChildAt(pos);
                    ImageView iv_address_bg = view.findViewById(R.id.iv_address_bg);
                    TextView tv_address_id = view.findViewById(R.id.tv_address_id);
                    iv_address_bg.setImageBitmap(bitmap);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        db.execSQL("update Space set Space_img = " + name
                                +" where Space_id = " + tv_address_id.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(3000);
                    String url = "http://112.74.109.111:8080/XHZH/space/updateImgById";
                    RequestParams params = new RequestParams();
                    params.put("Space_id", tv_address_id.getText().toString());
                    params.put("Space_img", name);
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                                boolean flag = json.getBoolean("flag");
                                if (flag) {
                                    Toast.makeText(activity, "修改成功", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(activity, "修改失败1", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "修改失败2", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });

                }catch (Exception e) {

                }
                break;
            case Constant.CHANGE_BG_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    String image_path;
                    if (Build.VERSION.SDK_INT >= 19) {
                        // a4.4及以上系统使用这个方法处理图片
                        image_path = handleImageOnKitKat(data);
                    } else {
                        // a4.4以下系统使用这个方法处理图片
                        image_path = handleImageBeforeKitKat(data);
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                    String name = System.currentTimeMillis() + "";
                    new UploadImage(bitmap, name).execute();
                    View view = gv_address.getChildAt(pos);
                    ImageView iv_address_bg = view.findViewById(R.id.iv_address_bg);
                    TextView tv_address_id = view.findViewById(R.id.tv_address_id);
                    iv_address_bg.setImageBitmap(bitmap);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        db.execSQL("update Space set Space_img = " + name
                                +" where Space_id = " + tv_address_id.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(3000);
                    String url = "http://112.74.109.111:8080/XHZH/space/updateImgById";
                    RequestParams params = new RequestParams();
                    params.put("Space_id", tv_address_id.getText().toString());
                    params.put("Space_img", name);
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                                boolean flag = json.getBoolean("flag");
                                if (flag) {
                                    Toast.makeText(activity, "修改成功", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(activity, "修改失败1", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "修改失败2", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_address);
        helper = new MyDatabaseHelper(this, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        initView();

    }
    private void initView() {
        activity = this;
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
        addr_list = new ArrayList<Map<String, Object>>();
        initData();
    }
    String[] from;
    private void initData() {
        from = new String[]{"tv_address_name", "tv_address_id", "iv_address_bg"};
        int[] to = {R.id.tv_address_name, R.id.tv_address_id, R.id.iv_address_bg};
        try {
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Space where User_id = "
                    + pref.getString("User_id", "798018"), null);
            while (cursor.moveToNext()) {
                String space = cursor.getString(cursor.getColumnIndex("Space_name"));
                String space_img = cursor.getString(cursor.getColumnIndex("Space_img"));
                int id = cursor.getInt(cursor.getColumnIndex("Space_id"));
                addData2AddressList(space, id, space_img);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addr_adapter = new SimpleAdapter(this, addr_list,
                R.layout.body_modify_address_item, from, to);
        addr_adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String arg2) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                } if(view instanceof ImageView && data instanceof String) {
                    ImageView iv = (ImageView)view;
                    String url = "http://139.199.38.177/php/XHZH/PicturesProfile/" + data + ".JPG";
                    Glide.with(activity)
                            .load(url)
                            .into(iv);
                    return true;
                } else {
                    return false;
                }

            }
        });
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
                        addAddress(new_address);
                    }
                }).setNegativeButton("取消",null).show();
    }
    void addAddress(String address) {  // 添加位置的回调方法
        if (address == null || address.equals("")) {
            AppUtils.showShortToast("名字不能为空", this);
        }
        else {
            final String new_address = address;
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            final String user_id = pref.getString("User_id", null);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(3000);
            RequestParams params = new RequestParams();
            params.put("User_id", user_id);
            params.put("new_address", new_address);
            String url = "http://112.74.109.111:8080/XHZH/space/add";
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    try {
                        JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                        boolean flag = json.getBoolean("flag");
                        String id = json.getString("id");
                        if (flag) {
                            Map map = new HashMap<String, Object>();
                            map.put(from[0], new_address);
                            map.put(from[1], id);
                            addr_list.add(0, map);
                            addr_adapter.notifyDataSetChanged();  // 数据变化时，哈哈
                            Toast.makeText(MainActivity.getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                            SQLiteDatabase db = helper.getWritableDatabase();
                            try {
                                db.execSQL("insert into Space (Space_name, Space_id, User_id) values(?, ?, ?)",
                                        new String[]{new_address, id, user_id});
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                db.close();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.getActivity(), "{'flag': 'false'}", Toast.LENGTH_SHORT).show();
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
        public void onItemClick(AdapterView<?> parent, View view, final int position,
                                long id) {
            final ImageView iv_del = view.findViewById(R.id.iv_delete_address);
            final TextView tv_address = view.findViewById(R.id.tv_address_name);
            final TextView tv_address_id = view.findViewById(R.id.tv_address_id);
            final ImageView iv_address_bg = view.findViewById(R.id.iv_address_bg);
            iv_address_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialog();
                    pos = position;
                }
            });
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
                            // AppUtils.showShortToast("" + position, MainActivity.getActivity());
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.setTimeout(3000);
                            String url = "http://112.74.109.111:8080/XHZH/space/deleteById";
                            RequestParams params = new RequestParams();
                            final String space_id = tv_address_id.getText().toString().trim();
                            params.put("space_id", space_id);
                            client.post(url, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    try {
                                        JSONObject json = new JSONObject(new String(bytes, "utf-8"));
                                        boolean flag = json.getBoolean("flag");
                                        if (flag) {
                                            addr_list.remove(position);
                                            addr_adapter.notifyDataSetChanged();
                                            SQLiteDatabase db = helper.getWritableDatabase();
                                            try {
                                                db.execSQL("delete from Space where Space_id = " + space_id);
                                                Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                db.close();
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

        }
    }

    private void addData2AddressList(String name, int id, String space_img) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(from[0], name);
        map.put(from[1], id);
        if (space_img == null || space_img.equals("")) {
            space_img = "box";
        }
        map.put(from[2], space_img);
        addr_list.add(map);
    }

    static Dialog mCameraDialog;
    private void setDialog() {
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.tv_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.tv_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(intent, Constant.CHANGE_BG);
                if (mCameraDialog != null) mCameraDialog.dismiss();
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraDialog.dismiss(); // 取消菜单
                Toast.makeText(activity, "取消", Toast.LENGTH_SHORT).show();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) this.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }
    private void openAlbum() { //选择图片就是这么简单
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        this.startActivityForResult(intent, Constant.CHANGE_BG_CHOOSE_PHOTO); // 打开相册
    }
    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        // displayImage(imagePath);
        return imagePath;
    }
    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //displayImage(imagePath); // 根据图片路径显示图片
        return imagePath;
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
