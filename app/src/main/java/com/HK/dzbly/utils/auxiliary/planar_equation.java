package com.HK.dzbly.utils.auxiliary;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 实现空间散点坐标到空间一个平面的坐标转换
 * 首先根据空间散点坐标拟合出一个平面，再将点投影到这个平面中去
 */
public class planar_equation {
    /**
     * 实现空间散点坐标到空间一个平面的坐标转换
     *
     * @param datax
     * @param datay
     * @param dataz
     * @return
     * @throws NumberFormatException
     */
    public List<Map<String, Object>> Get_equation(double[] datax, double[] datay, double[] dataz) throws NumberFormatException {

        DecimalFormat df = new DecimalFormat("0.##");
        String Equation = null;
        // 获取计算值方法对象
        Calculation_value cValue = new Calculation_value();
        // 获取计算公式对象
        CalculationEquations equations = new CalculationEquations();
        //获取系数值
        double A = 0;
        double B = 0;
        double C = -1;
        double D = 0;
        double x = 0;
        double y = 0;
        double z = 0;
        List<Map<String, Object>> listTemp = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        // 储存计算出需要的值
        map.put("x*x", cValue.Calculation_two(datax, datax));
        map.put("x*y", cValue.Calculation_two(datax, datay));
        map.put("x", cValue.Calculation_one(datax));
        map.put("x*z", cValue.Calculation_two(datax, dataz));
        map.put("y*y", cValue.Calculation_two(datay, datay));
        map.put("y", cValue.Calculation_one(datay));
        map.put("y*z", cValue.Calculation_two(datay, dataz));
        map.put("z", cValue.Calculation_one(dataz));
        map.put("n", datax.length);

        // 方程的未知数的个数
        int n = 3;
        // 系数矩阵
        double[][] test = {{(double) map.get("x*x"), (double) map.get("x*y"), (double) map.get("x")},
                {(double) map.get("x*y"), (double) map.get("y*y"), (double) map.get("y")},
                {(double) map.get("x"), (double) map.get("y"), Double.valueOf(map.get("n").toString())}};
        // 方程的解
        double[] value = {(double) map.get("x*z"), (double) map.get("y*z"), (double) map.get("z")};
        try {
            // 转换成增广矩阵并进行初等行变化
            double[][] mathMatrix = equations.mathDeterminantCalculation(equations.transferMatrix(test, value));
            // 找出非零行的个数
            int checkMatrixRow = equations.effectiveMatrix(mathMatrix);
            // 根据未知数的个数和方程组非零行的个数来判断当前方程组的解的情况
            if (n > checkMatrixRow) {
                System.out.println("未知数有" + n + "个，消元法后获取的阶梯方程组有" + checkMatrixRow + "个方程,少于未知数个数，所以该方程有无数组解");
            } else if (n < checkMatrixRow) {
                System.out.println("未知数有" + n + "个，消元法后获取的阶梯方程组有" + checkMatrixRow + "个方程,多于未知数个数，所以该方程有无解");
            } else {
                System.out.println("未知数有" + n + "个，消元法后获取的阶梯方程组有" + checkMatrixRow + "个方程,等于未知数个数，所以该方程有解");
                double[] result = equations.calculationResult(mathMatrix);
                for (int i = 0; i < result.length; i++) {
                    System.out.println("方程组的解为x" + (i + 1) + "=" + df.format(result[i]));
                }
                //将获取的值赋值给变量用于求投影点
                A = Double.valueOf(df.format(result[0]).toString());
                B = Double.valueOf(df.format(result[1]).toString());
                D = Double.valueOf(df.format(result[2]).toString());
                //打印方程式
                if (Double.valueOf(df.format(result[1]).toString()) > 0
                        && Double.valueOf(df.format(result[2]).toString()) > 0) {
                    Equation = df.format(result[0]) + "x+" + df.format(result[1]) + "y-" + "z+" + df.format(result[2])
                            + "=0";
                } else if (Double.valueOf(df.format(result[1]).toString()) > 0
                        && Double.valueOf(df.format(result[2]).toString()) < 0) {
                    Equation = df.format(result[0]) + "x+" + df.format(result[1]) + "y-" + "z" + df.format(result[2])
                            + "=0";
                } else if (Double.valueOf(df.format(result[1]).toString()) < 0
                        && Double.valueOf(df.format(result[2]).toString()) > 0) {
                    Equation = df.format(result[0]) + "x" + df.format(result[1]) + "y-" + "z+" + df.format(result[2])
                            + "=0";
                } else {
                    Equation = df.format(result[0]) + "x" + df.format(result[1]) + "y-" + "z" + df.format(result[2])
                            + "=0";
                }
                System.err.println("三元一次的表达式为：" + Equation);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //计算点在平面投影点的坐标将其存入到list中
        for (int i = 0; i < datax.length; i++) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            double xi = Double.parseDouble(df.format(((B * B + C * C) * datax[i] - A * (B * y + z + D)) / ((A * A + B * B) + C * C)).toString());
            double yi = Double.parseDouble(df.format(((A * A + C * C) * datay[i] - B * (A * x + z + D)) / (A * A + (B * B + C * C))).toString());
            double zi = Double.parseDouble(df.format(((A * A + B * B) * dataz[i] - C * (A * x + B * y + D)) / (A * A + B * B + C * C)).toString());
            //将数据存储在list中
            pMap.put("xp", xi);
            pMap.put("yp", yi);
            pMap.put("zp", zi);
            listTemp.add(pMap);
        }
        Log.i("plan_equation-listTemp", String.valueOf(listTemp));
        //返回得到的投影面上的坐标
        //坐标归一化
        Data_normalization dn = new Data_normalization();
        double[] dx = new double[datax.length];
        double[] dy = new double[datax.length];
        double[] dz = new double[datax.length];
        //得到x,y,z的坐标数组
        for (int i = 0; i < datax.length; i++) {
            dx[i] = Double.parseDouble(listTemp.get(i).get("xp").toString());
        }
        for (int i = 0; i < datay.length; i++) {
            dy[i] = Double.parseDouble(listTemp.get(i).get("yp").toString());
        }
        for (int i = 0; i < dataz.length; i++) {
            dz[i] = Double.parseDouble(listTemp.get(i).get("zp").toString());
        }

        double[] dxTemp = dn.normalization(dx);
        double[] dyTemp = dn.normalization(dy);
        double[] dzTemp = dn.normalization(dz);
        for (int i = 0; i < datax.length; i++) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            //将数据存储在list中
            pMap.put("xp", dxTemp[i]);
            pMap.put("yp", dyTemp[i]);
            pMap.put("zp", dzTemp[i]);
            list.add(pMap);
        }
        Log.i("planar_equation-list", String.valueOf(list));
        return list;
    }

}
