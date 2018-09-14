package com.xhzh.shounaxiang.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.xhzh.shounaxiang.R;

import org.w3c.dom.Text;

import java.security.cert.LDAPCertStoreParameters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout my_sex;
    LinearLayout my_birth;
    LinearLayout my_nickname;

    private static boolean firstClickBack = false;
    private static final int EXIT = 0;
    private static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT:
                    firstClickBack = false;
                    break;
            }
            return false;
        }
    });
    private BottomNavigationView navigation;
    ViewPager viewPager;
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        View view1 = getLayoutInflater().inflate(R.layout.body_query, null);
        View view2 = getLayoutInflater().inflate(R.layout.body_add, null);
        View view3 = getLayoutInflater().inflate(R.layout.body_mine, null);

        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        viewPager = findViewById(R.id.view_pager_bottom_navigation);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
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
                    //my_sex的Popmenu
                    my_sex = findViewById(R.id.my_sex);
                    my_sex.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showPopMenu(view);
                        }
                    });
                    //my_birth的日期选择器
                    my_birth = findViewById(R.id.my_birth);
                    initDialog();
                    my_nickname = findViewById(R.id.my_nickname);
                    my_nickname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAlertDialog();
                        }
                    });
                    break;

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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

//    my_sex的点击选择PopMenu
    public void showPopMenu(View view){
        PopupMenu menu = new PopupMenu(this,view);
        menu.getMenuInflater().inflate(R.menu.sex_menu,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.male_item:
                        Toast.makeText(MainActivity.this, "male", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.female_item:
                        Toast.makeText(MainActivity.this, "female", Toast.LENGTH_SHORT).show();
                        break;

                }
                return true;
            }
        });
        menu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(MainActivity.this, "关闭了", Toast.LENGTH_SHORT).show();
            }
        });
        menu.show();
    }

    private void initDialog() {

        /**
         * 弹出日期选择对话框
         */
        my_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                final int day=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Toast.makeText(MainActivity.this,year+"年"+(month+1)+"月"+dayOfMonth+"日",Toast.LENGTH_SHORT).show();
                    }
                },year,month,day);

                datePickerDialog.show();

            }
        });

    }
    private void showAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        final EditText etContent = (EditText) view.findViewById(R.id.et_nickname);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("昵称")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = etContent.getText().toString();
                        if (isNullEmptyBlank(str)) {
                            etContent.setError("输入内容不能为空");
                        } else {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        dialog.show();
    }
    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        else
            return false;
    }


}



