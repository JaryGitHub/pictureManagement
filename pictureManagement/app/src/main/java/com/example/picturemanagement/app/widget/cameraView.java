package com.example.picturemanagement.app.widget;

/**
 * Created by mycomputer on 2016/5/28.
 */
/**
 * 名称: CameraView.java
 * 描述: 启动相机功能
 */
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.example.picturemanagement.app.activity.camera_Activity;
import com.example.picturemanagement.app.database.database;
import com.example.picturemanagement.app.domain.camPara;
import com.example.picturemanagement.app.domain.photo;
import com.example.picturemanagement.app.util.chooseCategory;

import java.io.IOException;
import java.util.List;

public class cameraView implements SurfaceHolder.Callback {

    private Cursor cursor;//数据库的游标
    private database da;//数据库对象
    private SQLiteDatabase db;//为了获得事务
    private Camera camera = null;//获得照相机对象
    private Camera.Parameters parameters;//相机的设置
    private Boolean isSGD = false;//闪光灯开始设置为不开
    private camera_Activity camera_mainactivity;
    public static byte[] picdata;//存放照片的数组


    private static final int ZOOM_OUT = 0;//整个照相机的视角变大
    private static final int ZOOM_IN = 1;//整个照相机的视角变小
    private static final int ZOOM_DELTA = 1;//焦距变化量

    private int mMaxZoom;//最大焦距
    private int mScaleFactor = 1;//手势的判断
    public static ScaleGestureDetector mScaleDetector;//手势的检测

    public cameraView(camera_Activity camera_mainactivity) {
        this.camera_mainactivity = camera_mainactivity;
        mScaleDetector = new ScaleGestureDetector(this.camera_mainactivity, new ScaleListener());//监听手势
    }

    /**
     *设置闪光灯的状态
     */
    public void setIsSGD(Boolean isSGD) {
        this.isSGD = isSGD;
    }

    /**
     * 初始化surface组件和相机
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(); //打开相机服务
        try {
            camera.setPreviewDisplay(holder);  //设置预览
            initCamera(); //相机初始化

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }


    /**
     *停止相机的工作并且释放资源
     * @param holder
     */

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * 初始化相机
     */

    public void initCamera() {
        parameters = camera.getParameters(); //得到相机参数
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();      //获取相机预览支持尺寸
        Camera.Size size = camPara.getInstance().getPreviewSize(previewSizes, camera_mainactivity.screenHeight);
        parameters.setPreviewSize(size.width, size.height); //设置预览尺寸

        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();      //获取相机拍照支持尺寸
        Camera.Size size2 = camPara.getInstance().getPictureSize(pictureSizes, 800);
        parameters.setPictureSize(size2.width, size2.height); //设置照片尺寸

        parameters.setPictureFormat(PixelFormat.JPEG);   //设置照片格式
        if(isSGD) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);     //开启闪光灯
        }
        else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);  //关闭
        }

        parameters.set("orientation", "portrait");
        camera.setDisplayOrientation(90);  //预览旋转90度（不然你预览时是横着的）
        parameters.setRotation(90);
        camera.setParameters(parameters);//设置参数
        camera.startPreview();//开始预览
        camera.autoFocus(null);
    }

    /**
     * 相机拍照
     */
    public void takepic() {
        camera.takePicture(null, null, pictureCallback);//通过回调pictureCallback来进行拍照
    }

    /**
     *回调对象pictureCallback通过这个来获得照片
     */
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            final String []string = getClassification();
            camera.stopPreview();//停止相机工作
            picdata = new byte[data.length + 1];
            System.arraycopy(data, 0, picdata, 0, data.length);//把照片的数据保存到picdata数组中
            new AlertDialog.Builder(camera_mainactivity).setTitle("请选择分类").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initCamera();
                }
            }).setItems(string, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    for(int i = 0 ; i< string.length;i++)
                    {
                        if(which == i)
                        {
                            photo pd = new photo();
                            Bitmap bm = BitmapFactory.decodeByteArray(picdata, 0, picdata.length);
                            pd.setBm(bm);//保存图片到图片数据类
                            chooseCategory chooseCategory = new chooseCategory(pd);
                            if(chooseCategory.creatBitmap())
                            {
                                Toast.makeText(camera_mainactivity, "保存成功", Toast.LENGTH_SHORT).show();
                                ContentValues contentValues = new ContentValues();//使用ContentValues来保存数据
                                contentValues.put("P_url",chooseCategory.getPath());
                                contentValues.put("P_class", string[0]);
                                da.getWritableDatabase().insert("P_classification",null,contentValues);//把数据插入自定义的数据库中
                                initCamera();
                            }
                            else
                            {
                                Toast.makeText(camera_mainactivity,"保存失败",Toast.LENGTH_SHORT).show();
                                initCamera();
                            }
                        }
                    }
                }
            }).show();
            da.close();

        }
    };

    public String []getClassification()
    {
        int ans = 0;
        da = new database(camera_mainactivity);
        db = da.getWritableDatabase();
        db.beginTransaction();//设置数据库事务
        cursor = da.getReadableDatabase().rawQuery("select class from classification",null);
        cursor.moveToFirst();
        String []s = new String[cursor.getCount()];
        while(!cursor.isAfterLast())
        {
            s[ans++] = cursor.getString(cursor.getColumnIndex("class"));
            cursor.moveToNext();
        }
        db.setTransactionSuccessful();//结束事务
        db.endTransaction();
        db.close();
        cursor.close();
        return s;
    }

    /**
     * 设置相机焦距
     * @param params
     */
    private void handleZoom(Camera.Parameters params) {
        int zoom = params.getZoom();//获得当前焦距
        mMaxZoom = params.getMaxZoom();//获得最大焦距
        if (mScaleFactor == ZOOM_IN) {//判断手势，手势是变大焦距
            if (zoom < mMaxZoom) zoom += ZOOM_DELTA;
        } else if (mScaleFactor == ZOOM_OUT) {//判断手势，手势是变小焦距
            if (zoom > 0) zoom -= ZOOM_DELTA;
        }
        if(mMaxZoom == 0)
            return;
        if(params.isZoomSupported()) {//判断相机是不是支持变焦
            if(params.isSmoothZoomSupported()) {//相机是否支持平滑变焦
                camera.startSmoothZoom(zoom);
            }
            else {
                params.setZoom(zoom);
                camera.setParameters(params);
            }
        }
    }

    /**
     * 监听手势
     */
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = (int) detector.getScaleFactor();
            handleZoom(camera.getParameters());
            return true;
        }
    }
}
