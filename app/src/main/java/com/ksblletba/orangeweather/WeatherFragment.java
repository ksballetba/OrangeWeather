package com.ksblletba.orangeweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ksblletba.orangeweather.gson.Weather;
import com.ksblletba.orangeweather.util.HttpUtil;
import com.ksblletba.orangeweather.util.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherFragment extends Fragment {

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
    @BindView(R.id.weather_md_layout)
    NestedScrollView weatherMdLayout;
    Unbinder unbinder;
    private String weatherId;


    public TextView getDateText0() {
        return dateText0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherUrl = pref.getString("weather", null);
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_tab);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        if (weatherUrl != null) {
            Weather weather = Utility.handleWeatherResponse(weatherUrl);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getActivity().getIntent().getStringExtra("weather_id");
            weatherMdLayout.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            requestWeather(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });



        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            weatherId = weather.basic.weatherId;
                            SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_tab);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getActivity(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String placeName = weather.basic.cityName;
        CollapsingToolbarLayout collapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_layout_tab);
        collapsingToolbarLayout.setTitle(placeName);
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        ImageView weatherPic = getActivity().findViewById(R.id.weather_pic_tab);
//        if (weather.now.more.info.equals("晴")) {
//            Glide.with(getActivity()).load(R.drawable.sunny).into(weatherPic);
//        } else if (weather.now.more.info.equals("阴")) {
//            Glide.with(getActivity()).load(R.drawable.gloomy).into(weatherPic);
//        } else if (weather.now.more.info.indexOf("云") != -1) {
//            Glide.with(getActivity()).load(R.drawable.rainy2).into(weatherPic);
//        } else if (weather.now.more.info.indexOf("雨") != -1) {
//            Glide.with(getActivity()).load(R.drawable.rainy).into(weatherPic);
//        } else if (weather.now.more.info.indexOf("风") != -1) {
//            Glide.with(getActivity()).load(R.drawable.cloudy).into(weatherPic);
//        } else {
//            Glide.with(getActivity()).load(R.drawable.sun).into(weatherPic);
//        }

        WeatherTabActivity wta = (WeatherTabActivity)getActivity();
        wta.setDate(weather.forecastList.get(0).date);
        wta.setWeaInfo(weatherInfo);
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
