package com.example.administrator.capacityhome.smarthome.video.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Zed on 2017/6/8 15:09.
 * dec:
 */

//1 https://api.ipify.org/?format=json
//2 https://ip.nf/me.json（此server可以直接获取到地址信息）
//3 http://ip-api.com/json/?fields=country,status,query,message（此server可以直接获取到地址信息）


public class IPUtils {

	private final String TAG = IPUtils.class.getSimpleName();

	private final String IPURL01      = "http://ip-api.com/json/?fields=country,status,query,message";
	private final String IPURL02      = "https://api.ipify.org/?format=json";
	private final String IPURL03      = "https://ip.nf/me.json";
	private final int    HANDLER_SHOW = 100;

	private final int TIME_OUT = 5 * 1000;

	private Object m_wait = new Object();
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Context mContext;
	private ThreadPoolProxy mThreadPoolProxy;
	public static boolean isPushOver = false;
	private String IP;


	public IPUtils(Context context) {
		mContext = context;
	}

	public void startGetIP() {
		if(PushUtils.checkPlayServices(mContext)){
			startGetCountryTask();
			mTimer = new Timer();
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					Log.i(TAG, "-----------interrupt-----------");
					onDestroy();
					startGetSystemLanguage();
				}
			};
			mTimer.schedule(mTimerTask, TIME_OUT);
		}else {
			PushUtils.startPush(PushUtils.CHINA, mContext);
			Message message = mHandler.obtainMessage();
			message.what = HANDLER_SHOW;
			message.obj = "当前手机不支持google server 使用百度push";
			mHandler.sendMessage(message);
		}
	}

	private void startGetCountryTask() {
		mThreadPoolProxy = ThreadPoolProxyFactory.createNormalThreadPoolProxy();
		mThreadPoolProxy.execute(new URLRunnable(IPURL01));
		mThreadPoolProxy.execute(new URLRunnable(IPURL02));
		mThreadPoolProxy.execute(new URLRunnable(IPURL03));
	}


	private class URLRunnable implements Runnable {

		private String url;

		public URLRunnable(String url) {
			this.url = url;
		}

		@Override
		public void run() {

			String result = GetNetIp(url);
			synchronized (m_wait) {
				if (result != null && !isPushOver) {
					isPushOver = true;
					onDestroy();
					final String country = jsonString2Country(result);
					PushUtils.startPush(country, mContext);

					Message message = mHandler.obtainMessage();
					message.what = HANDLER_SHOW;
					message.obj = "IP ：" + IP + "  country : " + country;
					mHandler.sendMessage(message);
				}
			}
		}
	}


	private void startGetSystemLanguage() {

		boolean zhLanguage = isZHLanguage();
		PushUtils.startPush(zhLanguage ? PushUtils.CHINA : PushUtils.USA, mContext);

		Message message = mHandler.obtainMessage();
		message.what = HANDLER_SHOW;
		message.obj = "IP获取失败 检测当前系统语系是否为中文简体 " + zhLanguage;
		mHandler.sendMessage(message);
	}

	private boolean isZHLanguage() {
		boolean zh = false;
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();

		Log.i(TAG, " language == " + language);
		Log.i(TAG, " country == " + country);

		if ("zh".equals(language) && "cn".equals(country)) {
			zh = true;
		}
		return zh;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HANDLER_SHOW: {
//					Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};

	//remove all
	public void onDestroy() {

		Log.i(TAG, " --onDestroy-- ");

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mThreadPoolProxy != null) {
			mThreadPoolProxy.removeAl();
		}

	}


	private String GetNetIp(String url) {
		InputStream inStream = null;
		try {
			URL infoUrl = new URL(url);
			URLConnection connection = infoUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setConnectTimeout(TIME_OUT );
			httpConnection.setReadTimeout(TIME_OUT);
			httpConnection.setUseCaches(false);
			httpConnection.setRequestMethod("GET");
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					strber.append(line + "\n");
				}
				inStream.close();
				Log.i(TAG, "GetNetIp: " + strber.toString());
				return strber.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				inStream = null;
			}
		}

		return null;
	}

	private String jsonString2Country(String jsonString) {

		if (jsonString == null) {
			return null;
		}

		String ip = null;

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("query")) {//http://ip-api.com/json/?fields=country,status,query,message
				ip = jsonObject.getString("query");
				Log.i(TAG, IPURL01 + " 1 == " + ip);
			} else if (jsonObject.has("ip")) {
				ip = jsonObject.getString("ip");//https://api.ipify.org/?format=json
				JSONObject object = new JSONObject(ip);
				if (object.has("ip")) {
					ip = object.getString("ip");//https://ip.nf/me.json
					Log.i(TAG, IPURL03 + " 3 == " + ip);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, IPURL02 + " 2 == " + ip);
		}

		return locationCountry(ip);
	}

	private String locationCountry(String ip) {

//		if (ip == null) {
//			return null;
//		}
//
//		IP = ip;
//		// A File object pointing to your GeoIP2 or GeoLite2 database
//		// This creates the DatabaseReader object, which should be reused across
//		// lookups.
//		DatabaseReader reader = null;
//		try {
//			InputStream inputStream = mContext.getAssets().open("GeoLite2-Country.mmdb");
//			reader = new DatabaseReader.Builder(inputStream).build();
//			InetAddress ipAddress = InetAddress.getByName(ip);//"42.73.75.15"
//			// Replace "city" with the appropriate method for your database, e.g.,
//			// "country".
//			CountryResponse response = reader.country(ipAddress);
//			Country country = response.getCountry();
//			Log.i(TAG, country.getIsoCode());// 'US'
//			Log.i(TAG, country.getName());// 'United States'
//			Log.i(TAG, country.getNames().get("zh-CN"));// '美国'
//			return country.getIsoCode();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (GeoIp2Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}

	class GetCountryTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = GetNetIp(params[0]);
			Log.i(TAG, params[0] + "  ====result=====  " + result);
			return result;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(String result) {

			synchronized (m_wait) {
				if (result != null && !isPushOver) {
					isPushOver = true;
					final String country = jsonString2Country(result);
					if(country != null) {
						PushUtils.startPush(country, mContext);
						onDestroy();
					}

					((Activity) mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mContext, "IP ：" + IP + "  country : " + country, Toast.LENGTH_LONG).show();
						}
					});

				}
			}
		}
	}

}
