package com.example.administrator.capacityhome.smarthome.video.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by lidacheng on 2017/12/29.
 */

public class MediaPlayerUtil {
    private static MediaPlayer mp;
    private static int currentnum = 0;

    public static void play(Context context, int soundResource, final int loopNum) {
        currentnum = 0;
        if (mp != null && mp.isPlaying()) {

            // TODO Auto-generated method stub
            mp.stop();
            mp.release();//释放资源
        }
        mp = MediaPlayer.create(context, soundResource);
        try {
            mp.reset();
            mp = MediaPlayer.create(context, soundResource);//重新设置要播放的音频
            mp.start();//开始播放
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Log.i("currentNum", currentnum + "");
                    currentnum++;
                    if (loopNum > currentnum) {
                        mp.start();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
        }
    }
}
