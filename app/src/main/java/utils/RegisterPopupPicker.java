package utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.administrator.capacityhome.R;

import java.util.List;
import java.util.TimerTask;

import adapter.PopArrayAdapter;


public class RegisterPopupPicker {

    private Activity activity;

    private PopupWindow popupWindow;
    private java.util.Timer timer;
    private TimerTask task;
    private OnPopupItemSelectedListener listener;
    private InputMethodManager manager;     //键盘管理
    private boolean show = false;

    private String headText;
    private int color;
    private int headColor;

    public RegisterPopupPicker(Activity activity) {
        this.activity = activity;
        this.color = R.color.fontcolor_f6;
        // manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void showHeadText(boolean show, String headText) {
        this.show = show;
        this.headText = headText;
    }

    public void setHeadColor(int color) {
        headColor = color;
    }

    public void setTextColor(int color) {
        this.color = color;
    }

    public void setPopupItemSelectedListener(OnPopupItemSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * item为String的时候的选择项
     *
     * @param locationView
     * @param items
     */
    public void startSelectPopupItem(final View locationView, final List<String> items) {
        hideInputWindow(activity);
        View view = View.inflate(activity, R.layout.popup_picker_layout, null);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        popupWindow.showAtLocation(locationView,
                Gravity.NO_GRAVITY, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.update();
        if (timer == null) {
            timer = new java.util.Timer(true);
         /*
        界面弹出后背景变黑
         */
            if (task != null){
                task.cancel();  //将原任务从队列中移除
            }
            task = new TimerTask() {
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(timer!=null) {
                                timer.cancel();
                            }
                            setBackgroundAlpha(0.6f);
                        }
                    });

                }
            };
        }


        timer.schedule(task, 300);
        final ListView itemList = (ListView) view.findViewById(R.id.item_list);
        View headView = View.inflate(activity, R.layout.common_popup_head_view, null);
        TextView headViewText = (TextView) headView.findViewById(R.id.pop_head_text);
        if (show) {
            headViewText.setText(headText);
            headViewText.setTextColor(headColor);
            itemList.addHeaderView(headView);
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
                timer = null;
                task = null;
            }
        });
        PopArrayAdapter adapter = new PopArrayAdapter(activity, R.layout.popup_list_item, R.id.popup_item, items);
        adapter.setColor(color);
        itemList.setAdapter(adapter);
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                if (itemList.getHeaderViewsCount() > 0) {
                    if (position != 0) {
                        listener.onItemSelected(locationView, position - 1, items);
                    }
                } else {
                    listener.onItemSelected(locationView, position, items);
                }

            }
        });

        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    public interface OnPopupItemSelectedListener {
        public void onItemSelected(View locationView, int position, List<String> list);
    }

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    protected void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void hideInputWindow(Activity context) {
        if (context == null) {
            return;
        }
        final View v = ((Activity) context).getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            if(imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }


}
