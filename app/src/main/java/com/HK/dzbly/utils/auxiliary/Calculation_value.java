package com.HK.dzbly.utils.auxiliary;

/**
 * 实现具体的计算方法，得到对应的数据
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
