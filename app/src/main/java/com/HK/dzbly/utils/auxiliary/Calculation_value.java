package com.HK.dzbly.utils.auxiliary;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/16
 * 描述：实现具体的计算方法，得到对应的数据
 * 修订历史：
 */
public class Calculation_value implements Calculation {
    @Override
    public double Calculation_one(double[] x) {
        double dataSum = 0;
        for (int i = 0; i < x.length; i++) {
            dataSum = dataSum + x[i];
        }

        return dataSum;
    }

    @Override
    public double Calculation_two(double[] x, double[] y) {
        double dataSum = 0;
        for (int i = 0; i < x.length; i++) {
            dataSum = dataSum + x[i];
        }
        return dataSum;
    }
}
