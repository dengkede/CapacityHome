package com.example.administrator.capacityhome.smarthome.video.appteam;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;


import com.example.administrator.capacityhome.smarthome.video.push.SharedPrefsStrListUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatabaseManager {

	public static final String TABLE_DEVICE = "device";
	public static final String TABLE_SEARCH_HISTORY = "search_history";
	public static final String TABLE_SNAPSHOT = "snapshot";
	public static final String TABLE_VISIT_RECORD  = "Ealink_visit_record";
	public static final String TABLE_EALINK_SOUND  = "Ealink_sound";
	
	public static final String TABLE_BIND_SET ="Ealink_bind_set";
	public static final String TABLE_REMOVE_LIST = "remove_list";
	public static int n_mainActivity_Status = 0;

	private DatabaseHelper mDbHelper;
	public Context mcontext=null;
	public DatabaseManager(Context context) {
		mDbHelper = new DatabaseHelper(context);
		mcontext = context;
	}

	public SQLiteDatabase getReadableDatabase() {
		return mDbHelper.getReadableDatabase();
	}

	
	
	public static String uid_Produce(Context context)
	{

		SharedPreferences settings = context.getSharedPreferences("setting", 0);
		if(settings.getString("device_imei","").equals("")==true)
		{
			String serial_number = "";
			if (Build.VERSION.SDK_INT >= 9)
			{
				serial_number = Build.SERIAL;
			}
			String mac_address="";
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if(wifiManager!=null)
			{
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if(wifiInfo!=null)
				{
					mac_address = wifiInfo.getMacAddress();
				}
			}
			if((serial_number==null||serial_number.equals("")))
			{
				int rendom_number = (int)(Math.random() * 1+1)+(int)(Math.random() * 10+1)+(int)(Math.random() * 100+1)+(int)(Math.random() * 1000+1)+(int)(Math.random() * 10000+1)+(int)(Math.random() * 100000+1)+(int)(Math.random() * 1000000+1)+(int)(Math.random() * 10000000+1);
				if(rendom_number<10000000)
				{
					rendom_number=rendom_number+10000000;
				}
				serial_number= String.valueOf(rendom_number);

			}
			if((mac_address==null||mac_address.equals("")))
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");

				Date curDate = new Date(System.currentTimeMillis()) ;

				mac_address = formatter.format(curDate);
				//mac_address=String.valueOf((int)(Math.random() * 1+1)+(int)(Math.random() * 10+1)+(int)(Math.random() * 100+1)+(int)(Math.random() * 1000+1)+(int)(Math.random() * 10000+1)+(int)(Math.random() * 100000+1)+(int)(Math.random() * 1000000+1)+(int)(Math.random() * 10000000+1));
			}

			mac_address = mac_address.replace(":","");
			String[] new_mac_address = new String[2];
			new_mac_address[0]=mac_address.substring(0,6);
			new_mac_address[1]=mac_address.substring(6);
			String[] new_serial_number = new String[2];
			new_serial_number[0]=serial_number.substring(0,4);
			new_serial_number[1]=serial_number.substring(4);
			settings.edit().putString("device_imei","AN"+new_mac_address[0]+new_serial_number[1]+new_mac_address[1]+new_serial_number[0]).commit();

			return "AN"+new_mac_address[0]+new_serial_number[1]+new_mac_address[1]+new_serial_number[0];
		}
		else
		{
			return settings.getString("device_imei","");
		}



	}

	public static String get_language()
	{
		 return Locale.getDefault().getLanguage();
	}

	public static String get_osver()
	{
		 return Build.VERSION.RELEASE;
	}

	public static String get_appver(Context context)
	{
		 String versionName = "";
	     try
	     {
	    	 PackageManager packageManager = context.getPackageManager();
	         PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
	         versionName = packageInfo.versionName;
	         if (TextUtils.isEmpty(versionName))
	         {
	             return "";
	         }
	     }
	     catch (Exception e)
	     {
	         e.printStackTrace();
	     }
	     return versionName;
	}

	public static String get_model()
	{
		 return Build.MODEL.replace(" ","%20");
	}
	
	
	public long addDevice(String dev_nickname, String dev_uid, String dev_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel, int timesyc) {

		if(mcontext!=null){
			SharedPrefsStrListUtil.addUID(mcontext, dev_uid);
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_nickname", dev_nickname);
		values.put("dev_uid", dev_uid);
		values.put("dev_name", dev_name);
		values.put("dev_pwd", dev_pwd);
		values.put("view_acc", view_acc);
		values.put("view_pwd", view_pwd);
		values.put("event_notification", event_notification);
		values.put("camera_channel", channel);
		values.put("auto_time_sync", timesyc);

		long ret = db.insertOrThrow(TABLE_DEVICE, null, values);
		db.close();

		return ret;
	}

	public long add_remove_list(String dev_uid)
	{

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("uid", dev_uid);
		long ret = db.insertOrThrow(TABLE_REMOVE_LIST, null, values);
		db.close();
		return ret;
	}

	public void delete_remove_list(String dev_uid)
	{

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_REMOVE_LIST, "uid = '" + dev_uid + "'", null);
		db.close();
	}

	public long addSnapshot(String dev_uid, String file_path, long time) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("file_path", file_path);
		values.put("time", time);

		long ret = db.insertOrThrow(TABLE_SNAPSHOT, null, values);
		db.close();

		return ret;
	}

	public void updateDeviceInfoByDBID(long db_id, String dev_uid, String dev_nickname, String dev_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel, int time_sync) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("dev_nickname", dev_nickname);
		values.put("dev_name", dev_name);
		//values.put("dev_pwd", dev_pwd);
		values.put("dev_pwd", view_pwd);
		values.put("view_acc", view_acc);
		values.put("view_pwd", view_pwd);
		values.put("event_notification", event_notification);
		values.put("auto_time_sync",time_sync);
		values.put("camera_channel", channel);
		db.update(TABLE_DEVICE, values, "_id = '" + db_id + "'", null);
		db.close();
	}

	public void updateDeviceAskFormatSDCardByUID(String dev_uid, boolean askOrNot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ask_format_sdcard", askOrNot ? 1 : 0);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}
   
	public void updateDeviceTimeSycByUID(String dev_uid, int timesyc)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("auto_time_sync", timesyc);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}
	
	public void updateDeviceChannelByUID(String dev_uid, int channelIndex) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("camera_channel", channelIndex);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid, Bitmap snapshot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snapshot", getByteArrayFromBitmap(snapshot));
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid, byte[] snapshot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snapshot", snapshot);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void removeDeviceByUID(String dev_uid) {

//		if(mcontext!=null){
//			SharedPrefsStrListUtil.removeUID(mcontext,dev_uid);//根据网络请求来决定是否删除
//		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_DEVICE, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void clearDevice() {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_DEVICE, null, null);
		db.close();
	}

	public void removeSnapshotByUID(String dev_uid) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_SNAPSHOT, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {

		if (bitmap != null && !bitmap.isRecycled()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0, bos);
			return bos.toByteArray();
		} else {
			return null;
		}
	}

	public static BitmapFactory.Options getBitmapOptions(int scale) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;

		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return options;
	}

	public static Bitmap getBitmapFromByteArray(byte[] byts) {

		InputStream is = new ByteArrayInputStream(byts);
		return BitmapFactory.decodeStream(is, null, getBitmapOptions(2));
	}

	public long addSearchHistory(String dev_uid, int eventType, long start_time, long stop_time) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("search_event_type", eventType);
		values.put("search_start_time", start_time);
		values.put("search_stop_time", stop_time);

		long ret = db.insertOrThrow(TABLE_SEARCH_HISTORY, null, values);
		db.close();

		return ret;
	}
	//march
	
	public  long addEalinkVisitRecord(String dev_uid, Bitmap s, String time)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("snapshot", getByteArrayFromBitmap(s));
		values.put("time", time);
		long ret = db.insertOrThrow(TABLE_VISIT_RECORD, null, values);
		db.close();
		return ret;
		
	}
	public  void updateEalinkRecordByUID(String dev_uid, Bitmap s, String time)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snapshot", getByteArrayFromBitmap(s));
		values.put("time", time);
		db.update(TABLE_VISIT_RECORD, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}
	public void removeEalinkRecordByUID(String dev_uid)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_VISIT_RECORD, "dev_uid = '" + dev_uid + "'", null);
		db.close();
		
	}

	public void removeEalinkRecordBydbid(long id)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_VISIT_RECORD, "_id = '" + id + "'", null);
		db.close();
		
	}
	
	public long addEalinkSound()
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("path", "");
		values.put("sound", "");
		values.put("WIFI", "");
		long ret = db.insertOrThrow(TABLE_EALINK_SOUND, null, values);
		db.close();
		return ret;
	}
	public void updateOTAugrade(long _id, int  isSupportUpgrade)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("path", isSupportUpgrade);
		db.update(TABLE_EALINK_SOUND, values, "_id = '" + String.valueOf(_id) + "'", null);
		db.close();
		
	}
	
	
	public void updateEalinkWifi(long _id, int WIFI )
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("WIFI", WIFI);
		db.update(TABLE_EALINK_SOUND, values, "_id = '" + String.valueOf(_id) + "'", null);
		db.close();
		
	}
	
	
	public void updateEalinkSound(long _id, String path, String sound)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("path", path);
		values.put("sound", sound);
		db.update(TABLE_EALINK_SOUND, values, "_id = '" + String.valueOf(_id) + "'", null);
		db.close();
	}
	
	public  void rmvEalinkSound()
	{
		
	}
	
	public long addEalinkbBindSet()
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid_alarm", "");
		values.put("dev_uid_camera", "");
		values.put("dev_uid_unlock", "");
		values.put("time", "0");
		long ret = db.insertOrThrow(TABLE_BIND_SET, null, values);
		db.close();
		return ret;
	}
	
	public void updateEalinkBindAlarm(Long id, String UID)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid_alarm", UID);
		db.update(TABLE_BIND_SET, values, "_id = '" + String.valueOf(id) + "'", null);
		db.close();
		
	}
	
	public void RmvEalinkBindAlarm(long _id,String UID)
	{
		
	}
	
	public void updateEalinkBindCamera(Long id, String UID)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid_camera", UID);
		db.update(TABLE_BIND_SET, values, "_id = '" + String.valueOf(id) + "'", null);
		db.close();
		
	}
	public void updateEalinkTryToUnLockTime(Long id, long Time)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time", Time);
		db.update(TABLE_BIND_SET, values, "_id = '" + String.valueOf(id) + "'", null);
		db.close();
		
	}
	

	
	public void RmvEalinkBindCamera(long _id,String UID)
	{
		
	}
	
	public void updateEalinkBindUnlock(long id,String UID)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid_unlock", UID);
		db.update(TABLE_BIND_SET, values, "_id = '" + String.valueOf(id) + "'", null);
		db.close();
	}
	public void RmvEalinkBindUnlock(long _id,String UID)
	{
		
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DB_FILE = "IOTCamViewer.db";
		private static final int DB_VERSION = 7;

		private static final String SQLCMD_CREATE_TABLE_DEVICE = "CREATE TABLE " + TABLE_DEVICE + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_nickname			NVARCHAR(30) NULL, " + "dev_uid				VARCHAR(20) NULL, " + "dev_name				VARCHAR(30) NULL, " + "dev_pwd				VARCHAR(30) NULL, "
				+ "view_acc				VARCHAR(30) NULL, " + "view_pwd				VARCHAR(30) NULL, " + "event_notification 	INTEGER, " + "ask_format_sdcard		INTEGER," + "camera_channel			INTEGER, " +  "snapshot				BLOB," +  "auto_time_sync INTEGER"+");";

		private static final String SQLCMD_CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE " + TABLE_SEARCH_HISTORY + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "search_event_type	INTEGER, " + "search_start_time	INTEGER, " + "search_stop_time	INTEGER" + ");";

		private static final String SQLCMD_CREATE_TABLE_SNAPSHOT = "CREATE TABLE " + TABLE_SNAPSHOT + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "file_path			VARCHAR(80), " + "time				INTEGER" + ");";

		private static final String SQLCMD_CREATE_TABLE_REMOVE_LIST = "CREATE TABLE " + TABLE_REMOVE_LIST + "(" + "uid VARCHAR(20) NOT NULL PRIMARY KEY " +")";
		
		private static final String SQLCMD_DROP_TABLE_DEVICE = "drop table if exists " + TABLE_DEVICE + ";";

		private static final String SQLCMD_DROP_TABLE_SEARCH_HISTORY = "drop table if exists " + TABLE_SEARCH_HISTORY + ";";

		private static final String SQLCMD_DROP_TABLE_SNAPSHOT = "drop table if exists " + TABLE_SNAPSHOT + ";";

		private static final String SQLCMD_DROP_TABLE_REMOVE_LIST = "drop table if exists " + TABLE_REMOVE_LIST + ";";
		
		//private static final String SQLCMD_CREATE_TABLE_SNAPSHOT = "CREATE TABLE " + TABLE_SNAPSHOT + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "file_path			VARCHAR(80), " + "time				INTEGER" + ");";
		//march
		private static final String SQLCMD_CREATE_TABLE_VISIT_RECORD = "CREATE TABLE " + TABLE_VISIT_RECORD  + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "snapshot			BLOB, " + "time				VARCHAR(64)" + ");";
		
		private static final String SQLCMD_CREATE_TABLE_BIND_ALARM =  "CREATE TABLE " + TABLE_BIND_SET + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid_alarm			VARCHAR(20) NULL, " + "dev_uid_camera			VARCHAR(20) NULL, " + "dev_uid_unlock				VARCHAR(20) NULL ,"+ "time				VARCHAR(20) NULL" + ");";
		/*private static final String SQLCMD_CREATE_TABLE_BIND_CAMERA=  "CREATE TABLE " + TABLE_BIND_CAMERA + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "channel			BLOB, " + "time				VARCHAR(64)" + ");";
		private static final String SQLCMD_CREATE_TABLE_BIND_UNLOCK = "CREATE TABLE " + TABLE_BIND_UNLOCK + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "channel			BLOB, " + "time				VARCHAR(64)" + ");";*/
		
		private static final String SQLCMD_CREATE_TABLE_EALINK_SOUND = "CREATE TABLE " + TABLE_EALINK_SOUND  + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "path			VARCHAR(256) NULL, " + "sound				VARCHAR(32) NULL," +  "WIFI			VARCHAR(8) NULL"+ ");";
		
		
		public DatabaseHelper(Context context) {
			super(context, DB_FILE, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(SQLCMD_CREATE_TABLE_DEVICE);
			db.execSQL(SQLCMD_CREATE_TABLE_SEARCH_HISTORY);
			db.execSQL(SQLCMD_CREATE_TABLE_SNAPSHOT);
			db.execSQL(SQLCMD_CREATE_TABLE_REMOVE_LIST);
			//march
			db.execSQL(SQLCMD_CREATE_TABLE_VISIT_RECORD);
			db.execSQL(SQLCMD_CREATE_TABLE_BIND_ALARM);
			db.execSQL(SQLCMD_CREATE_TABLE_EALINK_SOUND);
			/*db.execSQL(SQLCMD_CREATE_TABLE_BIND_UNLOCK);*/
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			//db.execSQL(SQLCMD_DROP_TABLE_DEVICE);
			//db.execSQL(SQLCMD_DROP_TABLE_SEARCH_HISTORY);
			//db.execSQL(SQLCMD_DROP_TABLE_SNAPSHOT);
			//db.execSQL(SQLCMD_DROP_TABLE_REMOVE_LIST);
			if(oldVersion == 6)
				db.execSQL(SQLCMD_CREATE_TABLE_REMOVE_LIST);
			
			//onCreate(db);
		}

	}
}
