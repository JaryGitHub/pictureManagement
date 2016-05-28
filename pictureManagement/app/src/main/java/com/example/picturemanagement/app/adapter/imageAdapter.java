package com.example.picturemanagement.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.widget.galleryView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import java.util.ArrayList;

/**
 * Created by mycomputer on 2016/5/28.
 */
/**
 * 名称: ImageAdapter.java
 * 描述: 为画廊控件添加数据的适配器
 */
public class imageAdapter extends BaseAdapter {

    private int ownposition;//获得当前选中项的位置
    private String []url;//存放图片的url
    private DisplayImageOptions options;//添加框架配置
    private ArrayList<String> array;
    private Context mContext;

    // 声明 ImageAdapter
    public imageAdapter(Context c,ArrayList<String>s) {
        mContext = c;
        this.array = s;
        url = new String[array.size()];
        for(int i = 0;i < array.size();i++)
        {
            url[i] = ImageDownloader.Scheme.FILE.wrap(array.get(i));
        }
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(null)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }


    /**
     * 获得当前项的位置
     * @return
     */
    public int getOwnposition() {
        return ownposition;
    }


    // 获取图片的个数
    public int getCount() {
        return url.length;
    }

    // 获取图片在库中的位置
    public Object getItem(int position) {
        ownposition = position;
        return url[position];
    }

    // 获取图片ID
    public long getItemId(int position) {
        ownposition = position;
        return position;
    }

    /**
     * 绘制当前需要显示的图片
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = convertView;
        ownposition = position;//获取位置
        ImageView imageview;
        if(convertView == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.gallery_image,null);//获得galleryimage.xml
            imageview = (ImageView)view.findViewById(R.id.galleryimage);//获得galleryimage.xml中的imageview
            imageview.setBackgroundColor(0xFF000000);//设置背景色为黑色
            imageview.setLayoutParams(new galleryView.LayoutParams(//设置Imageview的大小
                    GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
            imageview.setAdjustViewBounds(true);//保持长宽比
            imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);//图片将填充中心
            view.setTag(imageview);
        }
        else
        {
            imageview = (ImageView) view.getTag();
        }
        ImageLoader.getInstance().displayImage(url[position],imageview,options);
        return imageview;
    }
}
