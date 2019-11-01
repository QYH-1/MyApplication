package com.HK.dzbly.model;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/16
 * 描述： 坐标点
 * 修订历史：
 */
public class Point implements Comparable {
    public double x;
    public double y;
    public double z;

    // 按x升序排列,x相同按y升序
    @Override
    public int compareTo(Object o) {
        Point b = (Point) o;
        if (this.x > b.x)
            return 1;
        else if (this.x == b.x) {
            if (this.y > b.y)
                return 1;
            else if (this.y == b.y)
                return 0;
            else
                return -1;
        } else
            return -1;
    }
}
