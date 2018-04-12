package com.ksblletba.orangeweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ksblletba.orangeweather.gson.Weather;
import com.ksblletba.orangeweather.util.HttpUtil;
import com.ksblletba.orangeweather.util.Utility;

import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherMdActivity extends AppCompatActivity {

    @BindView(R.id.weather_pic)
    ImageView weatherPic;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.date_text0)
    TextView dateText0;
    @BindView(R.id.info_text0)
    TextView infoText0;
    @BindView(R.id.max_text0)
    TextView maxText0;
    @BindView(R.id.min_text0)
    TextView minText0;
    @BindView(R.id.date_text1)
    TextView dateText1;
    @BindView(R.id.info_text1)
    TextView infoText1;
    @BindView(R.id.max_text1)
    TextView maxText1;
    @BindView(R.id.min_text1)
    TextView minText1;
    @BindView(R.id.date_text2)
    TextView dateText2;
    @BindView(R.id.info_text2)
    TextView infoText2;
    @BindView(R.id.max_text2)
    TextView maxText2;
    @BindView(R.id.min_text2)
    TextView minText2;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.carwash_text)
    TextView carwashText;
    @BindView(R.id.sport_text)
    TextView sportText;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.demo)
    TextView demo;
    @BindView(R.id.swipe_md)
    SwipeRefreshLayout swipeMd;
    @BindView(R.id.draw_md)
    DrawerLayout drawMd;
    @BindView(R.id.weather_md_layout)
    NestedScrollView weatherMdLayout;
    @BindView(R.id.nav_view)
    NavigationView navView;

    private NavigationView navigationView;

    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_md);
        ButterKnife.bind(this);
        appBar = findViewById(R.id.app_bar);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carwashText = findViewById(R.id.carwash_text);
        sportText = findViewById(R.id.sport_text);
        swipeMd = findViewById(R.id.swipe_md);
        navigationView = findViewById(R.id.nav_view);
        weatherMdLayout = findViewById(R.id.weather_md_layout);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherUrl = pref.getString("weather", null);

        if (weatherUrl != null) {
            Weather weather = Utility.handleWeatherResponse(weatherUrl);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            swipeMd.setRefreshing(true);
            weatherMdLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeMd.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                swipeMd.setEnabled(verticalOffset >= 0 ? true : false);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        break;
                    case R.id.nav_place:
                        Intent intent = new Intent(WeatherMdActivity.this, SwitchCityActivity.class);
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
                drawMd.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        Toast.makeText(WeatherMdActivity.this, "adsfa", Toast.LENGTH_SHORT).show();
    }

    public void requestWeather(final String mWeatherId) {
        String weatherAdress = "http://guolin.tech/api/weather?cityid=" + mWeatherId + "&key=c9feb8bcd6754f95ac3c8d46ace42629";
        HttpUtil.sendOkHttpRequest(weatherAdress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherMdActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            weatherId = weather.basic.weatherId;
                            swipeMd.setRefreshing(false);
                        } else {
                            Toast.makeText(WeatherMdActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        if (weather.now.more.info.equals("晴")) {
            Glide.with(WeatherMdActivity.this).load(R.drawable.sunny).into(weatherPic);
        } else if(weather.now.more.info.equals("阴")){
            Glide.with(WeatherMdActivity.this).load(R.drawable.gloomy).into(weatherPic);
        } else if(weather.now.more.info.indexOf("云")!=-1){
            Glide.with(WeatherMdActivity.this).load(R.drawable.rainy2).into(weatherPic);
        } else if(weather.now.more.info.indexOf("雨")!=-1){
             Glide.with(WeatherMdActivity.this).load(R.drawable.rainy).into(weatherPic);
        }else if(weather.now.more.info.indexOf("风")!=-1){
            Glide.with(WeatherMdActivity.this).load(R.drawable.cloudy).into(weatherPic);
        } else {
            Glide.with(WeatherMdActivity.this).load(R.drawable.sun).into(weatherPic);
        }
        collapsingLayout.setTitle(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        dateText0.setText(weather.forecastList.get(0).date);
        dateText1.setText(weather.forecastList.get(1).date);
        dateText2.setText(weather.forecastList.get(2).date);
        infoText0.setText(weather.forecastList.get(0).more.info);
        infoText1.setText(weather.forecastList.get(1).more.info);
        infoText2.setText(weather.forecastList.get(2).more.info);
        maxText0.setText(weather.forecastList.get(0).temperature.max);
        maxText1.setText(weather.forecastList.get(1).temperature.max);
        maxText2.setText(weather.forecastList.get(2).temperature.max);
        minText0.setText(weather.forecastList.get(0).temperature.min);
        minText1.setText(weather.forecastList.get(1).temperature.min);
        minText2.setText(weather.forecastList.get(2).temperature.min);
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度： " + weather.suggestion.comfort.info;
        String carWash = "洗车指数： " + weather.suggestion.carWash.info;
        String sport = "运动建议： " + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carwashText.setText(carWash);
        sportText.setText(sport);
        weatherMdLayout.setVisibility(View.VISIBLE);
    }
}
