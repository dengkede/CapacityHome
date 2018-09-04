package com.example.administrator.capacityhome.smarthome.video.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.example.administrator.capacityhome.smarthome.video.activity.MainActivity_video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class TPNSTask extends AsyncTask<String, Void, String> {

	static {
		//ssl 证书不过审
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	private final String TAG  = TPNSTask.class.getSimpleName();
	private final int    TIME = 10 * 1000;
	private String uid;
	private Context mContext;
	private int mType = -1;

	private Timer mTimer;
	private TimerTask mTimerTask;

	public TPNSTask(Context context, int type) {
		mContext = context;
		mType = type;
	}

	@Override
	protected String doInBackground(String... params) {

		if (params.length > 1 && params[1] != null) {
			uid = params[1];
		}

		Log.i(TAG, "url:" + params[0]);

		startTimeOut(mContext, mType, params);

		HttpURLConnection conn = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (params != null && params.length != 0) {
			try {
				URL url = new URL(params[0]);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line).append("\n");
				}

				reader.close();
				reader = null;
				conn.disconnect();
				conn = null;

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
						reader = null;
					}
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return builder.toString();
	}

	protected void onPostExecute(String response) {
		Log.i(TAG, "response:" + response);

		if (response.contains("200 Success.")) {
			stopTimeOut();
			Intent intent = new Intent(MainActivity_video.MAPPING);
			Bundle bundle = new Bundle();
			bundle.putString("uid", uid);
			bundle.putInt("type", mType);
			intent.putExtras(bundle);
			mContext.sendBroadcast(intent);
		}
	}

	@Override
	protected void onCancelled(String s) {
		super.onCancelled(s);
		stopTimeOut();
	}

	private void startTimeOut(final Context context, final int type, final String... params) {
		stopTimeOut();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				TPNSTask tpnsTask = new TPNSTask(context, type);
				tpnsTask.execute(params);
				mTimer.cancel();
				mTimerTask.cancel();
				mTimerTask = null;
				mTimerTask = null;
			}
		};
		mTimer.schedule(mTimerTask, TIME);
	}

	private void stopTimeOut() {
		if (mTimer != null && mTimerTask != null) {
			mTimer.cancel();
			mTimerTask.cancel();
			mTimer = null;
			mTimerTask = null;
		}
	}
}
