package com.example.administrator.capacityhome.smarthome.video.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ResetDBHelper {
    private static final String RESET_DB_SP = "reset_db_sp";
    private static final String IS_FIRST_SP = "is_first_sp";

    private static boolean IS_FIRST_INSTALL = true;

    public static boolean getConfig( Context cxt ){
        SharedPreferences cfg_app = cxt.getSharedPreferences( RESET_DB_SP, 0 );
        if( cfg_app != null ){
            IS_FIRST_INSTALL = cfg_app.getBoolean( IS_FIRST_SP, true );
        }
        return IS_FIRST_INSTALL;
    }

    public static void storeConfig( Context cxt ){
        SharedPreferences cfg_app= cxt.getSharedPreferences( RESET_DB_SP, 0 );
        if( cfg_app != null ){
            SharedPreferences.Editor editor = cfg_app.edit();
            editor.putBoolean( IS_FIRST_SP, false );
            editor.commit();
        }
    }
}
