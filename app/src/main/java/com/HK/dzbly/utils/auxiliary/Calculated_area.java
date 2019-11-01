package com.HK.dzbly.utils.auxiliary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/31
 * 描述：
 * 修订历史： 求断面测量时，点围成的面积
 */
public class Calculated_area {
    private double result;
    public double area(List<Map<String, Object>> dataList) {
        Log.i("Calculated_area", String.valueOf(dataList));
        if (dataList.size() == 3) {
//            Log.i("dataList.get(1)",String.valueOf(dataList.get(1).get("x")));
            result = Math.abs(((Double.parseDouble(String.valueOf(dataList.get(1).get("xp"))) - Double.parseDouble(String.valueOf(dataList.get(0).get("xp")))) * (Double.parseDouble(String.valueOf(dataList.get(2).get("yp"))) - Double.parseDouble(String.valueOf(dataList.get(0).get("yp"))))
                    - (Double.parseDouble(String.valueOf(dataList.get(1).get("yp"))) - Double.parseDouble(String.valueOf(dataList.get(0).get("yp")))) * (Double.parseDouble(String.valueOf(dataList.get(2).get("xp"))) - Double.parseDouble(String.valueOf(dataList.get(0).get("xp")))))) / 2;
            return result;
        }
        dataList = Data_conversion(dataList);
        return area(dataList);
    }
    private List<Map<String, Object>> Data_conversion(List<Map<String, Object>> data){
        if(data.size()>3){
            data.remove(1);
        }
        return data;
    }
}
