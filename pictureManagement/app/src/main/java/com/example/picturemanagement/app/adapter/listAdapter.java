package com.example.picturemanagement.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.widget.getForListview;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class listAdapter extends BaseAdapter{

    private ArrayList<getForListview> GET;
    private Context context;
    private DisplayImageOptions options;
    private String []url;
    private mark m;

    public listAdapter(Context c,ArrayList<getForListview>get)
    {
        this.context = c;
        this.GET = get;
        url = new String[GET.size()];
        for(int i = 0 ; i< GET.size();i++)
        {
            url[i] = ImageDownloader.Scheme.FILE.wrap(GET.get(i).getBitmapURl());
        }
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(null)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return GET.size();
    }

    @Override
    public Object getItem(int position) {
        return GET.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        m = null;
        getForListview getForListview = GET.get(position);
        if(convertView == null)
        {
            m = new mark();
            convertView = LayoutInflater.from(context).inflate(R.layout.iconlist,null);
            m.imageView = (ImageView)convertView.findViewById(R.id.image);
            m.textView = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(m);
        }
        else
        {
            m = (mark)convertView.getTag();
        }
        m.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        m.textView.setText(getForListview.getsign() +"("+getNumber(getForListview.getUrl())+")");
        ImageLoader.getInstance().displayImage(url[position], m.imageView, options);
        return convertView;
    }
    public class mark
    {
        ImageView imageView;
        TextView textView;
    }
    public int getNumber(String path)
    {
        int ans = 0;
        File file = new File(path);
        File []files = file.listFiles();
        for(int i = 0 ; i < files.length;i++)
        {
            if(files[i].isFile() & files[i].getName().endsWith(".jpg")||files[i].getName().endsWith(".png")||files[i].getName().endsWith(".JPG")||files[i].getName().endsWith(".bmp")||files[i].getName().endsWith(".PNG")||files[i].getName().endsWith(".BMP")||files[i].getName().endsWith(".JPEG")||files[i].getName().endsWith(".jpeg"))
            {
                ans++;
            }
        }
        return ans;
    }
}
