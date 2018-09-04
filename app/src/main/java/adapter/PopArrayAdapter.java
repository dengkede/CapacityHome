package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class PopArrayAdapter extends ArrayAdapter {

    private int color;

    public PopArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        for(int i = 0; i < parent.getChildCount(); i++){
            LinearLayout view = (LinearLayout) parent.getChildAt(i);
            for(int j = 0;j < view.getChildCount();j++){
                View chview = view.getChildAt(j);
                if (chview instanceof TextView) {
                    ((TextView) chview).setTextColor(getContext().getResources().getColor(color));

                }
            }
        }
        return super.getView(position, convertView, parent);
    }

    public void setColor(int color) {
        this.color = color;
    }
}
