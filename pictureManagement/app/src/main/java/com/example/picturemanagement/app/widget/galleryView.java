package com.example.picturemanagement.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.widget.Toast;
import com.example.picturemanagement.app.adapter.imageAdapter;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class galleryView extends Gallery{

    boolean is_first = false;//判断是不是第一张图片
    boolean is_last = false;//判断是不是最后一张图片

    public galleryView(Context context) {
        super(context);
    }

    public galleryView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);

    }

    /**
     * 判断是往右移动还是往左移动
     * @param e1
     * @param e2
     * @return
     */
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    /**
     * 设置滑动的时候，判断是不是到第一页了或者是不是在最后一页了
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX,
                           float distanceY) {
        imageAdapter ia = (imageAdapter) this.getAdapter();//获得适配器对象
        int p = ia.getOwnposition();//通过适配器获得当前选择项的位置
        int count = ia.getCount();//获得所有图片的个数
        int kEvent;//获得是向左还是向右滑动
        if (isScrollingLeft(e1, e2)) {//向左滑动
            if (p == 0 && is_first) {//已经滑到第一页了
                Toast.makeText(this.getContext(), "已经到了第一页", Toast.LENGTH_SHORT).show();
            } else if (p == 0) {
                is_first = true;
            } else {
                is_last = false;
            }

            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;//键盘事件获得向左移动
        } else {//向右移动
            if (p == count - 1 && is_last) {//滑到最后一页了
                Toast.makeText(this.getContext(), "已经到了最后一页", Toast.LENGTH_SHORT).show();
            } else if (p == count - 1) {
                is_last = true;
            } else {
                is_first = false;
            }

            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;//键盘事件获得向右移动
        }
        onKeyDown(kEvent, null);//提交键盘事件，只需滑动一小段距离就能滑动图片，不需要滑动整个图片必须全部滑动
        return true;
    }
}

