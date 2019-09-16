package com.HK.dzbly.utils.auxiliary;

import com.HK.dzbly.model.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/16
 * 描述：GiftWrapping算法实现凸包，得到在凸包上点的坐标
 * 修订历史：
 */
public class GiftWrap {
    // 已知的平面上的点集
    Point[] point;
    // 标pointA[i]是否已在凸包中
    boolean[] vis;
    // 点集的凸包,
    Queue<Integer> queue = new LinkedList<Integer>();
    //定义list用于存储凸包上的点
    private List<Map<String, Object>> list = new ArrayList<>();
    private int n;

    public GiftWrap() {

    }

    // 向量ca与ba的叉积
    double cross(Point c, Point a, Point b) {
        return (c.x - a.x) * (a.y - b.y) - (c.y - a.y) * (a.x - b.x);
    }

    // 求距离,主要是为了求极点
    public double distance(Point p1, Point p2) {
        return (Math.hypot((p1.x - p2.x), (p1.y - p2.y)));
    }

    public List<Map<String, Object>> go(double[] x, double[] y, double[] z) {

        //获取点的个数和点的坐标
        n = x.length;
        point = new Point[n + 1];
        vis = new boolean[n + 1];
        for (int i = 1; i <= n; i++) {// 输入从1开始
            point[i] = new Point();
            point[i].x = x[i - 1];
            point[i].y = y[i - 1];
            point[i].z = z[i - 1];
        }
        Arrays.sort(point, 1, point.length - 1);// 注意这个排序从1开始
        // 确定一个肯定在凸包上的点
        vis[1] = true;// 注意这里将point[1]标记为放进凸包,不过并没有真的放入队列
        int in = 1;// 在凸包上的点

        while (true) {
            int not = -1;
            for (int i = 1; i <= n; i++) {
                if (!vis[i]) {// 找一个不在凸包上的点
                    not = i;
                    break;
                }
            }
            if (not == -1)
                break;// 找不到,结束
            for (int i = 1; i <= n; i++) {
                /*
                 * 遍历所有点, 每个点都和现有最外侧的点比较,得到新的最外侧的点 第二个条件是找到极点，不包括共线点
                 */
                if ((cross(point[in], point[i], point[not]) > 0) || (cross(point[in], point[i], point[not]) == 0)
                        && (distance(point[in], point[i]) > distance(point[in], point[not])))
                    not = i;
            }
            if (vis[not])
                break;// 找不到最外侧的点了
            queue.offer(not);// 最外侧的点进凸包
            vis[not] = true;// 标记这点已放进凸包了
            in = not;// in始终表示一个必定在凸包里的点
        }
        in = 1;
        /*
         * 最后将point[1]的下标放进凸包 不会重复放入，因为最开始只是把标记下标1在凸包
         */
        queue.offer(1);
        /*
         * 这里有个问题需要弄明白 算凸多边形周长，需要连续的两个点，下面的是连续的不？ 是的。
         * 编号1插入到队尾，temp先取出的是队头，即是最后一个进入凸多边形的点，显然和编号1是连接的 那会不会重复呢？
         * 当然不会，因为while循环最后一次是编号1和最先插入的点的距离，此时编号1已经没了，队列空了
         */
        while (!queue.isEmpty()) {
            int temp = queue.poll();// 获取并移除队列的头
            for (int i = 0; i < 1; i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("x", point[in].x);
                map.put("y", point[in].y);
                map.put("z", point[in].z);
                list.add(map);
            }
            in = temp;
        }
        return list;
    }
}
