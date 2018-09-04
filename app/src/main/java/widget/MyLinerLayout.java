package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by 毛麒添 on 2017/2/24 0024.
 * 当自定的侧滑菜单打开的时候，右侧的主界面菜单不应该能滑动，
 * 自定义一个LinearLayout拦截并消费该触摸事件
 */

public class MyLinerLayout extends LinearLayout {

    private MySlideMenu mySlideMenu;

    public MyLinerLayout(Context context) {
        super(context);
    }

    public MyLinerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlideMenu(MySlideMenu mySlideMenu){
        this.mySlideMenu=mySlideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mySlideMenu!=null&& mySlideMenu.getDragState()== MySlideMenu.DragState.STATE_OPEN){
            //如果该侧滑面板是打开，则拦截消费触摸事件
           return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mySlideMenu!=null&& mySlideMenu.getDragState()== MySlideMenu.DragState.STATE_OPEN){
            if(event.getAction()==MotionEvent.ACTION_UP){//在侧滑面板打开的状态时候点一下主界面应该关闭侧滑面板
                mySlideMenu.close();
            }
            //如果该侧滑面板是打开，则拦截消费触摸事件
            return true;
        }
        return super.onTouchEvent(event);
    }
}
