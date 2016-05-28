package com.example.picturemanagement.app;

/**
 * Created by mycomputer on 2016/5/28.
 */

/**
 * 名称: MainActivity.java
 * 描述: 配置imageloader框架
 */
import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(400,800)
                .threadPoolSize(5)
                .build();
        ImageLoader.getInstance().init(configuration);//使用推荐自定义文件
    }
}
