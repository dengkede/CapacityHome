package com.example.administrator.capacityhome.smarthome.video.util;

import android.content.Context;


/**
 * Created by Zed on 2017/6/8 16:27.
 * dec:
 */

public class PushUtils {

	public static final String LOCATIONS  = "locations";
	public static final String COUNTRY    = "countryCode";
	public static final String CHINA      = "CN";
	public static final String USA        = "US";
	public static final String DEFAULT    = "-10000";
	public static final String TAG        = PushUtils.class.getSimpleName();
	public static       boolean isPushOver = false;

	public static void startPush(String locationCountryCode, final Context context) {

//		Log.i(TAG, " locationCountryCode == " + locationCountryCode);//CN HK
//
//		if (locationCountryCode != null) {
//			SharedPreferences sharedPreferences = context.getSharedPreferences(LOCATIONS, Context.MODE_PRIVATE);
//			String countryCode = sharedPreferences.getString(COUNTRY, DEFAULT);
//			if (!DEFAULT.equals(countryCode)) {//有保存数据
//				//国外到国内
//				if (CHINA.equals(locationCountryCode) && !locationCountryCode.equals(countryCode)) {
//					Log.i(TAG, " 国外到国内 清空国外mapping");
//					TPNSManager.URL = TPNSManager.FOREIGN_PUSH;
//					for (DeviceInfo deviceInfo : XMMainActivity.DeviceList) {
//						GooglePushUtils.unmapping(context, deviceInfo.UID);
//					}
//				}
//				//国内到国外
//				else if (!CHINA.equals(locationCountryCode) && CHINA.equals(countryCode)) {
//					Log.i(TAG, " 国内到国外 清空国内mapping");
//					TPNSManager.URL = TPNSManager.CHINA_PUSH;
//					for (DeviceInfo deviceInfo : XMMainActivity.DeviceList) {
//						JPushUtils.unmapping(context, deviceInfo.UID);
//					}
//				}
//			}
//			//设置新推送链接
//			if (CHINA.equals(locationCountryCode)) {
//				TPNSManager.mInland = true;
//				TPNSManager.URL = TPNSManager.CHINA_PUSH;
//			} else {
//				TPNSManager.mInland = false;
//				TPNSManager.URL = TPNSManager.FOREIGN_PUSH;
//			}
//			//保存此次地点信息
//			sharedPreferences.edit().putString(COUNTRY, locationCountryCode).commit();
//		}
//
//		Log.i(TAG, " mInland == " + TPNSManager.mInland);
//
//		JPushInterface.setDebugMode(false);
//		JPushInterface.init(context.getApplicationContext());
//
//		if (TPNSManager.mInland) {
//			JPushInterface.resumePush(context.getApplicationContext());
//			String registrationID = JPushInterface.getRegistrationID(context.getApplicationContext());
//			if (registrationID != null) {
//				JPushUtils.JPush_TOKEN = registrationID;
//				JPushUtils.register(context);
//				TPNSManager.check_mapping_list(context);
//			}
//		} else {
//			JPushInterface.stopPush(context.getApplicationContext());
//			new Thread() {
//				@Override
//				public void run() {
//					super.run();
//					InstanceID instanceID = InstanceID.getInstance(context);
//					try {
//						String token = instanceID.getToken(GooglePushUtils.PUSH_GOOGLE_SENDERID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
//						if (token != null) {
//							GooglePushUtils.TOKEN_GOOGLE = token;
//							GooglePushUtils.register(context);
//							TPNSManager.check_mapping_list(context);
//						}
//						Log.i(TAG, "token:" + token);
//					} catch (IOException e) {
//						Log.e(TAG, "Unable to register with GCM. " + e.getMessage());
//						((Activity) context).runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								Toast.makeText(context, "Unable to register with GCM : time out", Toast.LENGTH_SHORT).show();
//							}
//						});
//					}
//				}
//			}.start();
//		}
	}

	public static boolean checkPlayServices(Context context) {
//		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//		int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//		Log.i(TAG, "google server resultCode:" + resultCode);
//		if (resultCode != ConnectionResult.SUCCESS) {
////			if (apiAvailability.isUserResolvableError(resultCode)) {
////				apiAvailability.getErrorDialog(this, resultCode, 9000).show();
////			} else {
////				Log.i(TAG, "This device is not supported.");
////			}
//			return false;
//		}
		return true;
	}

}
