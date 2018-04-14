package com.ksblletba.orangeweather;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ksblletba.orangeweather.gson.Weather;
import com.ksblletba.orangeweather.util.HttpUtil;
import com.ksblletba.orangeweather.util.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrangeFragment extends Fragment {


    @BindView(R.id.morning_table)
    TextView morningTable;
    @BindView(R.id.noon_table)
    TextView noonTable;
    @BindView(R.id.lovely_suggestion)
    TextView lovelySuggestion;
    @BindView(R.id.lovely_gif)
    ImageView lovelyGif;
    Unbinder unbinder;
    @BindView(R.id.orange_info)
    NestedScrollView orangeInfo;

    private String weatherId;

    public OrangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orange_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        Glide.with(getActivity()).load(R.drawable.heart_hug).asGif().
                diskCacheStrategy(DiskCacheStrategy.SOURCE).into(lovelyGif);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherUrl = pref.getString("weather", null);
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_tab);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        if (weatherUrl != null) {
            Weather weather = Utility.handleWeatherResponse(weatherUrl);
            weatherId = weather.basic.weatherId;
            showTimetable(weather);
            showSuggestion(weather);
        } else {
            weatherId = getActivity().getIntent().getStringExtra("weather_id");
            orangeInfo.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            requestWeather(weatherId);
        }
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
                            showTimetable(weather);
                            showSuggestion(weather);
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

    private void showTimetable(Weather weather) {
        String date = weather.forecastList.get(0).date;
        String weekDay = Utility.dateToWeek(date);
        switch (weekDay) {
            case "星期一":
                morningTable.setText("早上八点有国际金融哟，不要睡懒觉啦٩(๑>◡<๑)۶ ");
                noonTable.setText("下午是保原呢，三节课让人难受，不过周一要元气满满哟( • ̀ω•́ )✧");
                break;
            case "星期二":
                morningTable.setText("没课的一天很难起床了，~(@^_^@)~");
                break;
            case "星期三":
                morningTable.setText("西文还是去上吧，小心老师点名哟(｀・ω・´)");
                noonTable.setText("今天小可爱的游泳技术又有提升了呢（づ￣3￣）づ╭❤～");
                break;
            case "星期四":
                morningTable.setText("风险管理是很难啦，不过也要认真听课哟(๑╹◡╹)ﾉ'''");
                break;
            case "星期五":
                morningTable.setText("中外法律课是你去还是同学呢，不要记错咯￣ω￣=");
                noonTable.setText("计算机课有什么不会的记得联系你最可爱的大树哈ヾ(@^▽^@)ノ");
                break;
            case "星期六":
                morningTable.setText("早早的起床补课，赚钱买新裙子啦ヾ(●´∀｀●) ");
                break;
            case "星期日":
                morningTable.setText("今天好好的睡个懒觉吧，美容觉让皮肤BlingBling(๑¯∀¯๑)");
                break;
        }
    }

    private void showSuggestion(Weather weather) {
        String weaInfo = weather.now.more.info;
        if (weaInfo.equals("晴")) {
            lovelySuggestion.setText("今天是个大晴天呢，要不要穿蓬蓬的仙女裙呢(✧◡✧)");
        } else if (weaInfo.equals("阴")) {
            lovelySuggestion.setText("今天天气有点一脸懵逼哟，记得保暖(❁´◡`❁)*✲ﾟ*");
        } else if (weaInfo.indexOf("云") != -1) {
            lovelySuggestion.setText("今天虽然有云彩，不过还是记得带伞哟，仙女不能见阳光的＜(▰˘◡˘▰)");
        } else if (weaInfo.indexOf("雨") != -1) {
            lovelySuggestion.setText("今天下雨啦，记得带伞，记得走得时候也带伞，不要再丢了啦(●´∀｀●)ﾉ");
        } else if (weaInfo.indexOf("风") != -1) {
            lovelySuggestion.setText("今天有风哟，小可爱小心被刮跑了♪（＾∀＾●）ﾉ");
        } else {
            lovelySuggestion.setText("小花陪着大树，大树也陪着小花o(*≧▽≦)ツ");
        }
        orangeInfo.setVisibility(View.VISIBLE);
    }

}
