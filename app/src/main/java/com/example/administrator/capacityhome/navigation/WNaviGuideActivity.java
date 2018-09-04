package com.example.administrator.capacityhome.navigation;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.baidu.platform.comapi.walknavi.widget.ArCameraView;

/**
 * AR步行导航
 * 步行导航界面
 */
public class WNaviGuideActivity extends Activity {

    private final static String TAG = WNaviGuideActivity.class.getSimpleName();

    private WalkNavigateHelper mNaviHelper;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// 获取导航控制类
        mNaviHelper = WalkNavigateHelper.getInstance();
        try {
            View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
            if (view != null) {
                setContentView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


/**
 * 要实现普通导航与AR导航的切换，需要在onWalkNaviModeChange回调函数中调用mNaviHelper.switchWalkNaviMode(WNaviGuideActivity.this, mode, listener);
 */
        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
                Log.d(TAG, "onWalkNaviModeChange : " + mode);
                mNaviHelper.switchWalkNaviMode(WNaviGuideActivity.this, mode, listener);
            }

            @Override
            public void onNaviExit() {
                Log.d(TAG, "onNaviExit");
            }
        });
// 开始导航
        boolean startResult = mNaviHelper.startWalkNavi(WNaviGuideActivity.this);
        Log.e(TAG, "startWalkNavi result : " + startResult);

        mNaviHelper.setTTsPlayer(new IWTTSPlayer() {
            @Override
            public int playTTSText(final String s, boolean b) {
                Log.d("tts", s);
                return 0;
            }
        });

        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {
                /**
                 * 诱导图标更新
                 *
                 * @param icon
                 */
            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {
                /**
                 * 诱导枚举信息
                 *
                 * @param routeGuideKind 诱导信息 see {@link RouteGuideKind}
                 */
            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {
                /**
                 * 诱导信息
                 *
                 * @param charSequence 第一行显示的信息，比如“沿当前道路”
                 * @param charSequence1 第二行显示的信息，比如“向东出发”，第二行信息也可能为空
                 */
            }
            /**
             * 总的剩余距离
             *
             * @param charSequence 已经带有单位
             */
            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }
            /**
             * 总的剩余时间
             *
             * @param charSequence 已经带有单位
             */
            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {

            }

            /**
             * GPS状态发生变化，来自诱导引擎的消息
             *
             * @param charSequence GPS信息
             * @param drawable
             */
            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {


            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onReRouteComplete() {

            }

            /**
             * 到达目的地
             */
            @Override
            public void onArriveDest() {

            }
            /**
             * 震动
             */
            @Override
            public void onVibrate() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(WNaviGuideActivity.this, "没有相机权限,请打开后重试", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaviHelper.startCameraAndSetMapView(WNaviGuideActivity.this);
            }
        }
    }

}

