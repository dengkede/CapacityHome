package com.example.administrator.capacityhome.picture_scan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import http.RequestTag;
import uk.co.senab.photoview.PhotoView;
import utils.DisplayUtil;

public class Pager_PicActivity extends BaseActivity implements View.OnClickListener {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private ViewPager viewPager;//声明ViewPager
    private int pagerPosition;
    private ArrayList<String> list_pic;
    private TextView tv_pager;
    private RelativeLayout relayout_back;//返回
    private boolean isFile = false;
    private TextView tv_delete;
    private PopupWindow popWindow;
    private RelativeLayout layout_parent;
    private TextView tv_cancel;
    private TextView tv_sure;
    private int length = 0;//记录图片组初始长度，用于标识返回时候是否传值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_pager__pic);
        layout_parent = (RelativeLayout) findViewById(R.id.layout_parent);
        relayout_back = (RelativeLayout) findViewById(R.id.relayout_imgBack);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        list_pic = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        if(list_pic!=null){
            length = list_pic.size();
        }
        isFile = getIntent().getBooleanExtra("isFile", false);
        setVies();//初始化控件
        if (!isFile) {
            tv_delete.setVisibility(View.GONE);
        }
        viewPager.setAdapter(new MyAdapter());
        //给viewPager设置监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                int childCount = viewPager.getChildCount();//viewPager得到页面的数量

                // 遍历当前所有加载过的PhotoView，恢复所有图片的默认状态
                tv_pager.setText(arg0 + 1 + "/" + list_pic.size());
                pagerPosition = arg0;
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewPager.getChildAt(i);

                    try {
                        if (childAt != null && childAt instanceof PhotoView) {
//                            PhotoView photoView = (PhotoView) childAt;//得到viewPager里面的页面
//                            photoView.setAdjustViewBounds(true);
//                            PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);//把得到的photoView放到这个负责变形的类当中
//                            //mAttacher.getDisplayMatrix().reset();//得到这个页面的显示状态，然后重置为默认状态
//                            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        viewPager.setCurrentItem(pagerPosition);
    }


    /**
     * 初始化控件
     */
    private void setVies() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_pager = (TextView) findViewById(R.id.tv_pager);
        tv_pager.setText(pagerPosition + 1 + "/" + list_pic.size());
        relayout_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relayout_imgBack:
                if(length == list_pic.size()) {
                    finish_rightToLeft();
                }else{
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("list_pic",list_pic);
                    setResult(2,intent);
                    finish();
                }
                break;
            case R.id.tv_delete:
                //删除
                showPopwindow();
                break;
        }
    }

    /**
     * 自定义pagerAdapter
     */
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list_pic.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //photoView.setImageResource(list_pic.get(position));
            if (isFile) {
                Picasso.with(Pager_PicActivity.this).load(new File(list_pic.get(position))).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(photoView);
            } else {
                Picasso.with(Pager_PicActivity.this).load(RequestTag.BaseImageUrl + list_pic.get(position)).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        try {
                            Picasso.with(Pager_PicActivity.this
                            ).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(photoView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片显示为充满全屏
            photoView.setBackgroundColor(Color.BLACK);
            photoView.setAdjustViewBounds(true);
            return photoView;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

//            if(list_pic ==null || list_pic.size()<=0) return ;
//            if(position==list_pic.size()) position--;
//            container.removeView(mImageViews[position]);
            container.removeView((View) object);
        }


        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == (View)object;
        }
    }

    private void showPopwindow() {
        if(popWindow == null) {
            int width = getResources().getDisplayMetrics().widthPixels / 10 * 8;
            int height = getResources().getDisplayMetrics().widthPixels / 3;
            View popView = LayoutInflater.from(this).inflate(R.layout.delete_img_pop,null);
            popWindow = new PopupWindow(popView, width, height);
            tv_cancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tv_sure = (TextView) popView.findViewById(R.id.tv_sure);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);// 设置同意在外点击消失
            ColorDrawable dw = new ColorDrawable(0x30000000);
            popWindow.setBackgroundDrawable(dw);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tv_cancel:
                            popWindow.dismiss();
                            break;
                        case R.id.tv_sure:
                            popWindow.dismiss();
                            list_pic.remove(viewPager.getCurrentItem());
                            viewPager.getAdapter().notifyDataSetChanged();
//                            if(pagerPosition!=0){
//                                tv_pager.setText(pagerPosition + 1 + "/" + list_pic.size());
//                            }
                            if(list_pic.size()>=1) {
                                tv_pager.setText(pagerPosition + 1 + "/" + list_pic.size());
                            }else{
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra("list_pic",list_pic);
                                setResult(2,intent);
                                finish();
                            }
                            break;
                    }
                }
            };
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    DisplayUtil.backgroundAlpha(1.0f, Pager_PicActivity.this);
                }
            });
            tv_sure.setOnClickListener(onClickListener);
            tv_cancel.setOnClickListener(onClickListener);
        }
        DisplayUtil.backgroundAlpha(0.3f, this);
        popWindow.showAtLocation(layout_parent, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    /**
     * 在此处返回false,所以会继续传播该事件.

     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(length == list_pic.size()) {
                finish_rightToLeft();
            }else{
                Intent intent = new Intent();
                intent.putStringArrayListExtra("list_pic",list_pic);
                setResult(2,intent);
                finish();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
