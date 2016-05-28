package com.example.picturemanagement.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.adapter.gridviewAdapter;
import com.example.picturemanagement.app.database.database;
import com.example.picturemanagement.app.domain.pictureData;
import com.example.picturemanagement.app.util.sortComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class management_Activity extends Activity {


    private static final int UPDATE = 0;
    private ArrayList<String> array;//保存数据库中所有的照片的URL
    private GridView grid;
    private TextView textview;
    private gridviewAdapter gridviewadapter;//grid的适配器
    private Button button_1,button_2,button_3,button_4,button_5,button_6;
    private ArrayList<String> mark = new ArrayList<String>() ;//保存长按以后选择的照片
    private database da;//数据库对象
    private Cursor cursor;//数据库的游标
    public boolean isgood = false;//判断是不是处于长按模式下
    private boolean firstload;//判断是不是第一次启动
    private LinearLayout line,lineforTextview;//横向的菜单保存区域
    private HorizontalScrollView horizontalScrollView;
    private double proportion = 0;//计算手机纵向分辨率和纵向分辨率为1280的比例
    private SQLiteDatabase db;//为了获得事务
    private ArrayList<pictureData> Temporary;
    private ArrayList<String>getPosition;
    private int H;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management__main);
        SharedPreferences sharepreferences = this.getSharedPreferences("相册管理", MODE_PRIVATE);//初始化 SharedPreferences对象
        SharedPreferences.Editor editor = sharepreferences.edit();//初始化SharedPreferences.Editor对象
        firstload = sharepreferences.getBoolean("first", true);//设置firstload的值
        if(firstload)//如果是true则为第一次初始化
        {
            Toast.makeText(this,"第一次初始化需要一些时间",Toast.LENGTH_LONG).show();
        }
        sharepreferences.edit().putBoolean("first", false).commit();//将first的值设置为false说明已经不是第一次使用了
        new task(this).execute("String");
    }

    private class task extends AsyncTask<String,Integer,Integer>
    {
        private Context context;
        public ProgressDialog pd;
        public task(Context c)
        {
            context = c;
        }

        protected void onPreExecute()
        {
            pd = ProgressDialog.show(context,"initialization","In progress",true);
        }

        protected void onPostExecute(Integer result)
        {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))//判断SD卡是不是存在
            {
                lineforTextview = (LinearLayout)findViewById(R.id.linerlayoutfortextview);
                line = (LinearLayout)findViewById(R.id.ll);
                horizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontalscrollview);
                grid = (GridView)findViewById(R.id.gridview);
                textview = (TextView)findViewById(R.id.textview);
                button_1 = (Button)findViewById((R.id.button_new_classification));
                button_2 = (Button)findViewById(R.id.button_search);
                button_3 = (Button)findViewById(R.id.button_delete);
                button_4 = (Button)findViewById(R.id.localimage);
                button_5 = (Button)findViewById(R.id.shanchu);
                button_6 = (Button)findViewById(R.id.fenlei);
                textview.setText(String.valueOf(array.size()) + "张照片");
                gridviewadapter = new gridviewAdapter(management_Activity.this,array);
                grid.setAdapter(gridviewadapter);
                grid.setOnItemClickListener(new OnItemClick(management_Activity.this));
                grid.setOnItemLongClickListener(new onitemlongclicklistener(management_Activity.this));
                button_1.setOnClickListener(new onclicklistenerforbutton_1(management_Activity.this));
                button_2.setOnClickListener(new onclicklistenerforbutton_2(management_Activity.this));
                button_3.setOnClickListener(new onclicklistenerforbutton_3(management_Activity.this));
                button_4.setOnClickListener(new onclicklistenerforbutton_4(management_Activity.this));
                button_5.setOnClickListener(new onclicklistenerforbutton_5(management_Activity.this));
                button_6.setOnClickListener(new onclicklistenerforbutton_6(management_Activity.this));
            }
            else
            {
                Toast.makeText(management_Activity.this, "没有SD卡请插入", Toast.LENGTH_LONG).show();
            }
            pd.cancel();
        }

        @Override
        protected Integer doInBackground(String... params) {
            initdata();
            return 1;
        }
    }

    /*
    初始化array容器同时初始化比例
     */
    public void initdata()
    {
        Temporary = new ArrayList<pictureData>();
        da = new database(this);//初始化数据库
        array = new ArrayList<String>();
        cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);//获得系统图片数据库的游标
        cursor.moveToFirst();//将游标移动开始位置
        db = da.getWritableDatabase();
        db.beginTransaction();//设置数据库事务
        while(!cursor.isAfterLast())//当游标不是达到最后
        {
            String s = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));//数据库中图片的URL
            ContentValues contentValues = new ContentValues();//使用ContentValues来保存数据
            contentValues.put("P_url",s);
            contentValues.put("P_class","null");
            da.getWritableDatabase().insert("P_classification",null,contentValues);//把数据插入自定义的数据库中
            cursor.moveToNext();//向下一个数据移动
        }
        cursor = da.getReadableDatabase().rawQuery("select P_url from P_classification",null);//游标设置为自定义数据库中P_classification表上
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String s = cursor.getString(cursor.getColumnIndex("P_url"));
            File file = new File(s);//判断这个URL的文件是不是存在
            if(file.exists())//存在就放入array容器中
            {
                Temporary.add(new pictureData(s));
            }
            else
            {
                da.getWritableDatabase().delete("P_classification","P_url = ?",new String[]{s});//从数据库中删除这个URL
            }
            cursor.moveToNext();
        }
        db.setTransactionSuccessful();//结束事务
        db.endTransaction();
        db.close();
        cursor.close();//关闭游标
        da.close();//关闭数据库
        Display mDisplay = getWindowManager().getDefaultDisplay();
        H = mDisplay.getHeight();//获得当前手机的纵向分辨率
        if(H == 854)
        {
            H = 800;
        }
        proportion = ((double)H / (double)1280);//获得当前手机纵向分辨率和纵向分辨率1280手机的比例
        Collections.sort(Temporary, new sortComparator());
        for(int i = 0;i < Temporary.size();i++)
        {
            array.add(Temporary.get(i).getUrl());
        }
    }
    /**
     * 为新建分类按钮设置监听
     */
    public class onclicklistenerforbutton_1 implements View.OnClickListener
    {
        private Context context;
        public onclicklistenerforbutton_1(Context c)
        {
            this.context = c;
        }

        @Override
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(context);//初始化 LayoutInflater对象，用于获得dialoglayoutfornew.xml中的控件
            final View textEntryView = inflater.inflate(R.layout.dialog_layout_for_new, null);//获得dialoglayoutfornew.xml的整体界面布局
            final EditText edtInput=(EditText)textEntryView.findViewById(R.id.edtInput);//获得edittext的布局
            new AlertDialog.Builder(context).setView(textEntryView).setPositiveButton("确定",new AlertDialog.OnClickListener(){//设置提示框

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String gettext = edtInput.getText().toString();//获得编辑框中的文本
                    da = new database(context);
                    ContentValues values = new ContentValues();
                    values.put("class", gettext.trim());
                    da.getWritableDatabase().insert("classification", null, values);//把新增加的分类写入数据库
                    da.close();
                }
            }).setNegativeButton("取消", null).setTitle("新建分类").show();
        }
    }

    /**
     * 给按条件搜索的按钮设置监听
     */
    public class onclicklistenerforbutton_2 implements View.OnClickListener
    {
        private Context context;
        private ArrayList<String> s;
        private  String []arraystring;
        public onclicklistenerforbutton_2(Context c)
        {
            this.context = c;
        }
        @Override
        public void onClick(View v) {
            String []strings = {"时间","分类"};//设置搜索的选项
            new AlertDialog.Builder(context).setItems(strings, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(which == 0)
                    {
                        Intent intent = new Intent();
                        intent.setClass(management_Activity.this, calendar_Activity.class);
                        intent.putStringArrayListExtra("array", array);//把array数组传给calendar.class
                        startActivity(intent);
                    }
                    else if(which == 1)
                    {
                        da = new database(context);
                        s = new ArrayList<String>();
                        db = da.getWritableDatabase();
                        db.beginTransaction();//启动事务
                        cursor = da.getReadableDatabase().rawQuery("select class from classification",null);//从数据库中读取所有的分类
                        cursor.moveToFirst();
                        while(!cursor.isAfterLast())
                        {
                            String ss = cursor.getString(cursor.getColumnIndex("class"));
                            s.add(ss);
                            cursor.moveToNext();
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        db.close();
                        cursor.close();//关闭游标
                        da.close();//关闭数据库
                        arraystring = changrToString(s);
                        new AlertDialog.Builder(context).setItems(arraystring, new AlertDialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent();
                                intent.putExtra("name",arraystring[which]);
                                intent.putExtra("proportion",proportion);
                                intent.setClass(management_Activity.this,showImage_Activity.class);//启动展示图片的Activity
                                context.startActivity(intent);
                            }
                        }).setTitle("选择列表").show();
                    }
                }
            }).show();
        }
    }

    /**
     *删除分类按钮设置监听
     */
    public class onclicklistenerforbutton_3 implements View.OnClickListener
    {
        private Context context;
        private ArrayList<String> s;
        private  String []arraystring;
        public onclicklistenerforbutton_3(Context c)
        {
            this.context = c;
        }
        @Override
        public void onClick(View v) {
            da = new database(context);
            s = new ArrayList<String>();
            db = da.getWritableDatabase();
            db.beginTransaction();
            cursor = da.getReadableDatabase().rawQuery("select class from classification", null);//获得所有的分类
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String ss = cursor.getString(cursor.getColumnIndex("class"));
                s.add(ss);
                cursor.moveToNext();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            arraystring = changrToString(s);//把容器转化为数组
            new AlertDialog.Builder(context).setItems(arraystring,new AlertDialog.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String string = arraystring[which];//获得选择的分类
                    String where = "class = ?";
                    String []wherevalues = {string};
                    da.getWritableDatabase().delete("classification", where, wherevalues);//删除选中的分类
                    db = da.getWritableDatabase();
                    db.beginTransaction();
                    Cursor c =  da.getReadableDatabase().rawQuery("select P_url,P_class from P_classification", null);//把有当前分类的图片中的这个分类从图片已经有的分类中删除
                    c.moveToFirst();
                    while(!c.isAfterLast())
                    {
                        String getstringP = c.getString(c.getColumnIndex("P_url"));
                        String getstring = c.getString(c.getColumnIndex("P_class"));
                        if(getstring.contains(string))
                        {
                            String newstring = getNewString(getstring,string);
                            ContentValues v = new ContentValues();
                            v.put("P_class",newstring);
                            da.getWritableDatabase().update("P_classification",v,"P_url=?",new String[]{getstringP});
                        }
                        c.moveToNext();
                    }
                    c.close();
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                }
            }).setTitle("选择需要删除的分类").show();
            cursor.close();
            da.close();
        }
    }

    /**
     * 给本地图片按钮设置监听
     */
    public class onclicklistenerforbutton_4 implements View.OnClickListener
    {
        private Context context;
        public onclicklistenerforbutton_4(Context con)
        {
            this.context = con;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(context,localimage_Activity.class);//跳转到localimage的Activity
            management_Activity.this.startActivity(intent);
        }
    }

    /**
     * 为隐藏的按钮设置监听，只有当长按图片的时候才会出现
     */
    public class onclicklistenerforbutton_5 implements View.OnClickListener
    {
        private  Context context;
        public onclicklistenerforbutton_5(Context con)
        {
            this.context = con;
        }

        @Override
        public void onClick(View v) {
            if(mark.size() == 0)//判断是不是选择了照片
            {
                Toast.makeText(context,"没有选择相应的照片",Toast.LENGTH_LONG).show();
            }
            else
            {
                da = new database(context);
                for(int i = 0;i < mark.size();i++)//把选中的照片从SD卡中删除
                {
                    String mak = mark.get(i);
                    File file = new File(mak);
                    if(file.exists())
                    {
                        file.delete();
                    }
                    da.getWritableDatabase().delete("P_classification","P_url=?",new String[]{mak});
                }
            }
            for(int i = 0;i < getPosition.size();i++)
            {
                array.remove(array.get(Integer.parseInt(getPosition.get(i))));
            }

            da.close();
            button_1.setClickable(true);//设置这些按钮为可以使用
            button_2.setClickable(true);
            button_3.setClickable(true);
            button_4.setClickable(true);
            isgood = false;//非长按选择
            button_5.setVisibility(View.GONE);//删除和分类按钮清除
            button_6.setVisibility(View.GONE);
            gridviewadapter = new gridviewAdapter(context,array);//重新更新适配器
            grid.setAdapter(gridviewadapter);//更新gridview
            textview.setText(String.valueOf(array.size()) + "张照片");
            LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();//把LinearLayout控件大小变回原来
            para.height = (int)(1040 * proportion);//比例的作用是因为分辨率不同导致不同的值效果不同，因为需要事先获得比例
            line.setLayoutParams(para);
        }
    }

    /**
     * 给分类按钮设置监听，只有当长按图片以后才会出现
     */
    public class onclicklistenerforbutton_6 implements View.OnClickListener
    {
        private  Context context;
        private ArrayList<String> s;
        private  String []arraystring;
        public onclicklistenerforbutton_6(Context con)
        {
            this.context = con;
        }
        @Override
        public void onClick(View v) {
            da = new database(context);
            s = new ArrayList<String>();
            db = da.getWritableDatabase();
            db.beginTransaction();
            cursor = da.getReadableDatabase().rawQuery("select class from classification",null);//读取所有的分类并保存
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String ss = cursor.getString(cursor.getColumnIndex("class"));
                s.add(ss);
                cursor.moveToNext();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            cursor.close();
            da.close();
            arraystring = changrToString(s);
            new AlertDialog.Builder(context).setItems(arraystring, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Cursor c = null;
                    if(mark.size() == 0)//如果没有选择图片的话
                    {
                        Toast.makeText(context,"没有选择相应的照片",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        da = new database(context);
                        for(int i = 0; i < mark.size();i++)
                        {
                            String mak = mark.get(i);
                            c = da.getReadableDatabase().rawQuery("select P_class from P_classification where P_url = '"+mak+"'",null);//通过图片的URL来获得图片的分类
                            c.moveToFirst();
                            String getString = c.getString(c.getColumnIndex("P_class"));//获得分类
                            if(getString.equals("null"))//如果分类是空的，那么直接更新分类
                            {
                                String newstring = "@"+arraystring[which];
                                ContentValues con = new ContentValues();
                                con.put("P_class", newstring.trim());
                                da.getWritableDatabase().update("P_classification",con,"P_url=?",new String[]{mak});
                            }
                            else if(!getString.contains(arraystring[which]))//如果在分类中不含有当前选择的分类那么就在分类字符串的末尾加上这个分类
                            {
                                String newString = getString + "@" +arraystring[which];
                                ContentValues con = new ContentValues();
                                con.put("P_class",newString.trim());
                                da.getWritableDatabase().update("P_classification",con,"P_url=?",new String[]{mak});
                            }
                        }
                        c.close();
                        da.close();
                    }
                    button_1.setClickable(true);
                    button_2.setClickable(true);//按钮重新可以使用
                    button_3.setClickable(true);
                    button_4.setClickable(true);
                    isgood = false;//长按模式结束
                    button_5.setVisibility(View.GONE);//删除和分类按钮消失
                    button_6.setVisibility(View.GONE);
                    gridviewadapter = new gridviewAdapter(context,array);
                    grid.setAdapter(gridviewadapter);//刷新适配器
                    LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
                    para.height = (int)(1040 * proportion);
                    line.setLayoutParams(para);
                }
            }).setTitle("选择列表").show();
        }
    }

    /**
     * 如果当长按模式选择了图片但是并未做如何分类直接使用返回按钮时
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isgood && keyCode == KeyEvent.KEYCODE_BACK)//处于长按模式
        {
            LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
            para.height = (int)(1040 * proportion);//恢复原来尺寸
            line.setLayoutParams(para);
            button_1.setClickable(true);
            button_2.setClickable(true);
            button_3.setClickable(true);//按钮可用
            button_4.setClickable(true);
            button_5.setVisibility(View.GONE);
            button_6.setVisibility(View.GONE);//删除和分类按钮消失
            isgood = false;//长按模式结束
            gridviewadapter = new gridviewAdapter(management_Activity.this,array);//刷新适配器
            grid.setAdapter(gridviewadapter);
        }
        else//非长按模式
        {
            System.exit(0);
            return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_management__main, menu);
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
            if(!isgood)//如果不是长按模式那么就进入预览模式
            {
                Intent intent = new Intent();
                intent.setClass(management_Activity.this, gallery_Activity.class);//来到画廊Activity预览
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("array",array);
                management_Activity.this.startActivity(intent);
            }
            else//如果是长按模式
            {
                if(!gridviewadapter.judge(position+""))//如果是第一次选择的图片
                {
                    mark.add(array.get(position));//放入选择的容器中
                    getPosition.add(String.valueOf(position));
                    gridviewadapter.addpostion(position + "");//把图片的编号保存在适配器中的容器中
                    ImageView image = (ImageView) view.getTag();
                    Matrix max = new Matrix();
                    Bitmap bm =((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();//获得grifview中图片的缓存
                    gridviewadapter.addmap(position+"",bm);//把bitmap缓存保存起来
                    max.setRotate(-45, bm.getWidth() / 2, bm.getHeight() / 2);//把选择的图片向左移动45度
                    Bitmap map = bm.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),max,true);//重新设置了bitmap
                    image.setImageBitmap(map);
                }
                else//如果是已经被选择的图片在被点击一次 那么就是不选择
                {
                    getPosition.remove(String.valueOf(position));
                    gridviewadapter.remove(position + "");//移除图片的编号
                    mark.remove(array.get(position));//移除图片的编号
                    Bitmap sup = gridviewadapter.getmap(position+"");//从适配器中获得原来的图片
                    gridviewadapter.removemap(position + "");
                    ImageView image = (ImageView) view.getTag();
                    image.setImageBitmap(sup);//重新设置图片
                }
            }

        }
    }

    /**
     * 设置长按模式
     */
    public class onitemlongclicklistener implements AdapterView.OnItemLongClickListener
    {
        private Context con;
        public onitemlongclicklistener(Context c)
        {
            this.con = c;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) line.getLayoutParams();
            para.height = (int)(880 * proportion);//如果进入长按模式那么删除和分类两个按钮就会出现，提供一定的空间。
            line.setLayoutParams(para);
            mark = new ArrayList<String>();//每次进入长按模式那么就需要对mark容器初始化一次
            getPosition = new ArrayList<String>();
            isgood = true;//进入长按模式
            button_1.setClickable(false);
            button_2.setClickable(false);
            button_3.setClickable(false);//其他按钮则不能被选择
            button_4.setClickable(false);
            button_5.setVisibility(View.VISIBLE);//删除和分类按钮出现
            button_6.setVisibility(View.VISIBLE);
            return true;
        }
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

    /**
     * 把容器里面的内容转换成string数组
     * @param s
     * @return
     */
    public String[] changrToString(ArrayList<String>s)
    {
        String []string = new String[s.size()];
        for(int i = 0; i < s.size();i++)
        {
            string[i] = s.get(i);
        }
        return string;
    }
}
