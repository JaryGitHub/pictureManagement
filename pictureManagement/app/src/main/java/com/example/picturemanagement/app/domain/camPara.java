package com.example.picturemanagement.app.domain;

/**
 * Created by mycomputer on 2016/5/28.
 */

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 名称: MyCamPara.java
 * 描述: 获得相机最合适的预览分辨率和摄像头最合适的分辨率
 */
public class camPara {

    private CameraSizeComparator sizeComparator = new CameraSizeComparator();//排序使用
    private static camPara myCamPara = null;

    /**
     * 用来返回 MyCamPara对象
     * @return
     */
    public static camPara getInstance(){
        if(myCamPara == null){
            myCamPara = new camPara();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }

    /**
     *用来获得相机预览时最合适的长宽比例
     * @param list
     * @param th
     * @return
     */
    public Camera.Size getPreviewSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);//进行排序
        Camera.Size size = null;
        for(int i = 0; i < list.size(); i++) {
            size = list.get(i);
            if((size.width > th) && equalRate(size, 1.3f)){
                break;
            }
        }
        return size;
    }

    /**
     * 获得摄像头最合适的长宽比例
     * @param list
     * @param th
     * @return
     */
    public Camera.Size getPictureSize(List<Camera.Size> list, int th){
        Collections.sort(list, sizeComparator);
        Camera.Size size = null;
        for(int i = 0; i < list.size(); i++) {
            size = list.get(i);
            if((size.width > th) && equalRate(size, 1.3f)){
                break;
            }
        }
        return size;
    }

    /**
     * 设置合适的范围
     * @param s
     * @param rate
     * @return
     */

    public boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);//获得长宽比
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 排序类
     */
    public  class CameraSizeComparator implements Comparator<Camera.Size> {
        //按升序排列
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }
}
