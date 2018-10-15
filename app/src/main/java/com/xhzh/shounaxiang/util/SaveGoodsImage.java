package com.xhzh.shounaxiang.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by wjsay on 2018/10/8
 * Describe:
 */
public class SaveGoodsImage {
    String img_name;
    Bitmap bitmap;
    public SaveGoodsImage(Bitmap bitmap, String img_name) {
        this.img_name = img_name;
        this.bitmap = bitmap;
    }
    public boolean save() {
        try {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/xhzh/");
            dir.mkdir();
            File dir02 = new File(dir + "/goods/");//两侧都要加/
            dir02.mkdir();
            File file = new File(dir02, img_name + ".JPG");
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
