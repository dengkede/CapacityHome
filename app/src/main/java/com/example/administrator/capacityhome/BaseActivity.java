package com.example.administrator.capacityhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.capacityhome.picture_scan.ImagePagerActivity;
import com.example.administrator.capacityhome.picture_scan.Pager_PicActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.tsy.sdk.myokhttp.MyOkHttp;

import java.util.ArrayList;

import butterknife.ButterKnife;
import widget.LoadingView;
import widget.LoadingView2;

public class BaseActivity extends RxAppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState, persistentState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private ProgressDialog progressDialog_pay;//正在充值

    protected void setText(int resId, String text) {
        TextView textView = (TextView) findViewById(resId);
        textView.setText(text);
    }

    protected void setText(int resId, int textId) {
        TextView textView = (TextView) findViewById(resId);
        textView.setText(textId);
    }

    protected void setSubmitBtn(String content) {
        setText(R.id.top_submit_btn, content);
    }

    protected void setPageTitle(String title) {
        setText(R.id.page_title, title);
    }

    protected void backActivity() {
        backActivity(R.id.return_btn);
    }

    protected void backActivity(int resId) {
        Button returnBtn = (Button) findViewById(resId);
        returnBtn.setVisibility(View.VISIBLE);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void submitBtnVisible(String text, View.OnClickListener listener) {
        TextView submitBtn = (TextView) findViewById(R.id.top_submit_btn);
        submitBtn.setText(text);
        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setOnClickListener(listener);

    }

    protected void startActivity(Context context, Class activity) {
        startActivity(new Intent(context, activity));
    }

    protected void startActivity_Bundle(Context context, Class activity, Bundle bundle) {
        startActivityForResult(new Intent(context, activity).putExtra("bundle", bundle), 2);
    }

    public void startActivity_ImagrPager(Context context, int position, ArrayList urls, boolean isFile) {
//        Intent intent = new Intent(this, ImagePagerActivity.class);
        Intent intent = new Intent(this, Pager_PicActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        //intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("isFile", isFile);
        startActivityForResult(intent, 2);
    }

    protected void startActivity_finish(Context context, Class activity) {
        startActivity(new Intent(context, activity));
        finish();
    }

    protected String getViewText(int viewId) {
        TextView textView = (TextView) findViewById(viewId);
        return textView.getText().toString();
    }

    protected void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastMessage(int msg) {
        Toast.makeText(this, this.getResources().getString(msg), Toast.LENGTH_SHORT).show();
    }

    protected String getEditStringWithTrim(EditText editText) {
        return editText.getText().toString().trim();
    }

    public void showLoadingView() {

        if (progressDialog == null) {
            progressDialog = new LoadingView(this, R.style.loading_dialog_style);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//默认
        //progressDialog.setCancelable(true);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    public void showLoadingView2(String title) {

        if (progressDialog2 == null) {
            progressDialog2 = new LoadingView2(this, R.style.loading_dialog_style, title);
        }
        progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);//默认
        //progressDialog.setCancelable(true);
        if (!progressDialog2.isShowing()) {
            progressDialog2.show();
        }

    }

    public void showLoadingView_pay() {

        if (progressDialog_pay == null) {
            progressDialog_pay = new LoadingView2(this, R.style.loading_dialog_style, "正在充值。。。");
        }
        progressDialog_pay.setProgressStyle(ProgressDialog.STYLE_SPINNER);//默认
        //progressDialog.setCancelable(true);
        if (!progressDialog_pay.isShowing()) {
            progressDialog_pay.show();
        }

    }

    public void dismiss_pay() {
        if (progressDialog_pay != null && progressDialog_pay.isShowing()) {
            progressDialog_pay.dismiss();
        }
    }

    /**
     * 判断弹框是否消失
     */
    public boolean getShow2State() {
        if (progressDialog2 == null) {
            return false;
        } else {
            if (progressDialog2.isShowing()) {
                return true;
            }
        }
        return false;
    }

    public void setProgressDialog2Null() {
        progressDialog2 = null;
    }

    public void setShow2Tile(String title) {
        progressDialog2.dismiss();
        progressDialog2 = new LoadingView2(this, R.style.loading_dialog_style, title);
        progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);//默认
        //progressDialog.setCancelable(true);
        if (!progressDialog2.isShowing()) {
            progressDialog2.show();
        }
        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //倒计时结束
                if (progressDialog2 != null && progressDialog2.isShowing()) {
                    progressDialog2.dismiss();
                    setProgressDialog2Null();
                }

            }
        };
        timer.start();
    }


    public void dismissLoadingView() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void dismissLoadingView2() {
        if (progressDialog2 != null) {
            if (progressDialog2.isShowing()) {
                progressDialog2.dismiss();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //   outState.putSerializable("static_user", UserToken.getInstance().getUserModel());
        super.onSaveInstanceState(outState);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
//            if (UserToken.getInstance().getUserModel() == null) {
//                UserToken.getInstance().setUserModel((UserModel) savedInstanceState.getSerializable("static_user"));
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyOkHttp.get().cancel(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 从右往左退出
     */
    protected void finish_rightToLeft() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    /**
     * 从左往右进入
     */
    protected void startActivity_leftToRight(Context context, Class activity) {
        startActivity(new Intent(context, activity));
        overridePendingTransition(R.anim.slide_in_right, R.anim.animo_no);
    }

    /**
     * congshg上往下
     */
    protected void startActivity_topToBottom() {
        finish();
        overridePendingTransition(0, R.anim.activity_out);
    }
}
