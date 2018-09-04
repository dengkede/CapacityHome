package utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;

import java.io.File;

/**
 * Created by hj on 2017/2/21 0021.
 * YongCheBang
 */

public class TakePhotoHelper {

    public static final String COMPRESS_HEIGHT = "compress_height";
    public static final String COMPRESS_WIDTH = "compress_width";
    public static final String COMPRESS_SIZE = "compress_size";

    public static final String CROP_HEIGHT = "crop_height";
    public static final String CROP_WIDTH = "crop_width";

    private int mMaxHeight = 400;
    private int mMaxWidth = 400;
    private int mMaxSize = 500;

    private int mCropHeight = 320;
    private int mCropWidth = 320;


    public TakePhotoHelper(Bundle bundle) {
        if (bundle != null) {
            mMaxHeight = bundle.getInt(COMPRESS_HEIGHT, 400);
            mMaxWidth = bundle.getInt(COMPRESS_WIDTH, 400);
            mMaxSize = bundle.getInt(COMPRESS_SIZE, 500);

            mCropHeight = bundle.getInt(CROP_HEIGHT, 320);
            mCropWidth = bundle.getInt(CROP_WIDTH, 320);
        }
    }

    private void configCompress(TakePhoto takePhoto) {
        LubanOptions options = new LubanOptions.Builder()
                .setMaxHeight(mMaxHeight)
                .setMaxWidth(mMaxWidth)
                .setMaxSize(mMaxSize)
                .create();

        CompressConfig compressConfig = CompressConfig.ofLuban(options);
        compressConfig.enableReserveRaw(true);
        CompressConfig config = new CompressConfig.Builder().setMaxSize(500*1024).setMaxPixel(1280).create();
        takePhoto.onEnableCompress(config, false);

    }


    //裁剪
    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(mCropWidth)
            .setOutputY(mCropHeight);
        return builder.create();
    }

    /**
     * 从相册获取照片  单张
     * @param takePhoto
     * @param withCrop  是否剪切尺寸
     */
    public void PickPhotoFromGallery(TakePhoto takePhoto, boolean withCrop) {
        Uri imageUri = getFileUri();
        configCompress(takePhoto);
        if (!withCrop) {
            takePhoto.onPickFromGallery();
        } else {
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        }
    }

    /**
     * 拍照获取照片
     * @param takePhoto
     * @param withCrop  是否裁剪
     */
    public void PickPhotoFromCapture(TakePhoto takePhoto, boolean withCrop) {
        Uri imageUri = getFileUri();
        configCompress(takePhoto);
        if (!withCrop) {
            takePhoto.onPickFromCapture(imageUri);
        } else {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        }

    }

    /**
     * 从相册获取多张照片
     * @param takePhoto
     * @param limit     选取张数上限
     * @param withCrop  是否裁剪
     */
    public void PickMultiplePhoto(TakePhoto takePhoto, int limit, boolean withCrop) {
        Uri imageUri = getFileUri();
        configCompress(takePhoto);
        if (withCrop) {
            takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
        } else {
            takePhoto.onPickMultiple(limit);
        }
    }

    private Uri getFileUri() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return Uri.fromFile(file);
    }

}
