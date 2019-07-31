package com.dylan.library.utils;

import android.graphics.PointF;

/**
 * Author: Dylan
 * Date: 2019/7/26
 * Desc:
 */

public class MathUtils {

    /**
     *  直线方程   y=ax+b   由起点和终点两个点确定a与b，
     *  代入直线上任意一点，前提是知道该点的x坐标或y坐标
     *
     * @param startPointF  起点坐标
     * @param endPointF    终点坐标
     * @param innerPoint   线上任意一点坐标
     * @return
     */
    public static PointF getInnerPointX(PointF startPointF, PointF endPointF, PointF innerPoint){
        float deltaX=endPointF.x-startPointF.x;
        float deltaY=endPointF.y-startPointF.y;
        innerPoint.x=(innerPoint.y-startPointF.y+(deltaY/deltaX)*startPointF.x)/(deltaY/deltaX);
        return innerPoint;
    }

    public static PointF getInnerPointY(PointF startPointF, PointF endPointF, PointF innerPoint){
        float deltaX=endPointF.x-startPointF.x;
        float deltaY=endPointF.y-startPointF.y;
        innerPoint.y=(deltaY/deltaX)*innerPoint.x+startPointF.y-(deltaY/deltaX)*startPointF.x;
        return innerPoint;
    }

}
