package com.example.administrator.capacityhome.picture_scan;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhang on 2018/4/23.
 */

public class HackyViewPager extends ViewPager {


    private static final String TAG = "HackyViewPager";

    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {

            return false;
        }catch(ArrayIndexOutOfBoundsException e ){

            return false;
        }
    }

}
