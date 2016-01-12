package com.whisper.sticker;

import android.graphics.PointF;
import android.util.Log;

/**
 * 作者：yz on 2015/12/18 10:35
 * 邮箱：974453813@qq.com
 */
public class MathUtil {
    /***********  判断1个点是否在矩形内部  *************/
    // 将举行拆分成两个三角形 ，则当点在任意一个三角形内部就在矩形内部 否则相反
    // 如何判断一个矩形是否在一个三角形内部呢 我们只需要判断当三角形的任意两个顶点所在直线L
    // 如果需要判断的点 与 最后一个顶点在直线L的同一侧即可证明点在三角形内部
    /***********************************************/

    /**
     * 判断一个点是否在一个矩形内 (包括边缘)
     * @param a 左上角
     * @param b 右上角
     * @param c 右下角
     * @param d 左下角
     * @param p 待判断的点
     * @return
     */
    public static boolean isPointInRectangle(PointF a,PointF b, PointF c, PointF d,PointF p) {
        long t1 = System.currentTimeMillis();
        boolean r1 = isPointInTriangle(a, b, d, p);
        boolean r2 = isPointInTriangle(b,d,c,p);
        long t2 = System.currentTimeMillis();
        Log.i("-- isPointInSameSide",(t2 - t1) / 1000.0 + " s");
        return (r1 || r2);
    }

    /**
     * 判断一个点是否在一个三角形内部 (包括边缘)
     * @param a
     * @param b
     * @param c
     * @param p
     * @return
     */
    public static boolean isPointInTriangle(PointF a,PointF b, PointF c,PointF p) {
        boolean lineAB = isPointInSameSide(a, b, c, p);
        boolean lineBC = isPointInSameSide(b, c, a, p);
        boolean lineAC = isPointInSameSide(a, c, b, p);
        return lineAB && lineBC && lineAC;
    }

    /**
     * 判断c、p两点是否在ab所在直线的同一边
     * @param a
     * @param b
     * @param c
     * @param p
     * @return
     */
    public static boolean isPointInSameSide(PointF a, PointF b, PointF c, PointF p) {
        // 直线一般式方程 Ax + By + C = 0;  A = y2-y1; B = x1-x2; C = x2*y1 - x1*y2;
        // 当Ax + By + C 的值符号相同 则在同一边
        // ab直线  判断c p 是否在该直线的同一边
        float A = b.y - a.y;
        float B = a.x - b.x;
        float C = b.x * a.y - a.x * b.y;

        float resultC = A * c.x + B * c.y + C;
        float resultP = A * p.x + B * p.y + C;

        Log.i("-- isPointInSameSide","result =" + resultC + "," + resultP);
        return ((resultC >= 0 && resultP >= 0)) || (resultC < 0 && resultP < 0);
    }

    /**
     * 计算两点的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double caculateDistance(float x1, float y1, float x2 ,float y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算x轴到点(x,y)的角度 正-顺时针  负数-逆时针
     * @param x
     * @param y
     * @param originX  原点坐标 x
     * @param originY  原点坐标 y
     * @return
     */
    public static double caculateAngle(float x, float y, float originX, float originY) {
        //提示 一般原点为(0,0)  如果原点改为(x,y) 则原来的点换到新坐标系的点坐标为(x1-x,y1-y)
        float newX = x - originX;
        float newY = y - originY;

        double r = Math.atan2(newY, newX); //弧度
        return Math.toDegrees(r);
    }
}
