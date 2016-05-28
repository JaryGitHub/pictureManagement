package com.example.picturemanagement.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.gridviewAdapter;
import com.example.picturemanagement.app.database.database;

import java.util.ArrayList;

public class showImage_Activity extends Activity {

    private ArrayList<String> array;
    private ArrayList<String>mark = new ArrayList<String>();//存放已经选中的图片位置
    private GridView grid;
    private TextView text;
    private Button button,button_1;
    public boolean isgood = false;//判断是不是处于长按模式
    private gridviewAdapter my;//适配器
    public database da;
    private String s;//保存分类名字
    private LinearLayout line,line_1;//获得xml中的控件LinearLayout
    private SQLiteDatabase db;
    private double proportion ;//计算手机纵向分辨率和纵向分辨率为1280的比例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_main);
        s = getIntent().getExtras().getString("name");//获得了选定分类的名字
        proportion = getIntent().getExtras().getDouble("proportion");//获得当前手机纵向分辨率和纵向分辨率为1280的比例
        array = new ArrayList<String>();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))//判断SD卡是否存在
        {
            da = new database(this);//启动数据库
            db = da.getWritableDatabase();//启动事务加速读取
            db.beginTransaction();
            Cursor cursor = da.getReadableDatabase().rawQuery("select P_url,P_class from P_classification",null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String s1 = cursor.getString(cursor.getColumnIndex("P_url"));//获得图片的URL
                String s2 = cursor.getString(cursor.getColumnIndex("P_class"));//获得图片的分类
                if(s2.contains(s))//如果分类中有选定的分类那么加入容器
                {
                    array.add(s1);
                }
                cursor.moveToNext();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            cursor.close();//关闭数据库和事务
            da.close();
            my = new gridviewAdapter(this,array);
            line = (LinearLayout)findViewById(R.id.line);
            LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
            para.height = (int)(1040 * proportion);
            line.setLayoutParams(para);
            line_1 = (LinearLayout)findViewById(R.id.linner);
            para = (LinearLayout.LayoutParams) line_1.getLayoutParams();
            para.height = (int)(60 * proportion);
            line_1.setLayoutParams(para);
            grid = (GridView)findViewById(R.id.gridview);
            text = (TextView)findViewById(R.id.textview);
            button = (Button)findViewById(R.id.button_search);
            button_1 = (Button)findViewById(R.id.button_delete);
            button.setOnClickListener(new onclicklistener(this));
            button_1.setOnClickListener(new onclicklistener_1(this));
            button_1.setVisibility(View.GONE);//按钮设置为不出现
            grid.setAdapter(my);
            grid.setOnItemClickListener(new OnItemClick(this));
            grid.setOnItemLongClickListener(new onItemLongClickListener(this));
            text.setText(String.valueOf(array.size()) + "张照片");
        }
        else
        {
            Toast.makeText(showImage_Activity.this, "没有SD卡请插入", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *对于按条件搜索按钮设置监听点击就会显示分类
     */
    public class onclicklistener implements View.OnClickListener
    {
        private Context context;
        public onclicklistener(Context con)
        {
            this.context = con;
        }
        @Override
        public void onClick(View v) {
            String []strings = {"时间"};
            new AlertDialog.Builder(context).setItems(strings,new AlertDialog.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setClass(showImage_Activity.this, calendar_Activity.class);//启动calendar   Activity
                    intent.putStringArrayListExtra("array", array);
                    context.startActivity(intent);
                }
            }).show();
        }
    }

    /**
     * 设置删除按钮的监听，可以从当前分类中删除图片
     */
    public class onclicklistener_1 implements View.OnClickListener {
        private Context context;

        public onclicklistener_1(Context con) {
            this.context = con;
        }

        @Override
        public void onClick(View v) {
            da = new database(context);//初始化数据库
            Cursor cursor = null;
            if(mark.size() == 0)//判断有没有选择相应的照片
            {
                Toast.makeText(context,"对不起没有选择图片",Toast.LENGTH_LONG).show();
                LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
                para.height = (int)(1040 * proportion);//当删除按钮使用好了就隐藏原来的控件恢复成1040乘以比例的大小（1040是纵向分辨率为1040的手机）
                line.setLayoutParams(para);
                button_1.setVisibility(View.GONE);
            }
            else
            {
                for (int i = 0; i < mark.size(); i++) {//如果存在选中的图片，那么就把这个分类从图片分类字符串中删除
                    String mak = mark.get(i);
                    cursor = da.getReadableDatabase().rawQuery("select P_class from P_classification where P_url = ?", new String[]{mak});
                    cursor.moveToFirst();
                    String get = cursor.getString(cursor.getColumnIndex("P_class"));
                    get = getNewString(get, s);//获得新的已经除去分类的字符串
                    ContentValues c = new ContentValues();
                    c.put("P_class", get);
                    da.getWritableDatabase().update("P_classification", c, "P_url = ?", new String[]{mak});
                }
                array = new ArrayList<String>();
                db = da.getWritableDatabase();//启动事务加速读取
                db.beginTransaction();
                cursor = da.getReadableDatabase().rawQuery("select P_url,P_class from P_classification",null);//重新读取数据库中当前分类的图片
                cursor.moveToFirst();
                while(!cursor.isAfterLast())
                {
                    String s1 = cursor.getString(cursor.getColumnIndex("P_url"));
                    String s2 = cursor.getString(cursor.getColumnIndex("P_class"));
                    if(s2.contains(s))
                    {
                        array.add(s1);
                    }
                    cursor.moveToNext();
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                cursor.close();
                da.close();
                my = new gridviewAdapter(context,array);//刷新适配器
                grid.setAdapter(my);
                text.setText(String.valueOf(array.size()) + "张照片");
                button_1.setVisibility(View.GONE);
                LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
                para.height = (int)(1040 * proportion);//当删除按钮使用好了就隐藏原来的控件恢复成1040乘以比例的大小（1040是纵向分辨率为1040的手机）
                line.setLayoutParams(para);
            }
        }
    }

    /**
     * 为gridview可以选择里面的每一个小的图片设置监听
     */
    public class OnItemClick implements AdapterView.OnItemClickListener
    {

        private Context context;
        public OnItemClick(Context con)
        {
            this.context = con;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(!isgood)//非长按模式
            {
                Intent intent = new Intent();
                intent.setClass(showImage_Activity.this, gallery_Activity.class);
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("array",array);
                showImage_Activity.this.startActivity(intent);
            }
            else//长按模式
            {
                if(!my.judge(position+""))//如果是第一次选择的图片
                {
                    mark.add(array.get(position));//放入选择的容器中
                    my.addpostion(position+"");//把图片的编号保存在适配器中的容器中
                    ImageView image = (ImageView) view.getTag();
                    Matrix max = new Matrix();
                    Bitmap bm =((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();//获得grifview中图片的缓存
                    my.addmap(position+"",bm);//把bitmap缓存保存起来
                    max.setRotate(-45, bm.getWidth() / 2, bm.getHeight() / 2);//把选择的图片向左移动45度
                    Bitmap map = bm.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),max,true);
                    image.setImageBitmap(map);
                }
                else//如果是已经被选择的图片在被点击一次 那么就是不选择
                {
                    my.remove(position + "");//移除图片的编号
                    mark.remove(array.get(position));
                    Bitmap sup = my.getmap(position+"");//从适配器中获得原来的图片
                    my.removemap(position + "");
                    ImageView image = (ImageView) view.getTag();
                    image.setImageBitmap(sup);//重新设置图片
                }
            }

        }
    }

    /**
     * 进入长按模式 那么就可以删除照片
     */
    public class onItemLongClickListener implements AdapterView.OnItemLongClickListener
    {
        private Context context;
        public onItemLongClickListener(Context con)
        {
            this.context = con;
        }
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
            para.height = (int)(1000 * proportion);//为删除按钮留出一些空间
            line.setLayoutParams(para);
            mark = new ArrayList<String>();//初始化mark容器
            button_1.setVisibility(View.VISIBLE);//删除按钮可见
            isgood = true;
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_image_, menu);
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

    /**
     *把特定的分类从图片的分类字符串中删除
     * @param s
     * @param s1
     * @return
     */
    public String getNewString(String s, String s1)
    {
        int postion = s.indexOf(s1);
        int length = s1.length();
        int Length = s.length();
        String newString = s.substring(0,postion - 1) + s.substring(postion + length, Length);
        return newString;
    }
}
