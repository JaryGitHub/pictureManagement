package com.example.picturemanagement.app.util;

import com.example.picturemanagement.app.domain.pictureData;

import java.util.Comparator;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class sortComparator implements Comparator{
    @Override
    public int compare(Object lhs, Object rhs) {

        pictureData p = (pictureData)lhs;
        pictureData p_1 = (pictureData)rhs;
        if((p_1.getTime() - p.getTime()) > 0)
        {
            return 1;
        }
        else if((p_1.getTime() - p.getTime()) < 0)
        {
            return -1;
        }
        else
            return 0;
    }
}
