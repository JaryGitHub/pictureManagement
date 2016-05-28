package com.example.picturemanagement.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.picturemanagement.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class gridviewAdapter extends BaseAdapter{

    private Context context;
    private String url[];//���ͼƬ��url
    private DisplayImageOptions options;
    private ArrayList<String> arraylist;
    private ArrayList<String>contain = new ArrayList<String>();
    private HashMap<String,Bitmap> mp = new HashMap<String,Bitmap>();
    public gridviewAdapter(Context convert,ArrayList<String>s)
    {
        this.context = convert;
        this.arraylist = s;
        url = new String[arraylist.size()];
        for(int i = 0;i < arraylist.size();i++)
        {
            url[i] = ImageDownloader.Scheme.FILE.wrap(arraylist.get(i));
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
        return url.length;
    }


    @Override
    public Object getItem(int position) {
        return url[position];
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addpostion(String num)
    {
        contain.add(num);
    }


    public void remove(String num)
    {
        contain.remove(num);
    }


    public boolean judge(String s)
    {
        if(contain.contains(s))
            return true;
        else
            return  false;
    }


    public void addmap(String s,Bitmap bm)
    {
        mp.put(s, bm);
    }


    public void removemap(String s)
    {
        mp.remove(s);
    }


    public Bitmap getmap(String s)
    {
        return mp.get(s);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = convertView;
        ImageView imageView;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_image, null);
            imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(imageView);
        }
        else{
            imageView = (ImageView) view.getTag();
        }
        ImageLoader.getInstance().displayImage(url[position], imageView, options);


        if(contain.contains(""+position))
        {
            imageView = (ImageView) view.getTag();
            Bitmap bm =((BitmapDrawable) ((ImageView) imageView).getDrawable()).getBitmap();
            Matrix max = new Matrix();
            max.setRotate(-45,bm.getWidth()/2,bm.getHeight()/2);//��б45��
            Bitmap map = bm.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),max,true);
            imageView.setImageBitmap(map);
        }
        return view;
    }

}
