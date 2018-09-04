package utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ImageBase64 {

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String  bitmapToBase64(Bitmap bitmap, String url) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        url = url.substring(url.lastIndexOf(".")+1,url.length());
        return "data:image/"+url+";base64,"+result;
    }


    /**
     * 将图片转换成Base64编码的字符串
     * @param path
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path){
        Log.d("picicpicpicp",path);
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (IOException e){
            e.printStackTrace();
            Log.d("picicpicpicp2ee",e.toString());
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("picicpicpicp2e",e.toString());
                }
            }

        }
      //  Log.d("picicpicpicp2",result);
        return "data:image/"+"png"+";base64,"+result;
    }


    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String  bitmapToBase64_png(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        url = url.substring(url.lastIndexOf(".")+1,url.length());
        return "data:image/"+"png"+";base64,"+result;
    }

}
