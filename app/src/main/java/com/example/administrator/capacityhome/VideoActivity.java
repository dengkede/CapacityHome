package com.example.administrator.capacityhome;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;

import utils.CreateVideoThumbnail;
import widget.DLVideoView;

public class VideoActivity extends Activity
         {
    private String url = " \"/up_files/order/20180507/1dc85e2ce0c363f5d4df348c8dfa057a.mp4\"";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ImageView img = (ImageView) findViewById(R.id.img);
        DLVideoView videoView = (DLVideoView) findViewById(R.id.videoView1);
        String path_video = getIntent().getStringExtra("path_video");
       // img.setImageBitmap(CreateVideoThumbnail.createVideoThumbnail(path_video, MediaStore.Images.Thumbnails.MINI_KIND));
     //  String path =  Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/VID_20180511_135932.mp4";
        /***
         * * 将播放器关联上一个音频或者视频文件
         * * videoView.setVideoURI(Uri uri)
         * * videoView.setVideoPath(String path)
         * * 以上两个方法都可以。       */
       // videoView.setVideoPath(path);
        //           /**       * w为其提供一个控制器，控制其暂停、播放……等功能       */
//        String path_video = getIntent().getStringExtra("url");
        //img.setImageBitmap(CreateVideoThumbnail.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND));
       // videoView.setVideoURI(Uri.parse(path_video));
      //  videoView.setVideoPath(path_video);
        videoView.setMediaController(new MediaController(this));
        /**       * 视频或者音频到结尾时触发的方法       */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("通知", "完成");
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("通知", "播放中出现错误");
                return false;
            }
        });
        videoView.setVideoPath(path_video);
        videoView.requestFocus();
        videoView.start();
    }
}

