package model;

import android.graphics.Bitmap;

/**
 * Created by zhang on 2018/5/11.
 */

public class VideoModel {
    private Bitmap bitmap;
    private String pic;

    public VideoModel(Bitmap bitmap, String pic) {
        this.bitmap = bitmap;
        this.pic = pic;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
