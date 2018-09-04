package utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by TanBo on 2017/3/22.
 * 倒计时计时器
 */

public class Timer {

   public static void timeCount(final TextView textView){
       textView.setClickable(false);
       textView.setAlpha(0.5f);
       CountDownTimer timer = new CountDownTimer(60000, 1000) {
           @Override
           public void onTick(long millisUntilFinished) {

               textView.setText((millisUntilFinished / 1000) + " s");
           }

           @Override
           public void onFinish() {
               textView.setEnabled(true);
               textView.setClickable(true);
               textView.setAlpha(1.0f);
               textView.setText("重新获取");
           }
       }; timer.start();
   }

    /**
     * 判断按钮是否可再次点击
     * @param view
     * @param isClicble
     */
    public static void setViewClicble(View view, boolean isClicble){
        view.setClickable(isClicble);
        if(isClicble){
            view.setAlpha(1.0f);
        }else{
            view.setAlpha(0.5f);
        }
    }



}
