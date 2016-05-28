package com.example.picturemanagement.app.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.listAdapter;
import com.example.picturemanagement.app.widget.getForListview;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class localimage_Activity extends AppCompatActivity {

    private ListView list;//list容器用于存放文件夹的名字
    private HashSet<String> set;//用来存放图片的父文件夹，又重复的可能性所以使用set
    private ArrayList<getForListview> arraylist;//存放文件夹的名字
    ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();//为了在list显示图片和名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localimage_main);
        list = (ListView)findViewById(R.id.LISTVIEW);
        set = new HashSet<String>();
        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);//获得系统图片的URL
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String s = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            File file = new File(s);
            set.add(file.getParent());//获得所有的父文件夹名字
            cursor.moveToNext();
        }
        arraylist = new ArrayList<getForListview>();
        Iterator i = set.iterator();//获得SET的迭代器
        while(i.hasNext())//遍历set容器
        {
            String ss = String.valueOf(i.next());
            if(getBitmapUrl(ss) != null)
            {
                arraylist.add(new getForListview(getBitmapUrl(ss),getlast(ss),ss));
            }
        }
        list.setAdapter(new listAdapter(this,arraylist));
        list.setOnItemClickListener(new onitemclicklistener(this));
    }

    /**
     *设置listview的监听当点击以后会显示这个文件夹中的图片
     */
    public class onitemclicklistener implements AdapterView.OnItemClickListener
    {
        private Context context;
        public onitemclicklistener(Context con)
        {
            this.context = con;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String string = arraylist.get(position).getUrl();
            Intent intent = new Intent();
            intent.setClass(localimage_Activity.this,showImage_Activity.class);
            intent.putExtra("address",string);
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_localimage_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getBitmapUrl(String path)
    {
        String PATH = null;
        File file = new File(path);
        File []files = file.listFiles();
        if(file != null && files != null)
        {
            for(int i = 0 ; i < files.length;i++)
            {
                if(files[i].isFile() & files[i].getName().endsWith(".jpg")||files[i].getName().endsWith(".png")||files[i].getName().endsWith(".JPG")||files[i].getName().endsWith(".bmp")||files[i].getName().endsWith(".PNG")||files[i].getName().endsWith(".BMP")||files[i].getName().endsWith(".JPEG")||files[i].getName().endsWith(".jpeg"))
                {
                    PATH = files[i].getAbsolutePath();
                    break;
                }
            }
        }
        return PATH;
    }
    /**
     * 获得图片的父文件夹的名字
     * @param s
     * @return
     */
    public String getlast(String s)
    {
        int ans = 0,bns = 0;
        char []m = s.toCharArray();
        char [] n = new char[s.length()];
        char []last = new char[s.length()];
        for(int i = s.length() - 1;i>= 0;i--)
        {
            if(m[i] == '/')
            {
                break;
            }
            else
            {
                n[ans++] = m[i];
            }
        }
        for(int i = n.length - 1;i >= 0;i--)
        {
            last[bns++] = n[i];
        }
        String s1 = String.valueOf(last).trim();
        return s1;
    }
}
