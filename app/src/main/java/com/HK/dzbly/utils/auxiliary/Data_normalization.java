package com.HK.dzbly.utils.auxiliary;

import java.text.DecimalFormat;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/16
 * 描述：实现数据的归一化
 * 修订历史：
 */
public class Data_normalization {
    DecimalFormat df = new DecimalFormat("0.##");

    public double[] normalization(double[] temp) {

        double[] p = new double[temp.length];
        //求出数组中的最大值
        double db = maxV(temp);
        for (int i = 0; i < p.length; i++) {
            p[i] = Double.valueOf(df.format(temp[i] / db));
        }
        return p;
    }

    /**
     * 获取数组中的最大值
     *
     * @param matrixJ matrixJ
     * @return v
     */
    public double maxV(double[] matrixJ) {
        double v = matrixJ[0];
        for (int i = 0; i < matrixJ.length; i++) {
            if (matrixJ[i] > v) {
                v = matrixJ[i];
            }
        }
        return v;
    }
}
