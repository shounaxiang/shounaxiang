package com.xhzh.shounaxiang.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.listener.OnClickBackListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;

public class MainActivity extends AppCompatActivity {
    LinearLayout my_birth;
    LinearLayout my_sex;
    LinearLayout ll_nickname;
    TextView tv_my_birth;
    TextView tv_logout;
    private static final String TAG = "MainActivity";
    private static boolean firstClickBack = false;
    private static final int EXIT = 0;
    public static final int MODIFY_NAME = 1;
    public static final String NICKNAME = "nickname";
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
        }

    }

    private void initView() {
        View view1 = getLayoutInflater().inflate(R.layout.body_query, null);
        View view2 = getLayoutInflater().inflate(R.layout.body_add, null);
        View view3 = getLayoutInflater().inflate(R.layout.body_mine, null);
        tv_nickname = view3.findViewById(R.id.tv_nickname);
        my_sex = view3.findViewById(R.id.my_sex);
        my_birth = view3.findViewById(R.id.my_birth);
        ll_nickname = view3.findViewById(R.id.my_nickname);
        tv_my_birth = view3.findViewById(R.id.tv_my_birth);
        tv_logout = view3.findViewById(R.id.tv_logout);
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

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
        my_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date = tv_my_birth.getText().toString().split("-");
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

}



