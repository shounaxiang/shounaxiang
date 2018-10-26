package com.xhzh.shounaxiang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.DatabaseConfigure;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.listener.AddGoods_OnClickListener;
import com.xhzh.shounaxiang.listener.EditText_TextWatcher;
import com.xhzh.shounaxiang.listener.BottomMenuOnClinkListener;
import com.xhzh.shounaxiang.listener.ModifyAddress_OnClickListener;
import com.xhzh.shounaxiang.listener.Query_OnClickListener;
import com.xhzh.shounaxiang.localdatabase.MyDatabaseHelper;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.AudioSoundRecognizer;
import com.xhzh.shounaxiang.util.Constant;
import com.xhzh.shounaxiang.util.DownloadImage;
import com.xhzh.shounaxiang.util.PermUtil;
import com.xhzh.shounaxiang.util.SaveGoodsImage;
import com.xhzh.shounaxiang.util.UploadImage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

public class MainActivity extends AppCompatActivity {
    private static Activity MAINACTIVITY;
    LinearLayout my_birth;
    LinearLayout my_sex;
    LinearLayout ll_nickname;
    TextView tv_my_birth;
    TextView tv_logout;
    ImageView iv_avatar;
    Bitmap bitmap;
    LinearLayout ll_query_item_goods;
    LinearLayout ll_query_item_addr;
    RelativeLayout selected_goods;
    RelativeLayout selected_address;
    View[] views = new View[2];
    Button btn_modify_address;
    SharedPreferences pref;
    GridView gv_query_goods, gv_query_addr;
    ImageView iv_add_goods, iv_new_goods;
    EditText et_goods_name;
    Button btn_add_goods;
    ImageView iv_query;
    Button btn_voice_input;
    private static AudioSoundRecognizer recognizer;
    private MyDatabaseHelper helper;
    private List<Map<String, Object>> goods_list, addr_list;
    private SimpleAdapter goods_adapter, addr_adapter;
    private static final String TAG = "MainActivity";
    private static boolean firstClickBack = false;
    private static final int EXIT = 0;
    public static final int MODIFY_NAME = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final String NICKNAME = "avatar";
    private ViewPager.OnPageChangeListener pageChangeListener;
    private static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT:
                    firstClickBack = false;
                    break;
                case MODIFY_NAME:

                    break;
            }
            return false;
        }
    });
    private BottomNavigationView navigation;
    ViewPager viewPager;
    private List<View> viewList;
    TextView tv_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        }, 1);
        if(!PermUtil.checkPerm(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //ToastUtils.showShortToast("对不起，没有该权限，软件无法正常运行");
            Toast.makeText(MainActivity.this, "对不起，没有该权限，软件无法正常运行",
                    Toast.LENGTH_SHORT).show();
            //ActivityCollector.finishAll();
        }
        if (!PermUtil.checkPerm(this, Manifest.permission.CAMERA)) {
            Toast.makeText(MainActivity.this, "对不起，没有拍照权限，软件无法正常运行",
                    Toast.LENGTH_SHORT).show();
        }
        if (!PermUtil.checkPerm(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(MainActivity.this, "对不起，没有录音权限，软件无法正常运行",
                    Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Constant.MODIFY_NAME:
                if(data != null) {
                    tv_nickname.setText(data.getStringExtra(NICKNAME));
                }
                break;
            case Constant.CHOOSE_PHOTO:
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
                    displayImage(image_path);
                }
                break;
            case Constant.ADD_GOODS:
                try {
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    iv_new_goods.setImageBitmap(bitmap);
                    new SaveGoodsImage(bitmap, "test").save();
                }catch (Exception e) {

                }
                break;
            case Constant.CHOOSE_GOODS:
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
                    setNewGoodsImage(image_path);
                }
                break;
            case Constant.MODIFY_ADDRESS:
                initDataAddress();
                break;
        }
    }

    private void initView() {
        MainActivity.MAINACTIVITY = this;
        helper = new MyDatabaseHelper(this, DatabaseConfigure.db_name, null, DatabaseConfigure.version);
        pref = getSharedPreferences("user", MODE_PRIVATE);
        //new DownloadImage(pref.getString("User_phone", "default")).execute();
        View body_query = getLayoutInflater().inflate(R.layout.body_query, null);
        View body_add = getLayoutInflater().inflate(R.layout.body_add, null);
        btn_voice_input = body_add.findViewById(R.id.btn_voice_input);
        iv_add_goods = body_add.findViewById(R.id.iv_add_goods);
        iv_add_goods.setOnClickListener(new BottomMenuOnClinkListener(this));
        View body_mine = getLayoutInflater().inflate(R.layout.body_mine, null);
        iv_new_goods = body_add.findViewById(R.id.iv_new_goods);
        et_goods_name = body_add.findViewById(R.id.et_goods_name);
        btn_add_goods = body_add.findViewById(R.id.btn_add_goods);
        bitmap = ((BitmapDrawable)iv_add_goods.getDrawable()).getBitmap();
        btn_add_goods.setOnClickListener(new AddGoods_OnClickListener(this));
        et_goods_name.addTextChangedListener(new EditText_TextWatcher(this, btn_add_goods, et_goods_name));
        selected_goods = body_query.findViewById(R.id.selected_goods);
        selected_address = body_query.findViewById(R.id.selected_address);
        iv_query = body_query.findViewById(R.id.iv_query);
        iv_query.setOnClickListener(new Query_OnClickListener(this));
        views[0] = body_query.findViewById(R.id.view1);
        views[1] = body_query.findViewById(R.id.view2);
        ll_query_item_goods = body_query.findViewById(R.id.ll_query_item_goods);
        ll_query_item_addr = body_query.findViewById(R.id.ll_query_item_addr);
        gv_query_goods = ll_query_item_goods.findViewById(R.id.gv_query_goods);
        gv_query_addr = ll_query_item_addr.findViewById(R.id.gv_query_addr);
        goods_list = new ArrayList<Map<String, Object>>();
        addr_list = new ArrayList<Map<String, Object>>();
        //initData(); initDataAddress();
        btn_modify_address = body_query.findViewById(R.id.btn_modify_address);
        btn_modify_address.setOnClickListener(new ModifyAddress_OnClickListener(this));
        tv_nickname = body_mine.findViewById(R.id.tv_nickname);
        tv_nickname.setText(pref.getString("User_name", "佚名"));
        my_sex = body_mine.findViewById(R.id.my_sex);
        TextView tv_my_sex = body_mine.findViewById(R.id.tv_my_sex);
        tv_my_sex.setText(pref.getString("User_sex", "null"));
        my_birth = body_mine.findViewById(R.id.my_birth);
        ll_nickname = body_mine.findViewById(R.id.my_nickname);
        tv_my_birth = body_mine.findViewById(R.id.tv_my_birth);
        tv_my_birth.setText(pref.getString("User_birthday", ""));
        tv_logout = body_mine.findViewById(R.id.tv_logout);
        iv_avatar = body_mine.findViewById(R.id.iv_avatar);
        //File path = new File(User.LocalPritruesProfile, pref.getString("User_phone", "111") + ".JPG");
        //iv_avatar.setImageURI(Uri.fromFile(path));
        Glide.with(this).load("http://139.199.38.177/php/XHZH/PicturesProfile/"
                + pref.getString("User_phone", "") + ".JPG").into(iv_avatar);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_portrait);
        viewList = new ArrayList<>();
        viewList.add(body_query);
        viewList.add(body_add);
        viewList.add(body_mine);

        viewPager = findViewById(R.id.view_pager_bottom_navigation);
        viewPager.setAdapter(pagerAdapter);
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigation.setSelectedItemId(R.id.navigation_query);
                        initData();
                        break;
                    case 1:
                        navigation.setSelectedItemId(R.id.navigation_add);
                        break;
                    case 2:
                        navigation.setSelectedItemId(R.id.navigation_mine);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        my_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionPicker picker = new OptionPicker(MainActivity.this, new String[]{"男", "女"});
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        TextView tv = findViewById(R.id.tv_my_sex);
                        tv.setText(item);
                    }
                });
                picker.show();
            }
        });
        selected_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                views[0].setVisibility(View.VISIBLE);
                views[1].setVisibility(View.INVISIBLE);
                ll_query_item_goods.setVisibility(View.VISIBLE);
                ll_query_item_addr.setVisibility(View.INVISIBLE);
                btn_modify_address.setVisibility(View.INVISIBLE);
            }
        });
        selected_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                views[0].setVisibility(View.INVISIBLE);
                views[1].setVisibility(View.VISIBLE);
                ll_query_item_goods.setVisibility(View.INVISIBLE);
                ll_query_item_addr.setVisibility(View.VISIBLE);
                btn_modify_address.setVisibility(View.VISIBLE);
            }
        });
        my_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date = tv_my_birth.getText().toString().split("-");
                if (date.length < 3) {
                    date = (new SimpleDateFormat("yyyy-mm-dd").format(new Date())).toString().split("-");
                }
                int year=Integer.parseInt(date[0]);
                int month=Integer.parseInt(date[1]) - 1;
                int day=Integer.parseInt(date[2]);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                tv_my_birth.setText(year + "-" + String.format("%02d", month +1) + "-"
                                        + String.format("%02d", dayOfMonth));
                            }
                        },year,month,day);

                //限制最大可选时间
                DatePicker dp = datePickerDialog.getDatePicker();
                dp.setMaxDate(new Date().getTime());
                datePickerDialog.show();

            }
        });
        ll_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ModifyNickname.class);
                intent.putExtra(NICKNAME, tv_nickname.getText().toString());
                startActivityForResult(intent, MODIFY_NAME);
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("退出将会清空缓存数据，确定退出？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                        editor.putBoolean("LOGIN_STATE", false);
                        editor.apply();
                        finish();
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

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[] {
                                    Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        initData(); initDataAddress();
        recognizer = getRecognizer();
        btn_voice_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recognizer.isRecognizing()) {
                    recognizer.stopRecognize();
                    btn_voice_input.setText("语音输入");
                }
                else {
                    recognizer.startRecognize();
                    btn_voice_input.setText("识别中...点击停止");
                }
            }
        });

    }
    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_query:
                    viewPager.setCurrentItem(0);
                    initData();
                    return true;
                case R.id.navigation_add:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_mine:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
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

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
            new UploadImage(bitmap, pref.getString("User_phone", "error")).execute();
            //Log.d(TAG, "图片路径：" + imagePath);
            iv_avatar.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    private void setNewGoodsImage(String imagePath) {
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
            iv_new_goods.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to set image", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        firstClickBack = !firstClickBack;
        if (firstClickBack) {
            Toast.makeText(MainActivity.this,
                    "再次点击退出应用", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(EXIT, 2000);
        }
        else {
            SQLiteDatabase db = helper.getReadableDatabase();
            try {
                db.execSQL("drop table xhzh.Goods");
                db.execSQL("drop table xhzh.Space");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }
    private void initData() {
        goods_list.clear();
        List<String> imgs = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        List<String> addrs = new ArrayList<String>();
        List<String> goods_id = new ArrayList<String>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Goods where User_id = "
                    + pref.getString("User_id", "3"), null);
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
    private void initDataAddress() {
        addr_list.clear();
        String[] from = {"tv_address_name", "tv_address_id", "iv_address_bg"};
        int[] to = {R.id.tv_address_name, R.id.tv_address_id, R.id.iv_address_bg};
        try {
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Space where User_id = "
                    + pref.getString("User_id", "798018"), null);
            while (cursor.moveToNext()) {
                String space = cursor.getString(cursor.getColumnIndex("Space_name"));
                int id = cursor.getInt(cursor.getColumnIndex("Space_id"));
                String space_img = cursor.getString(cursor.getColumnIndex("Space_img"));
                addData2AddressList(space, id, space_img);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addr_adapter = new SimpleAdapter(this, addr_list,
                R.layout.body_query_addr_item, from, to);
        gv_query_addr.setAdapter(addr_adapter);
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
                    Glide.with(getActivity())
                            .load(url)
                            .into(iv);
                    return true;
                } else {
                    return false;
                }

            }
        });
        gv_query_addr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_address_name = view.findViewById(R.id.tv_address_name);
                Intent intent = new Intent(getActivity(), ShowGoodsByAddressActivity.class);
                intent.putExtra("address_name", tv_address_name.getText().toString());
                getActivity().startActivity(intent);

            }
        });
    }
    public static Activity getActivity() {
        return MAINACTIVITY;
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
    private void addData2AddressList(String name, int id, String space_img) {
        String[] from = {"tv_address_name", "tv_address_id", "iv_address_bg"};
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(from[0], name);
        map.put(from[1], id);
        if (space_img == null || space_img.equals("")) {
            space_img = "box";
        }
        map.put(from[2], space_img);
        addr_list.add(map);
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
                                    et_goods_name.setText(tmp);
                                    //sendRecorder(tmp);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            else
                                Log.e(TAG, "onRecognizingResult: error: " + i);
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

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
