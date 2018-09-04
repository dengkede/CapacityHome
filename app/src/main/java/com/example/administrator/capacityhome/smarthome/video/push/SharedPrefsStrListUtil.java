package com.example.administrator.capacityhome.smarthome.video.push;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanlinchen on 4/4/2016.
 */
public class SharedPrefsStrListUtil {

    private final static String TAG = SharedPrefsStrListUtil.class.getSimpleName();
    private final static String SP_NAME = "uid_list";

    public static List<String> initUIDList(Context context) {

        String favStr = null;
        List<String> list = new ArrayList<>();
        FileInputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        final byte[] buf = new byte[1024];
        byteArrayOutputStream = new ByteArrayOutputStream();
        int length = 0;
        try {
            inputStream = context.openFileInput(SP_NAME);
            while ((length = inputStream.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, length);
            }
            byte[] content = byteArrayOutputStream.toByteArray();
            favStr = new String(content, "UTF-8");
            byteArrayOutputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (favStr != null) {
            Log.i(TAG, "initUIDList: " + favStr);
            JSONObject object = null;
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(favStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    list.add(object.getString("uid"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public static void addUID(Context context, String uid) {

        List<String> list = initUIDList(context);
        list.add(uid);
        try {
            FileOutputStream outputStream = context.openFileOutput(SP_NAME, Context.MODE_PRIVATE);
            outputStream.write(list2Json(list).getBytes("UTF-8"));
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUID(Context context, String uid) {

        List<String> list = initUIDList(context);
        list.remove(uid);
        try {
            FileOutputStream outputStream = context.openFileOutput(SP_NAME, Context.MODE_PRIVATE);
            outputStream.write(list2Json(list).getBytes("UTF-8"));
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String list2Json(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (String s : list) {
                JSONObject object = new JSONObject();
                object.put("uid", s);
                jsonArray.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }
}

