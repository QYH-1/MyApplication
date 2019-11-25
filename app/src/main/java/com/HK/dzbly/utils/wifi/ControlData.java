package com.HK.dzbly.utils.wifi;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/31
 * 描述：
 * 修订历史： 设置控制激光向硬件发送的信息
 */
public class ControlData {
    private String[] data = {"打开左点", "关闭左点", "打开左线", "关闭左线", "打开下点", "关闭下点", "打开下线", "关闭下线",
            "打开上点", "关闭上点", "打开上线", "关闭上线", "打开右点", "关闭右点", "打开右线", "关闭右线"};
    private byte[] msg; //保存发送数据

    /**
     * 通过接受用户点击的按钮，确定通过wif发送的数据
     *
     * @param clickText
     * @return
     */
    public byte[] setData(String clickText) {
        if (clickText.equals(data[0])) {
//            msg = (byte) 0x45495704;
            msg = new byte[]{69, 73, 87, 4};
        } else if (clickText.equals(data[1])) {
            //msg = (byte) 0x45495705;
            msg = new byte[]{69, 73, 87, 5};
        } else if (clickText.equals(data[2])) {
            //msg = (byte) 0x45495706;
            msg = new byte[]{69, 73, 87, 6};
        } else if (clickText.equals(data[3])) {
            //msg = (byte) 0x45495707;
            msg = new byte[]{69, 73, 87, 7};
        } else if (clickText.equals(data[4])) {
            // msg = (byte) 0x45495708;
            msg = new byte[]{69, 73, 87, 8};
        } else if (clickText.equals(data[5])) {
            //msg = (byte) 0x45495709;
            msg = new byte[]{69, 73, 87, 9};
        } else if (clickText.equals(data[6])) {
            //msg = (byte) 0x45495710;
            msg = new byte[]{69, 73, 87, 16};
        } else if (clickText.equals(data[7])) {
            //msg = (byte) 0x45495711;
            msg = new byte[]{69, 73, 87, 17};
        } else if (clickText.equals(data[8])) {
            // msg = (byte) 0x45495712;
            msg = new byte[]{69, 73, 87, 18};
        } else if (clickText.equals(data[9])) {
            // msg = (byte) 0x45495713;
            msg = new byte[]{69, 73, 87, 19};
        } else if (clickText.equals(data[10])) {
            // msg = (byte) 0x45495714;
            msg = new byte[]{69, 73, 87, 20};
        } else if (clickText.equals(data[11])) {
            //msg = (byte) 0x45495715;
            msg = new byte[]{69, 73, 87, 21};
        } else if (clickText.equals(data[12])) {
            // msg = (byte) 0x45495716;
            msg = new byte[]{69, 73, 87, 22};
        } else if (clickText.equals(data[13])) {
            // msg = (byte) 0x45495717;
            msg = new byte[]{69, 73, 87, 23};
        } else if (clickText.equals(data[14])) {
            // msg = (byte) 0x45495718;
            msg = new byte[]{69, 73, 87, 24};
        } else {
            //msg = (byte) 0x45495719;
            msg = new byte[]{69, 73, 87, 25};
        }

        Log.d("---msg", String.valueOf(msg));
        return msg;
    }
}
