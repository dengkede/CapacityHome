package com.example.administrator.capacityhome.smarthome.video.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.example.administrator.capacityhome.smarthome.video.appteam.DatabaseManager;
import com.example.administrator.capacityhome.smarthome.video.obj.DeviceInfo;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;
import com.example.administrator.capacityhome.smarthome.video.util.ResetDBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class XMMainActivity {

	public static final String TAG = "XMMainActivity";

	public static List<MyCamera> CameraList = Collections.synchronizedList(new ArrayList<MyCamera>());                         //new ArrayList<MyCamera>();
	public static List<DeviceInfo> DeviceList = Collections.synchronizedList(new ArrayList<DeviceInfo>());


	public static void initCameraList(Context context, boolean bFirstTime) {
		if(ResetDBHelper.getConfig( context ) ){
			DatabaseManager manager = new DatabaseManager(context);
			manager.clearDevice();
			ResetDBHelper.storeConfig( context );
		}

		if (bFirstTime) {
			CameraList.clear();
			DeviceList.clear();
			DatabaseManager manager = new DatabaseManager(context);
			SQLiteDatabase db = manager.getReadableDatabase();
			Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE,
					new String[]{"_id", "dev_nickname", "dev_uid",
							"dev_name", "dev_pwd", "view_acc", "view_pwd",
							"event_notification", "camera_channel", "snapshot",
							"ask_format_sdcard", "auto_time_sync"}, null,
					null, null, null, "_id LIMIT "
							+ MainActivity_video.CAMERA_MAX_LIMITS);

			while (cursor.moveToNext()) {
				long db_id = cursor.getLong(0);
				String dev_nickname = cursor.getString(1);
				String dev_uid = cursor.getString(2);
				String view_acc = cursor.getString(5);
				String view_pwd = cursor.getString(6);
				int event_notification = cursor.getInt(7);
				int channel = cursor.getInt(8);
				byte[] bytsSnapshot = cursor.getBlob(9);
				int ask_format_sdcard = cursor.getInt(10);
				int time_sync = cursor.getInt(11);
				Bitmap snapshot = (bytsSnapshot != null && bytsSnapshot.length > 0) ? DatabaseManager
						.getBitmapFromByteArray(bytsSnapshot) : null;

				MyCamera camera = new MyCamera(context,dev_nickname, dev_uid, view_acc, view_pwd);
				DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(),
						dev_nickname, dev_uid, view_acc, view_pwd, "",
						event_notification, channel, snapshot, (int) 0,
						time_sync, 0);
				dev.ShowTipsForFormatSDCard = ask_format_sdcard == 1;
				DeviceList.add(dev);
				CameraList.add(camera);
			}
			cursor.close();
			db.close();

		}
	}


	public static void showAlert(Context context, CharSequence title,
                                 CharSequence message, CharSequence btnTitle) {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setTitle(title);
		dlgBuilder.setMessage(message);
		dlgBuilder.setPositiveButton(btnTitle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

}
