package com.qjizho.inspmarker.activity;

import in.srain.cube.request.JsonData;



import in.srain.cube.request.JsonData;

public class ImageListItem {

    public String picUrl;

    public ImageListItem(JsonData jsonData) {
        picUrl = jsonData.optString("pic");
    }
}
