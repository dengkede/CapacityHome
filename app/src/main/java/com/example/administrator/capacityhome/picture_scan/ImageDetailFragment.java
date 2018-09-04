package com.example.administrator.capacityhome.picture_scan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.administrator.capacityhome.MyApplication;
import com.example.administrator.capacityhome.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import http.RequestTag;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zhang on 2018/4/23.
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private PhotoView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private boolean isFile = false;
    public static ImageDetailFragment newInstance(String imageUrl,boolean isFile) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        args.putBoolean("isFile",isFile);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
        isFile = getArguments().getBoolean("isFile");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (PhotoView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float v, float v1) {
                getActivity().finish();
            }

        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isFile){
            Picasso.with(getActivity()).load(new File(mImageUrl)).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(mImageView);
        }else {
            Picasso.with(getActivity()).load(RequestTag.BaseImageUrl + mImageUrl).placeholder(R.mipmap.default_iv).error(R.mipmap.default_iv).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    mAttacher.update();
                }

                @Override
                public void onError() {
                    try {
                        Picasso.with(getActivity()
                        ).load(RequestTag.BaseImageUrl + MyApplication.getConfigrationJson().getJSONObject("other").getString("default_img")).placeholder(R.mipmap.default_iv).error(R.mipmap.img_user).into(mImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                            }
                        });
                        mAttacher.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        }

    }

}