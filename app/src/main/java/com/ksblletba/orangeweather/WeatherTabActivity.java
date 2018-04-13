package com.ksblletba.orangeweather;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherTabActivity extends AppCompatActivity {

    @BindView(R.id.tool_bar_tab)
    Toolbar toolBarTab;
    @BindView(R.id.collapsing_layout_tab)
    CollapsingToolbarLayout collapsingLayoutTab;
    @BindView(R.id.app_bar_tab)
    AppBarLayout appBarTab;
    @BindView(R.id.tab_layout_tab)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_tab)
    ViewPager viewPagerTab;
    @BindView(R.id.fab_tab)
    FloatingActionButton fabTab;
    @BindView(R.id.nav_view_tab)
    NavigationView navViewTab;
    @BindView(R.id.draw_tab)
    DrawerLayout drawTab;
    @BindView(R.id.swipe_tab)
    SwipeRefreshLayout swipeTab;
    @BindView(R.id.weather_pic_tab)
    ImageView weatherPicTab;

    private WeatherFragment weatherFragment = new WeatherFragment();
    private OrangeFragment orangeFragment = new OrangeFragment();
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_tab);
        ButterKnife.bind(this);
        initViewPager();
        setSupportActionBar(toolBarTab);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        appBarTab.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                swipeTab.setEnabled(verticalOffset >= 0 ? true : false);
            }
        });

        navViewTab.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        break;
                    case R.id.nav_place:
                        Intent intent = new Intent(WeatherTabActivity.this, SwitchCity2Activity.class);
                        startActivity(intent);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawTab.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fab_tab)
    public void onClick() {

    }

    private void initViewPager() {
        fragments.add(weatherFragment);
        fragments.add(orangeFragment);
        viewPagerTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        tabLayout.setupWithViewPager(viewPagerTab);
        tabLayout.getTabAt(0).setText("天气");
        tabLayout.getTabAt(1).setText("贴士");
    }

}
