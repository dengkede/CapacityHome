package com.example.administrator.capacityhome.picture_scan;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.capacityhome.BaseActivity;
import com.example.administrator.capacityhome.R;

import widget.MyImageView;

public class ImageBrowseActivity extends BaseActivity {
private MyImageView myImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        myImage = new MyImageView(this);
        myImage.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.default_iv));
        myImage.onGlobalLayout();
    }
}
