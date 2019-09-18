package com.HK.dzbly.utils.wifi;

import android.util.Log;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/21$
 * 描述：处理wifi传递的数据，传递过来的数据六位一分，三位整数，二位小数
 * 修订历史：
 */
public class Concerto {

    //处理传递进来长度为6的字符串
    public String Dataconversion(String data) {
        String integrate = null; //整数
        String decimal = null;// 小数
        String dana = null;// 转换之后得到的数据

        //接收第1位为符号位
        String str1 = data.substring(0, 1);
        //接收的2、3、4位为整数部分
        String str2 = data.substring(1, 4);
        if (str2.substring(0, 1).equals("0")) {
            if (str2.substring(1, 2).equals("0")) {
                integrate = str2.substring(2);
            } else {
                integrate = str2.substring(1);
            }
        } else {
            integrate = str2;
        }
        //后两位为小数部分
        String str3 = data.substring(4);
        decimal = str3;
        Log.d("小数部分", decimal);

        if (str1.equals("0")) {
            dana = integrate + "." + decimal;
        } else {
            dana = "-" + integrate + "." + decimal;
        }
        Log.d("结果", dana);
        return dana;
    }
}
