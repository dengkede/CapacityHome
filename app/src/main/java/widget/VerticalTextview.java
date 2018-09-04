package widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VerticalTextview extends TextSwitcher implements ViewFactory {
    private static final int FLAG_START_AUTO_SCROLL = 0;
    private static final int FLAG_STOP_AUTO_SCROLL = 1;
    private float mTextSize;
    private int mPadding;
    private int textColor;
    private VerticalTextview.OnItemClickListener itemClickListener;
    private Context mContext;
    private int currentId;
    private ArrayList<JSONObject> textList;
    private Handler handler;

    public void setText(float textSize, int padding, int textColor) {
        this.mTextSize = textSize;
        this.mPadding = padding;
        this.textColor = textColor;
    }

    public VerticalTextview(Context context) {
        this(context, (AttributeSet)null);
        this.mContext = context;
    }

    public VerticalTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextSize = 16.0F;
        this.mPadding = 5;
        this.textColor = -16777216;
        this.currentId = -1;
        this.mContext = context;
        this.textList = new ArrayList();
    }

    public void setAnimTime(long animDuration) {
        this.setFactory(this);
        Animation in = new TranslateAnimation(0.0F, 0.0F, (float)animDuration, 0.0F);
        in.setDuration(animDuration);
        in.setInterpolator(new AccelerateInterpolator());
        Animation out = new TranslateAnimation(0.0F, 0.0F, 0.0F, (float)(-animDuration));
        out.setDuration(animDuration);
        out.setInterpolator(new AccelerateInterpolator());
        this.setInAnimation(in);
        this.setOutAnimation(out);
    }

    public void setTextStillTime(final long time) {
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 0:
                        if (VerticalTextview.this.textList.size() > 0) {
                            VerticalTextview.this.currentId++;
                            try {
                                VerticalTextview.this.setText((CharSequence)VerticalTextview.this.textList.get(VerticalTextview.this.currentId % VerticalTextview.this.textList.size()).getString("title"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        VerticalTextview.this.handler.sendEmptyMessageDelayed(0, time);
                        break;
                    case 1:
                        VerticalTextview.this.handler.removeMessages(0);
                }

            }
        };
    }

    public void setTextList(ArrayList<JSONObject> titles) {
        this.textList.clear();
        this.textList.addAll(titles);
        this.currentId = -1;
    }

    public void startAutoScroll() {
        this.handler.sendEmptyMessage(0);
    }

    public void stopAutoScroll() {
        this.handler.sendEmptyMessage(1);
    }

    public View makeView() {
        TextView t = new TextView(this.mContext);
        t.setGravity(19);
        t.setMaxLines(1);
        t.setPadding(this.mPadding, this.mPadding, this.mPadding, this.mPadding);
        t.setTextColor(ContextCompat.getColor(mContext,this.textColor));
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.mTextSize);
        t.setClickable(true);
        t.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VerticalTextview.this.itemClickListener != null && VerticalTextview.this.textList.size() > 0 && VerticalTextview.this.currentId != -1) {
                    VerticalTextview.this.itemClickListener.onItemClick(VerticalTextview.this.currentId % VerticalTextview.this.textList.size());
                }

            }
        });
        return t;
    }

    public void setOnItemClickListener(VerticalTextview.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int var1);
    }
}
