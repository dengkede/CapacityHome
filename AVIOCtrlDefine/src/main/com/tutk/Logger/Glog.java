package com.tutk.Logger;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Gavin on 2014/1/10.
 */
public class Glog {

	/* ����亙�頦������哨蕭�����部嚙賢�頦������哨蕭嚙踝蕭����剖�頦����都嚙賢�頦����郭嚙賢�頦������剖�頦���� ��剖�嚙�
	 * ��閉嚙�����苛erbose (lowest priority) ���嚙賢�頦��Debug ���嚙�����背nfo ��閉嚙�����苦arning
	 * ��閉嚙�����胥rror ���嚙賢�頦��Fatal ��閉嚙�����范ilent (highest priority, on which nothing is ever
	 * printed) */

	static boolean mIsLogOn = true;

	public static void D(String tag, String message) {
		if (!mIsLogOn)
			return;

//		if (tag.equals("RECORD")) {
//			try {
//				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/James_says_HELLO/");
//				if (!rootFolder.exists()) {
//					rootFolder.mkdir();
//				}
//
//				FileOutputStream output = new FileOutputStream(rootFolder.getAbsolutePath() + "/Log_file.txt",true);
//				output.write(message.getBytes());
//				output.write("\n".getBytes());
//				output.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else if(tag.equals("EasyWiFi")){
//			try {
//				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/James_says_HELLO/");
//				if (!rootFolder.exists()) {
//					rootFolder.mkdir();
//				}
//
//				FileOutputStream output = new FileOutputStream(rootFolder.getAbsolutePath() + "/Log_easyWiFi.txt",true);
//				output.write(message.getBytes());
//				output.write("\n".getBytes());
//				output.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else if(tag.equals("Gianni")){
//			try {
//				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gianni_says_HI/");
//				if (!rootFolder.exists()) {
//					rootFolder.mkdir();
//				}
//
//				FileOutputStream output = new FileOutputStream(rootFolder.getAbsolutePath() + "/Log_Gianni.txt",true);
//				output.write(message.getBytes());
//				output.write("\n".getBytes());
//				output.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}else if(tag.equals("TestTool")){
//			try {
//				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestTool_says_OHAYOO/");
//				if (!rootFolder.exists()) {
//					rootFolder.mkdir();
//				}
//
//				FileOutputStream output = new FileOutputStream(rootFolder.getAbsolutePath() + "/Log_TestTool.txt",true);
//				output.write(message.getBytes());
//				output.write("\n".getBytes());
//				output.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		Log.d(tag, String.valueOf(message));
	}

	public static void D(String tag, String message, Throwable tr) {
		if (!mIsLogOn)
			return;

		Log.d(tag, String.valueOf(message), tr);
	}

	public static void E(String tag, String message) {
		if (!mIsLogOn)
			return;

//		if(tag.equals("Gianni")){
//			try {
//				File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gianni_says_HI/");
//				if (!rootFolder.exists()) {
//					rootFolder.mkdir();
//				}
//
//				FileOutputStream output = new FileOutputStream(rootFolder.getAbsolutePath() + "/Log_Gianni.txt",true);
//				output.write(message.getBytes());
//				output.write("\n".getBytes());
//				output.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		Log.e(tag, String.valueOf(message));
	}

	public static void E(String tag, String message, Throwable tr) {
		if (!mIsLogOn)
			return;

		Log.e(tag, String.valueOf(message), tr);
	}

	public static void I(String tag, String message) {
		if (!mIsLogOn)
			return;

		Log.i(tag, String.valueOf(message));
	}

	public static void I(String tag, String message, Throwable tr) {
		if (!mIsLogOn)
			return;

		Log.i(tag, String.valueOf(message), tr);
	}

	public static void V(String tag, String message) {
		if (!mIsLogOn)
			return;

		Log.v(tag, String.valueOf(message));
	}

	public static void V(String tag, String message, Throwable tr) {
		if (!mIsLogOn)
			return;

		Log.v(tag, String.valueOf(message), tr);
	}

	public static void W(String tag, String message) {
		if (!mIsLogOn)
			return;

		Log.w(tag, String.valueOf(message));
	}

	public static void W(String tag, String message, Throwable tr) {
		if (!mIsLogOn)
			return;

		Log.w(tag, String.valueOf(message), tr);
	}

    public static void cleanLogFile(){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/James_says_HELLO/Log_file.txt");
		if(file.exists()){
			file.delete();
		}
	}

	public static String getString(byte[] data) {

		StringBuilder sBuilder = new StringBuilder();

		for (int i = 0; i < data.length; i++) {

			if (data[i] == 0x0)
				break;

			sBuilder.append((char) data[i]);
		}

		return sBuilder.toString();
	}

	public static String getGianni_byteString(byte[] data) {

		String strDebug = "bytearray("+data.length+"):";
		int debuglen = 0;
		int data_len = 0;
		int cmd_len = 0;
		if(data != null){
			data_len = (0x0FFFF & ((data[4] & 0x00FF) | ((data[5] << 8) & 0x0FF00)));//data[4]+4+2+1;
			cmd_len = data_len + 4 + 2 + 2;
			strDebug+= "DATA_LEN("+data_len+")";
		}

		for( byte v : data ) {
			strDebug += String.format( "0x%02X ", v);
			debuglen++;
			if (debuglen == data.length || debuglen == cmd_len){
				break;
			}

		}
		return strDebug;
	}

	public static String getGianni_byte10String(byte[] data) {

		if(data.length < 6 ){
			return "[getGianni_byte10String] error: data length < 6!";
		}

		String strDebug = "bytearray("+data.length+"):";
		int debuglen = 0;
		int data_len = 0;
		int cmd_len = 0;
		if(data != null){
			data_len = (0x0FFFF & ((data[4] & 0x00FF) | ((data[5] << 8) & 0x0FF00)));
			cmd_len = data_len + 4 + 2 + 2;
			strDebug+= "DATA_LEN("+data_len+")";
		}

		for( byte v : data ) {
			strDebug += ( v & 0x0FF ) + " ";
			debuglen++;
			if (debuglen == data.length || debuglen == cmd_len){
				break;
			}

		}
		return strDebug;
	}



}
