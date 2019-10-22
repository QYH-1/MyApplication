package com.HK.dzbly.utils.wifi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/21
 * 描述：接受wifi发送过来的数据，更新ui
 * 修订历史：
 */
public class ReceiveMsg {
    private StringBuilder stringBuilder = new StringBuilder();
    private String datas = null;
    private Handler handler;    //用来更新UI等(发送信息给主线程)
    public void receiveMsg(DataInputStream inputStream,Handler handler) {
        while (true) {
            try {
                //接受wifi的数据
                int data = inputStream.read();
                Log.d("data", String.valueOf(data));
                //将十六进制的数转换为二进制
                String n = Integer.toHexString(data);
                Log.d("n", String.valueOf(n));
                //拼接字符串
                if (n.equals("0")) {
                    stringBuilder.append(n);
                    stringBuilder.append(0);
                } else if (n.equals("1") || n.equals("2") || n.equals("3") || n.equals("4") || n.equals("5") || n.equals("6") || n.equals("7") || n.equals("8") || n.equals("9")) {
                    stringBuilder.append(0);
                    stringBuilder.append(n);
                } else if (n.substring(0, 1).equals("-")) {
                    n = n.substring(1, 3);
                    stringBuilder.append(n);
                } else {
                    stringBuilder.append(n);
                }
                Log.d("stringBuilder", String.valueOf(stringBuilder));
                datas = String.valueOf(stringBuilder);
                Log.d("datas", String.valueOf(datas));
                //当接受的wifi的数据为30位的整数倍时，通过handler向界面传递数据
                while (datas.length() % 30 == 0 && datas.length() != 0) {
                    Message msg = Message.obtain();
                    msg.what = 0; //当what的标志为0时代表是接受的wifi的数据
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", datas);
                    msg.setData(bundle);
                    //msg.what = 0; //当what的标志为0时代表是接受的wifi的数据
                    handler.sendMessage(msg);
                    Log.w("AAA-11", "c:" + datas);
                    //当数据传递后将接受变量置为空，方便下次接受
                    datas = null;
                    stringBuilder = null;
                    break;
                }
            } catch (NullPointerException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
