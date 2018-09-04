package com.example.administrator.capacityhome.smarthome.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.administrator.capacityhome.R;
import com.example.administrator.capacityhome.smarthome.video.zxing.CameraManager;
import com.example.administrator.capacityhome.smarthome.video.zxing.CaptureActivityHandler;
import com.example.administrator.capacityhome.smarthome.video.zxing.InactivityTimer;
import com.example.administrator.capacityhome.smarthome.video.zxing.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

public class qr_codeActivity extends Activity implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private SurfaceHolder surfaceHolder;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_action);
        CameraManager.init(getApplication());
        inactivityTimer = new InactivityTimer(this);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
    }



    //<----------------------------------------------------------Following are zxing originally source code--------------------------------------------------------->//
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {//Scan again
            handler = new CaptureActivityHandler(this, decodeFormats,characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
    {


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result obj, Bitmap barcode){ //After scan completed
        inactivityTimer.onActivity();
        viewfinderView.drawResultBitmap(barcode);//When scan complete,show capture picture on scanner
        playBeepSoundAndVibrate();//Play beep and vibrate
        //if(obj.getText().matches(".*?\\|\\|.*?"))//����哨蕭��蕭�佗蕭嚙踝蕭嚙踝蕭嚙賢�嚙賡�嚙踝蕭rcode 嚙踝蕭�賢�鞈ｇ蕭�萇�嚙踝蕭嚙踝蕭��蕭��蕭     //嚙踝蕭�賢�鞈ｇ蕭�佗蕭嚙踝蕭嚙踝蕭嚙賢�嚙賡�嚙踝蕭嚙踝蕭嚙踝蕭��蕭��釭嚙踝蕭嚙踝蕭嚙賢�嚙賡�嚙踝蕭嚙踝蕭嚙練ber_id
        //{
        Intent intent = new Intent();
        intent = intent.setClass(qr_codeActivity.this,AddDeviceActivity.class);
        Bundle bundle =new Bundle();
        bundle.putString("SCAN_RESULT", obj.getText());
        //bundle.putString("tabhost", "scan_qrcode");
        intent.putExtras(bundle);
        qr_codeActivity.this.setResult(RESULT_OK, intent);
        finish();
        //else
        //{
        //	black_view.setVisibility(View.VISIBLE);//����哨蕭��蕭�綽蕭ew��釭�拙�頦����郭嚙賡�嚙踝蕭嚙踝蕭嚙踝蕭�刻�嚙質�嚙�		//	QR_CODE_CustomDialog failDialog = new QR_CODE_CustomDialog(widthPixels,heightPixels,qr_codeActivity.this,qr_codeActivity.this,R.style.MyDialog,decodeFormats,characterSet,black_view,"嚙踝蕭�剖�鞈ｇ蕭�佗蕭嚙賢�頦��嚙賢��哨蕭嚙質�嚙踝蕭嚙踝蕭��蕭�佗蕭嚙踝蕭嚙踝蕭嚙踝蕭"���嚙踝蕭�刻�嚙賡��哨蕭嚙踝蕭R Code ����哨蕭��蕭�佗蕭嚙賢�鞈�蕭嚙賢�嚙賡�嚙質�嚙踝蕭嚙踝蕭��蕭��釭嚙賢�頦��嚙賢�嚙賡�嚙質�嚙踝蕭�賢�嚙�"����剖�鞈ｇ蕭��蕭0);
        //	failDialog.show();
        //	CameraManager.get().stopPreview();//嚙踝蕭�賢�鞈ｇ蕭�佗蕭嚙踝蕭嚙賡��批�嚙賡�嚙踝蕭����剖�鞈ｇ蕭�佗蕭嚙踝蕭嚙踝蕭嚙踝蕭	//}


    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onBackPressed()
    {
        //嚙踝蕭�剖�頦����釭嚙賢�鞈⊿�嚙賢��剛�鞈迎蕭嚙踝蕭��ck嚙踝蕭嚙踝蕭嚙�		    //app_static_variables.set_member_id("");//����哨蕭��berid����哨蕭��蕭��蕭        //super.onBackPressed();//��釭�抬蕭嚙踝蕭����哨蕭嚙質�����剛�鞈迎蕭嚙踝蕭嚙踝蕭��蕭��悅k嚙踝蕭嚙踝蕭��蕭�佗蕭嚙踝蕭�迎蕭嚙踝蕭		    //Log.w("c2dm", "��釭�抬蕭嚙踝蕭�佗蕭嚙踝蕭嚙踝蕭嚙賢��哨蕭�鳴蕭嚙踝蕭嚙踝蕭��蕭��蕭嚙�);
        Intent intent = new Intent();
        intent = intent.setClass(qr_codeActivity.this,AddDeviceActivity.class);
        //Bundle bundle =new Bundle();
        //嚙踝蕭�剖�鞈ｇ蕭�佗蕭嚙踝蕭��蕭嚙踝蕭
        //bundle.putString("MEMBER_ID","");
        //bundle.putString("tabhost", "qrcode_activity");
        //intent.putExtras(bundle);
        qr_codeActivity.this.setResult(RESULT_CANCELED, intent);
        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}