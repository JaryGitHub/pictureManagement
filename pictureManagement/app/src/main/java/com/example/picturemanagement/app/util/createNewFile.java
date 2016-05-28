package com.example.picturemanagement.app.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class createNewFile {
    public createNewFile() {
        File sd= Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/PhotoManager";
        File file = new File(path);
        if(!file.exists()) {
            file.mkdir();
        }
    }
}
