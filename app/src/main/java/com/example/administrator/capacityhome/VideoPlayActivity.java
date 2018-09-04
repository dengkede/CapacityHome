package com.example.administrator.capacityhome;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;

import java.io.File;
import java.util.HashMap;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import http.RequestTag;
import utils.ACache;
import utils.DisplayUtil;
import widget.NumberProgressView;

public class VideoPlayActivity extends BaseActivity {
    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private Handler handler;
    private ACache aCache;
    private PopupWindow popupWindow_down;
    private RelativeLayout layout_parent;
    private TextView tv_load_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decor = this.getWindow().getDecorView();
        aCache = ACache.get(this);
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_video_play);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                jzVideoPlayerStandard.thumbImageView.setImageBitmap((Bitmap) message.obj);
                return false;
            }
        });
        initView();
    }

    private void initView() {
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        final String path = getIntent().getStringExtra("path_video");
        findViewById(R.id.layout_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //View view = LayoutInflater.from(this).inflate(jzVideoPlayerStandard.getLayoutId(),null);
        //"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
        String s = path.substring(path.length()-RequestTag.BaseVieoUrl.length(),path.length()-4)+".mp4";
        if(fileIsExists(getSaveFilePath()+s)){
            showLoadingView();
            jzVideoPlayerStandard.setUp(getSaveFilePath()+s,
                    JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                    "");
            try {
                getNetVideoBitmap(getSaveFilePath() + s);
            }catch (Exception e){

            }
            dismissLoadingView();
            jzVideoPlayerStandard.setVisibility(View.VISIBLE);
        }else {
            ss(path);

        }
        // jzVideoPlayerStandard.startWindowFullscreen();
        // jzVideoPlayerStandard.thumbImageView.setImageBitmap(get(path));
        //  jzVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
      //  jzVideoPlayerStandard.thumbImageView.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.default_iv));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getNetVideoBitmap(path);
//            }
//        }).start();

        //jzVideoPlayerStandard.startFullscreen(this, JZVideoPlayerStandard.class, path, "");
//       jzVideoPlayerStandard.fullscreenButton.setVisibility(View.GONE);
        //jzVideoPlayerStandard.setState(JZVideoPlayerStandard.CURRENT_STATE_PAUSE);
        // jzVideoPlayerStandard.startButton.performClick();
        //jzVideoPlayerStandard.stop;

    }

    public Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
            Message message = new Message();
            message.obj = bitmap;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    /**
     * 获取视频文件截图
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */
    private Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    /**
     * 获取视频文件缩略图 API>=8(2.2)
     *
     * @param path 视频文件的路径
     * @param kind 缩略图的分辨率：MINI_KIND、MICRO_KIND、FULL_SCREEN_KIND
     * @return Bitmap 返回获取的Bitmap
     */
    private Bitmap getVideoThumb2(String path, int kind) {
        return ThumbnailUtils.createVideoThumbnail(path, kind);
    }

    private Bitmap getVideoThumb2(String path) {
        return getVideoThumb2(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }

    private Bitmap get(String paths) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//设置资源位置
//        String path="/storage/sdcard1"+"/Movies"+"/XiaomiPhone.mp4";
//绑定资源
        mmr.setDataSource(paths);
//获取第一帧图像的bitmap对象
        Bitmap bitmap = mmr.getFrameAtTime();
//加载到ImageView控件上
        return bitmap;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 下载视屏文件
     */
    private void downVideo(final String path){

        MyOkHttp.get().download(this, path, getSaveFilePath(), path.substring(path.length()-RequestTag.BaseVieoUrl.length(),path.length()-4)+".mp4", new DownloadResponseHandler() {
            @Override
            public void onFinish(File download_file) {
            //   aCache.put(path + "video.mp4",path + "video.mp4");
                try {
                    popupWindow_down.dismiss();
                    jzVideoPlayerStandard.setUp(download_file.getAbsolutePath(),
                            JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                            "");
                    getNetVideoBitmap(download_file.getAbsolutePath());
                }catch (Exception e){
                  deleteFile(new File(getSaveFilePath()+path.substring(path.length()-RequestTag.BaseVieoUrl.length(),path.length()-4)+".mp4"));
                }
                jzVideoPlayerStandard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(final long currentBytes, final long totalBytes) {
                Log.d("path",currentBytes/totalBytes+"");
                if(totalBytes<=1024){
                    deleteFile(new File(getSaveFilePath()+path.substring(path.length()-RequestTag.BaseVieoUrl.length(),path.length()-4)+".mp4"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastMessage("视频不存在或网络延迟太大已停止播放");
                            finish();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_load_dialog.setText("视屏加载中:"+(int)(currentBytes*100/totalBytes)+"%");
                        }
                    });
                }


            }

            @Override
            public void onFailure(String error_msg) {
                popupWindow_down.dismiss();
                jzVideoPlayerStandard.setVisibility(View.VISIBLE);
                deleteFile(new File(getSaveFilePath()+path.substring(path.length()-RequestTag.BaseVieoUrl.length(),path.length()-4)+".mp4"));
                toastMessage("视屏加载失败，请稍后重试。");
            }
        });
    }

    /**
     * 获取文件保存路径 sdcard根目录/download/文件名称
     *
     * @return
     */
    public static String getSaveFilePath() {
        String newFilePath = Environment.getExternalStorageDirectory() + "/Download/video/";
        return newFilePath;
    }
    //判断文件是否存在

    public boolean fileIsExists(String strFile)

    {

        try

        {

            File f=new File(strFile);

            if(!f.exists())

            {

                return false;

            }



        }

        catch (Exception e)

        {

            return false;

        }



        return true;

    }

    private void showpopupWindow_down(Long cur, Long total) {
        if (popupWindow_down == null) {
            layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
//            int height = getResources().getDisplayMetrics().widthPixels / 2.2;
            View popView = LayoutInflater.from(this).inflate(R.layout.loading_layout, null);
            popupWindow_down = new PopupWindow(popView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv_load_dialog = (TextView) popView.findViewById(R.id.tv_load_dialog);

            tv_load_dialog.setText("视屏加载中:"+(int)(cur/total)+"%");
            popupWindow_down.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow_down.setFocusable(true);
            popupWindow_down.setOutsideTouchable(true);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popupWindow_down.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {

                    }
                }
            };
            popupWindow_down.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, VideoPlayActivity.this);
                    if(!tv_load_dialog.getText().toString().equals("视屏加载中:100%")) {
                        finish();
                    }
                }
            });

        }
        dismissLoadingView();
        DisplayUtil.backgroundAlpha(0.3f, this);
        popupWindow_down.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
private void ss(final String path){
    final View mView =getWindow().getDecorView();
    mView.post( new Runnable( ) {
        @Override
        public void run() {
            if( null == popupWindow_down ){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showpopupWindow_down(0l,1l);
                        downVideo(path);
                    }
                });

            }
        }
    });
}
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
}
