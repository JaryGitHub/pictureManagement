package com.example.picturemanagement.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.picturemanagement.app.R;
import com.example.picturemanagement.app.database.database;
import com.example.picturemanagement.app.util.createNewFile;
import com.example.picturemanagement.app.widget.cameraView;

import java.io.File;

public class camera_Activity extends Activity {

    private database da;//数据库对象
    //菜单
    protected static final int Menu_Flash = Menu.FIRST;//开启闪光灯按钮
    protected static final int Menu_About = Menu.FIRST + 1;//关于按钮
    protected static final int Menu_Exit = Menu.FIRST + 2;//退出按钮
    //程序第一次运行参数
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_RUN = "first";
    private boolean first;//判断是不是第一次运行

    private SurfaceView surfaceview;//启用surfaceview方便绘制照相机获得的图像
    private SurfaceHolder surfaceholder;//为了对surfaceview进行修改
    private ImageButton ibtn_shezhi;//设置按钮 进入设置菜单
    private ImageButton ibtn_xiangce;//进入相册管理
    private Boolean isSGD = false;//闪光灯是不是开启
    private ImageButton ibtn_paizhao;//拍照按钮
    private cameraView cameraview;

    public static int screenWidth;//保存屏幕的宽度
    public static int screenHeight;//保存屏幕的高度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__main);

        //判断SD卡是否插入
        if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) != true) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("SD卡未插入，请检查SD卡插入情况！").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();//结束整个Activity
                        }
                    }).show();
            return;
        }
        File sd=Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/PhotoManager";
        File file = new File(path);
        //判断程序是否是第一次运行
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        first = settings.getBoolean(FIRST_RUN, true);
        if (first) {
            Toast.makeText(this, "The Application is first run",
                    Toast.LENGTH_LONG).show();
            new createNewFile();
            String []strings = {"人物","风景"};
            da = new database(this);
            for(int i = 0 ; i < strings.length;i++)
            {
                ContentValues contentValues = new ContentValues();//使用ContentValues来保存数据
                contentValues.put("class", strings[i]);
                da.getWritableDatabase().insert("classification", null, contentValues);//把数据插入自定义的数据库中
            }
            da.close();
        }
        else if(!file.exists())
        {
            new createNewFile();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //设置窗口一直处于高亮状态
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//取消全屏模式
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//添加全屏模式
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//获得系统级的屏幕服务
        Display display = wm.getDefaultDisplay();
        // 获取屏幕宽高
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();


        surfaceview = (SurfaceView) findViewById(R.id.sfv_photo);
        surfaceholder = surfaceview.getHolder();//获得surfaceholder对象
        cameraview = new cameraView(this);
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//指定Push Buffer
        surfaceholder.addCallback(cameraview);//添加回调对象来回调surfaceholder


        ibtn_shezhi = (ImageButton) findViewById(R.id.ibtn_shezhi);
        ibtn_xiangce = (ImageButton) findViewById(R.id.ibtn_xiangce);
        ibtn_paizhao = (ImageButton) findViewById(R.id.ibtn_paizhao);
        /**
         * 设置监听  按下相册按钮来到相册界面
         */
        ibtn_xiangce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(camera_Activity.this,management_Activity.class);
                startActivity(intent);
            }
        });
        /**
         * 设置监听  按下相册按钮来到选择闪光灯是否打开界面
         */
        ibtn_shezhi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(camera_Activity.this, photoSetup_Activity.class);
                startActivityForResult(intent, 0);
                openOptionsMenu();
            }
        });
        /**
         *设置监听   按下拍照按钮后来到保存照片界面
         */
        ibtn_paizhao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraview.takepic();
            }
        });
    }

    /**
     * 菜单界面
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu_Flash, 0, "开启闪光灯");
        menu.add(0, Menu_About, 1, "关于");
        menu.add(0, Menu_Exit, 2, "退出");

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }
    /**
     * 对于菜单上的按钮设置相应的相应事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == Menu.FIRST) {
            if(item.getTitle().equals("开启闪光灯")) {
                isSGD = true;
                item.setTitle("关闭闪光灯");
            }
            else {
                isSGD = false;
                item.setTitle("开启闪光灯");
            }
            cameraview.setIsSGD(isSGD);//通知cameraview类来开启闪光灯
            cameraview.initCamera();//重新初始化相机
        }
        if(item.getItemId() == Menu.FIRST + 1) {
            new AlertDialog.Builder(this).setTitle("About").setMessage("PhotoManager for V1.0" +"\n" + "                                    By  Alvin")
                    .setPositiveButton("确定", null)
                    .show();
        }
        if(item.getItemId() == Menu.FIRST + 2) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 从其他界面返回当前界面时，闪光灯回到原来的状态
     */
    @Override
    protected void onRestart() {
        cameraview.setIsSGD(isSGD);
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //保存状态信息
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (first) {
            editor.putBoolean(FIRST_RUN, false);
        }
        editor.commit();
    }

    /**
     *获得从PhotoSetup中得到的是否打开闪光灯这个事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data == null)
            return;
        Bundle bundle = data.getExtras();
        isSGD = bundle.getBoolean("isSGD");
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *获得当前的手势动作并且给cameraview中传递这个动作
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cameraview.mScaleDetector.onTouchEvent(event);
        return true;
    }

}
