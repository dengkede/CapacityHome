package com.example.administrator.capacityhome.smarthome.video.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.capacityhome.R;

import java.io.IOException;

public class ToneListActity extends Activity {

    public static final String TONE_FILE_NAME = "tone_shpreference";

    private ImageButton back_btn;
    private ListView toneListView;
    private ImageButton switch_btn;
    private String[]    toneList;
    private MediaPlayer mMediaPlayer;

    SharedPreferences toneSharedPreferences;
    private boolean isOpen;
    private int     pos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.titlebar);
        TextView tv = (TextView) this.findViewById(R.id.bar_text);
        tv.setText(getText(R.string.txt_push_tone));
        ImageView imTpnsreg = (ImageView) findViewById(R.id.imTpnsreg);
        imTpnsreg.setVisibility(View.GONE);
        back_btn = (ImageButton) findViewById(R.id.bar_left_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quit();
            }
        });
        setContentView(R.layout.activity_tone_list);
        toneList = getResources().getStringArray(R.array.tone_arr);
        toneSharedPreferences = getSharedPreferences(TONE_FILE_NAME, Activity.MODE_PRIVATE);

        pos = toneSharedPreferences.getInt("pos", 2);
        isOpen = toneSharedPreferences.getBoolean("isOpen", true);
        switch_btn = (ImageButton) findViewById(R.id.ibtn_next);
        if (isOpen) {
            switch_btn.setBackgroundResource(R.mipmap.switch_on);
        }else {
            switch_btn.setBackgroundResource(R.mipmap.switch_off);
        }
        switch_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isOpen = !isOpen;
                switch_btn.setBackgroundResource(isOpen ? R.mipmap.switch_on : R.mipmap.switch_off);
                toneSharedPreferences.edit().putBoolean("isOpen", isOpen).commit();
            }
        });

        toneListView = (ListView) findViewById(R.id.tone_list);
        toneListView.setAdapter(toneAdapter);
        toneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Uri toneUri = null;
                pos = arg2;
                toneAdapter.notifyDataSetChanged();
                if (toneList[pos].equalsIgnoreCase("alarm01")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm01);
                } else if (toneList[pos].equalsIgnoreCase("alarm02")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm02);
                } else if (toneList[pos].equalsIgnoreCase("dingdong01")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dingdong01);
                } else if (toneList[pos].equalsIgnoreCase("dingdong02")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dingdong02);
                } else if (toneList[pos].equalsIgnoreCase("dingling")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dingling);
                } else if (toneList[pos].equalsIgnoreCase("doorring01")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.doorring01);
                } else if (toneList[pos].equalsIgnoreCase("ring01")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring01);
                } else if (toneList[pos].equalsIgnoreCase("ring02")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring02);
                } else if (toneList[pos].equalsIgnoreCase("ring03")) {
                    toneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring03);
                }
                playRingtone(ToneListActity.this, toneUri);
                toneSharedPreferences.edit().putInt("pos", pos).commit();
            }
        });
    }


    public void playRingtone(Context context, Uri toneUri) {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = MediaPlayer.create(context, toneUri);
        mMediaPlayer.setLooping(false);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    public void quit() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static boolean getPushToneSwitch(Context context){
        SharedPreferences preferences = context.getSharedPreferences(TONE_FILE_NAME, Activity.MODE_PRIVATE);
        return preferences.getBoolean("isOpen", true);
    }

    public static int getPushTonePos(Context context){
        SharedPreferences preferences = context.getSharedPreferences(TONE_FILE_NAME, Activity.MODE_PRIVATE);
        return preferences.getInt("pos", 2);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return super.onKeyDown(keyCode, event);
    }

    public BaseAdapter toneAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return toneList.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(ToneListActity.this).inflate(R.layout.list_item_setting, parent, false);
                holder = new ViewHolder();
                holder.name_tv = (TextView) convertView.findViewById(R.id.list_tv);
                holder.ok_iv = (ImageButton) convertView.findViewById(R.id.list_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name_tv.setText(toneList[position]);
            holder.name_tv.setTextColor(getResources().getColor(R.color.txt_color));

            if (pos == position) {
                holder.ok_iv.setVisibility(View.VISIBLE);
            } else {
                holder.ok_iv.setVisibility(View.INVISIBLE);
            }


            return convertView;
        }

    };

    public static class ViewHolder {
        public TextView    name_tv;
        public ImageButton ok_iv;
    }
}

