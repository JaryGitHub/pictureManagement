package com.example.picturemanagement.app.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import com.example.picturemanagement.app.domain.photo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class chooseCategory {
    public static photo pd;//获得图片
    public chooseCategory(photo pd)
    {
        this.pd = pd;
    }
    public String path;
    public boolean creatBitmap()
    {
        Bitmap bm = pd.getBm();//获得bitmap
        File sd= Environment.getExternalStorageDirectory();
        String fname = DateFormat.format("yyyyMMddhhmmss", new Date()).toString()+".jpg";//照片名字
        path = sd.getPath() + "/PhotoManager/"  + fname;//照片的路径
        File file = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));//把照片保存在这个文件夹下
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPath()
    {
        return path;
    }
}
