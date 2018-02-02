package com.example.repair;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.repair.homescreen_fragments.BottomNavigationBehavior;
import com.example.repair.homescreen_fragments.CustomPagerAdapter;
public class HomeScreen extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private BottomNavigationView navigation;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        viewPager = findViewById(R.id.view_pager);
        fragmentPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);

        navigation = findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_repair:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_shopping:
                    viewPager.setCurrentItem(1);
                    break;
            }
            return true;
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_repair);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
}