package com.example.administrator.capacityhome.smarthome.video.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.obj.MyCamera;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseActivity extends com.example.administrator.capacityhome.BaseActivity {
    private Timer mTimer;
    private TimerTask mTimerTask;

    private final long sec = 1000;
    private final long TIME_OUT = 15 * sec;
    private final long finish_TIME_OUT = 10 * sec;

    public int connect_count = 0;
    private Dialog mWait_PDlg;

    public abstract void errorState(String dev_uid, MyCamera camera);
    public abstract void finish_TIMEOUT();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public void controlTimeOutTask(final String dev_uid, final Boolean hideDlg) {
        if ( hideDlg ) {
            hideProgressDlg();
            // showProgressDlg();
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                for (MyCamera camera : XMMainActivity.CameraList) {
                    if (camera.getUID().equals(dev_uid)) {
                        errorState(dev_uid, camera);
                        break;
                    }
                }
            }
        };
        mTimer.schedule(mTimerTask, TIME_OUT);
    }

    public void startTimeOutTask(final String dev_uid) {
        //  showProDlg();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {

                stopTimeOutTask();
                hideProgressDlg();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (MyCamera camera : XMMainActivity.CameraList) {
                            if (camera.getUID().equals(dev_uid)) {
                                if(camera.mSID != -1) {
                                    errorState(dev_uid ,camera);
                                } else {
                                    String msg = getString(R.string.txt_communication_failed) + " sid " + camera.mSID;
                                    Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                                camera.disconnect();
                                break;
                            }
                        }
                    }
                });


            }
        };
        mTimer.schedule(mTimerTask, TIME_OUT);
    }

//    public void startFinishTimeOut() {
//        stopTimeOutTask();
//        hideProgressDlg();
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mWait_PDlg = new Dialog(BaseActivity.this, R.style.setting_dialog);
//                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View view = mInflater.inflate(R.layout.cus_dialoge, null);
//                TextView tv_stuta = (TextView)view.findViewById(R.id.tv_device_stuta);
//                tv_stuta.setText("Please Wait...");
//                mWait_PDlg.setContentView(view);
//
//                Window window = mWait_PDlg.getWindow();
//                window.setWindowAnimations(R.style.setting_dailog_animstyle);
//                mWait_PDlg.setCanceledOnTouchOutside(false);
//                mWait_PDlg.setCancelable(false);
//                mWait_PDlg.show();
//            }
//        });
//
//        mTimer = new Timer();
//        mTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                stopTimeOutTask();
//                hideProgressDlg();
//                finish_TIMEOUT();
//            }
//        };
//        mTimer.schedule(mTimerTask, finish_TIME_OUT);
//    }

//    private void showProDlg() {
//        stopTimeOutTask();
//        hideProgressDlg();
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mWait_PDlg = new Dialog(BaseActivity.this, R.style.setting_dialog);
//                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View view = mInflater.inflate(R.layout.cus_dialoge, null);
//                mWait_PDlg.setContentView(view);
//
//                Window window = mWait_PDlg.getWindow();
//                window.setWindowAnimations(R.style.setting_dailog_animstyle);
//                mWait_PDlg.setCanceledOnTouchOutside(false);
//                mWait_PDlg.setCancelable(false);
//                mWait_PDlg.show();
//            }
//        });
//    }

    public void stopTimeOutTask() {
        connect_count = 0;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

//    public void showProgressDlg(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mWait_PDlg == null) {
//                    mWait_PDlg = new Dialog(BaseActivity.this, R.style.setting_dialog);
//                    LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View view = mInflater.inflate(R.layout.cus_dialoge, null);
//                    mWait_PDlg.setContentView(view);
//
//                    Window window = mWait_PDlg.getWindow();
//                    window.setWindowAnimations(R.style.setting_dailog_animstyle);
//                    mWait_PDlg.setCanceledOnTouchOutside(false);
//                    mWait_PDlg.setCancelable(false);
//                    mWait_PDlg.show();
//                }
//            }
//        });
//    }

    public void hideProgressDlg() {
        if (mWait_PDlg == null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mWait_PDlg.dismiss();
                    mWait_PDlg = null;
                } catch (Exception e) {}
            }
        });
    }
}

