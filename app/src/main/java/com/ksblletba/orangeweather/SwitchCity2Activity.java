package com.ksblletba.orangeweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ksblletba.orangeweather.db.City;
import com.ksblletba.orangeweather.db.County;
import com.ksblletba.orangeweather.db.Province;
import com.ksblletba.orangeweather.util.HttpUtil;
import com.ksblletba.orangeweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SwitchCity2Activity extends AppCompatActivity {

    @BindView(R.id.switch_toolbar)
    Toolbar switchToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    private ProgressDialog progressDialog;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private ActionBar actionBar;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    private List<Place> placeList = new ArrayList<>();
    private PlaceAdapter adapter;
    private int[] placeImageId = {R.drawable.city1, R.drawable.city2, R.drawable.city3, R.drawable.city4, R.drawable.city5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_city2);
        ButterKnife.bind(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PlaceAdapter(placeList);

        setSupportActionBar(switchToolbar);
        actionBar = getSupportActionBar();
        recyclerView.setAdapter(adapter);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
        adapter.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SwitchCity2Activity.this).edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(SwitchCity2Activity.this, WeatherTabActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        queryProvinces();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentLevel == LEVEL_PROVINCE) {
                    finish();
                }
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    private void queryProvinces() {

        actionBar.setTitle("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            placeList.clear();
            for (Province province : provinceList) {
                Random random = new Random();
                int i = random.nextInt(5);
                placeList.add(new Place(province.getProvinceName(), placeImageId[i]));
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {

        switchToolbar.setTitle(selectedProvince.getProvinceName());
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            placeList.clear();
            for (City city : cityList) {
                Random random = new Random();
                int i = random.nextInt(5);
                placeList.add(new Place(city.getCityName(), placeImageId[i]));
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {

        switchToolbar.setTitle(selectedCity.getCityName());
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            placeList.clear();
            for (County county : countyList) {
                Random random = new Random();
                int i = random.nextInt(5);
                placeList.add(new Place(county.getCountyName(), placeImageId[i]));
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = true;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (!result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
