package com.xhzh.shounaxiang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.dataclass.User;
import com.xhzh.shounaxiang.listener.BottomMenuOnClinkListener;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.DownloadImage;
import com.xhzh.shounaxiang.util.PermUtil;
import com.xhzh.shounaxiang.util.UploadImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

public class MainActivity extends AppCompatActivity {
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
    GridView gv_query_goods;
    ImageView iv_add_goods;
    private List<Map<String, Object>> goods_list;
    private SimpleAdapter goods_adapter;
    private static final String TAG = "MainActivity";
    private static boolean firstClickBack = false;
    private static final int EXIT = 0;
    public static final int MODIFY_NAME = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final String NICKNAME = "avatar";
    public static  Context main_context;
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 1);
        if(!PermUtil.checkPerm(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //ToastUtils.showShortToast("对不起，没有该权限，软件无法正常运行");
            Toast.makeText(MainActivity.this, "对不起，没有该权限，软件无法正常运行",
                    Toast.LENGTH_SHORT).show();
            //ActivityCollector.finishAll();
        }
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case MODIFY_NAME:
                if(data != null) {
                    tv_nickname.setText(data.getStringExtra(NICKNAME));
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // a4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // a4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }

    }

    private void initView() {
        main_context = this;
        pref = getSharedPreferences("user", MODE_PRIVATE);
        new DownloadImage(pref.getString("User_phone", "default")).execute();
        View body_query = getLayoutInflater().inflate(R.layout.body_query, null);
        View body_add = getLayoutInflater().inflate(R.layout.body_add, null);
        iv_add_goods = body_add.findViewById(R.id.iv_add_goods);
        iv_add_goods.setOnClickListener(new BottomMenuOnClinkListener(this));
        View body_mine = getLayoutInflater().inflate(R.layout.body_mine, null);
        selected_goods = body_query.findViewById(R.id.selected_goods);
        selected_address = body_query.findViewById(R.id.selected_address);
        views[0] = body_query.findViewById(R.id.view1);
        views[1] = body_query.findViewById(R.id.view2);
        ll_query_item_goods = body_query.findViewById(R.id.ll_query_item_goods);
        gv_query_goods = ll_query_item_goods.findViewById(R.id.gv_query_goods);
        goods_list = new ArrayList<Map<String, Object>>();
        initData();
        ll_query_item_addr = body_query.findViewById(R.id.ll_query_item_addr);
        btn_modify_address = body_query.findViewById(R.id.btn_modify_address);
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
        File path = new File(User.LocalPritruesProfile, pref.getString("User_phone", "111") + ".JPG");
        iv_avatar.setImageURI(Uri.fromFile(path));
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
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
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
        displayImage(imagePath); // 根据图片路径显示图片
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
    @Override
    public void onBackPressed() {
        firstClickBack = !firstClickBack;
        if (firstClickBack) {
            Toast.makeText(MainActivity.this,
                    "再次点击退出应用", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(EXIT, 2000);
        }
        else {
            finish();
        }
    }
    private void initData() {
        int[] fruits = {R.drawable.fruit1, R.drawable.fruit2, R.drawable.fruit3, R.drawable.fruit4};
        String[] names = {"荔枝", "柿子", "桃子", "草莓"};
        String[] addrs = {"盘子", "桌子", "箱子", "篮子"};
        String[] from = {"iv_query_goods", "tv_query_goods_name", "tv_query_goods_address"};
        for (int i = 0; i < fruits.length; ++i) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put(from[0], fruits[i]);
            map.put(from[1], names[i]);
            map.put(from[2], addrs[i]);
            goods_list.add(map);
        }

        int[] to = {R.id.iv_query_goods, R.id.tv_query_goods_name, R.id.tv_query_goods_address};
        goods_adapter = new SimpleAdapter(this, goods_list,
                R.layout.body_query_goods_item, from, to);
        gv_query_goods.setAdapter(goods_adapter);

    }

}
