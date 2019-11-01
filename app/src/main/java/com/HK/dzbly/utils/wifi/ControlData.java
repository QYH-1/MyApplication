package com.HK.dzbly.utils.wifi;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/31
 * 描述：
 * 修订历史： 设置控制激光向硬件发送的信息
 */
public class ControlData {
    private String[] data = {"打开左侧点激光", "打开左侧线激光", "打开右侧点激光", "打开右侧线激光", "打开下侧点激光", "打开下侧线激光", "打开上侧点激光", "打开上侧线激光",
            "关闭左侧点激光", "关闭左侧线激光", "关闭右侧点激光", "关闭右侧线激光", "关闭下侧点激光", "关闭下侧线激光", "关闭上侧点激光", "关闭上侧线激光"};
    private byte msg; //保存发送数据

    /**
     * 通过接受用户点击的按钮，确定通过wif发送的数据
     *
     * @param clickText
     * @return
     */
    public byte setData(String clickText) {
        if (clickText.equals(data[0])) {
            msg = (byte) 0x45495704;
        } else if (clickText.equals(data[1])) {
            msg = (byte) 0x45495705;
        } else if (clickText.equals(data[2])) {
            msg = (byte) 0x45495706;
        } else if (clickText.equals(data[3])) {
            msg = (byte) 0x45495707;
        } else if (clickText.equals(data[4])) {
            msg = (byte) 0x45495708;
        } else if (clickText.equals(data[5])) {
            msg = (byte) 0x45495709;
        } else if (clickText.equals(data[6])) {
            msg = (byte) 0x45495710;
        } else if (clickText.equals(data[7])) {
            msg = (byte) 0x45495711;
        } else if (clickText.equals(data[8])) {
            msg = (byte) 0x45495712;
        } else if (clickText.equals(data[9])) {
            msg = (byte) 0x45495713;
        } else if (clickText.equals(data[10])) {
            msg = (byte) 0x45495714;
        } else if (clickText.equals(data[11])) {
            msg = (byte) 0x45495715;
        } else if (clickText.equals(data[12])) {
            msg = (byte) 0x45495716;
        } else if (clickText.equals(data[13])) {
            msg = (byte) 0x45495717;
        } else if (clickText.equals(data[14])) {
            msg = (byte) 0x45495718;
        } else {
            msg = (byte) 0x45495719;
        }
        return msg;
    }
}
