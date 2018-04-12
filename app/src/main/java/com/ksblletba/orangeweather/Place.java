package com.ksblletba.orangeweather;

/**
 * Created by Administrator on 2018/4/12.
 */

public class Place {
    private String name;
    private int imageId;

    public Place(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }
}
