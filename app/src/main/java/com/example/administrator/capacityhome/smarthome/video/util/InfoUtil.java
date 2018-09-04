package com.example.administrator.capacityhome.smarthome.video.util;

import android.content.Context;

import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;


/**
 * Created by Administrator on 2017/3/16.
 */

public class InfoUtil {
    public static String getString(Context context, String key) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE).getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static void clearUserData(Context context) {

        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static int getInt(Context context, String key) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static void putInt(Context context, String key, int value) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static float getFloat(Context context, String key) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE).getFloat(key, 0);
    }

    public static void putFloat(Context context, String key, float value) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putFloat(key, value).commit();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static void logout(Context context) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().clear().commit();
        DatabaseManager manager = new DatabaseManager(context);
        manager.clearDevice();
    }

}
