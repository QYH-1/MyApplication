package com.HK.dzbly.utils.auxiliary;

import android.util.Log;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/16
 * 描述：实现数据的归一化
 * 修订历史：
 */
public class Data_normalization {
    DecimalFormat df = new DecimalFormat("0.###");

    public Map<String, double[]> normalization(double[] temp, double[] tempY) {
        Map<String, double[]> map = new HashMap<>();
        double[] p = new double[temp.length];
        double[] pY = new double[tempY.length];
        //求出数组中的最大值
        double db = maxV(temp);
        double dY = maxV(tempY);
        db = Math.max(db, dY);
        Log.d("db", String.valueOf(db));
        for (int i = 0; i < p.length; i++) {
            p[i] = Double.valueOf(df.format(temp[i] / db));
            pY[i] = Double.valueOf(df.format(tempY[i] / db));
        }
        Log.d("p", Arrays.toString(p));
        Log.d("pY", Arrays.toString(pY));

        map.put("pX", p);
        map.put("pY", pY);

        return map;
    }

    public float[] normalization(float[] temp) {

        float[] p = new float[temp.length];
        //求出数组中的最大值
        double db = maxV(temp);
        for (int i = 0; i < p.length; i++) {
            p[i] = Float.parseFloat((df.format(temp[i] / db)));
        }
        return p;
    }

    /**
     * 获取数组中元素绝对值的最大值
     *
     * @param matrixJ matrixJ
     * @return v
     */
    public double maxV(double[] matrixJ) {
        double v = Math.abs(matrixJ[0]);
        for (int i = 0; i < matrixJ.length; i++) {
            if (Math.abs(matrixJ[i]) > v) {
                v = Math.abs(matrixJ[i]);
            }
        }
        return v;
    }

    /**
     * 获取数组中的最大值
     *
     * @param matrixJ matrixJ
     * @return v
     */
    public float maxV(float[] matrixJ) {
        float v = matrixJ[0];
        for (int i = 0; i < matrixJ.length; i++) {
            if (matrixJ[i] > v) {
                v = matrixJ[i];
            }
        }
        return v;
    }
}
