package com.example.administrator.capacityhome.smarthome.video.obj;

import android.graphics.Bitmap;

import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.push.JpuchNotifyActivity;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.DefaultPushNotificationBuilder;

public class DeviceInfo {

	public long DBID;
	public String UUID;
	public String NickName;
	public String UID;
	public String View_Account;
	public String View_Password;
	public String Status;
	public Bitmap Snapshot;
	public int EventNotification;
	public int ChannelIndex;
	public boolean Online;
	public boolean ShowTipsForFormatSDCard;
	public int  TimeSync =0;  //0 no need  
	
	public int DeviceType ;
	public int MappType = 0;

	public int count;
	public byte bEnable1 ;
	public byte bEnable2;
	public int  nTime1;
	public int  nTime2;
	public int  nEnable =0 ;

	public boolean isUpgradeStatus = false;

	public DeviceInfo(long db_id, String uuid, String nickname, String uid, String view_acc, String view_pwd, String status, int event_notification,
                      int camera_channel, Bitmap snapshot , int deviceType, int sync, int mappType) {

		DBID = db_id;
		UUID = uuid;
		NickName = nickname;
		UID = uid;
		View_Account = view_acc;
		View_Password = view_pwd;
		Status = status;
		EventNotification = event_notification;
		ChannelIndex = camera_channel;
		Snapshot = snapshot;
		Online = false;
		ShowTipsForFormatSDCard = false; //march
		DeviceType = deviceType;
		TimeSync =sync;
		MappType = mappType;
	}

	public String getView_Password() {
		return View_Password;
	}

	public void setView_Password(String view_Password) {
		View_Password = view_Password;


	}
}
