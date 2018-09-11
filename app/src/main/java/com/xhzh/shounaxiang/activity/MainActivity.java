package com.xhzh.shounaxiang.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xhzh.shounaxiang.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
}
