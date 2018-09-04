package com.example.administrator.capacityhome.smarthome.video.activity;

import com.alibaba.fastjson.JSONObject;

public class FastJsonUtil {

    public static Object get(String json, Class clazz){
        return JSONObject.parseObject(json,clazz);
    }

}
