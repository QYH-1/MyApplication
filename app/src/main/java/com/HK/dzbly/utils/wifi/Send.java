package com.HK.dzbly.utils.wifi;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/21
 * 描述：向硬件传递数据
 * 修订历史：
 */
public class Send {
    public void sendData(OutputStream outputStream,byte msg) throws IOException {
        Log.i("msg", String.valueOf(msg));
        outputStream.write(msg);
        Log.d("向服务器端发送消息", String.valueOf(msg));
        outputStream.flush();
    }
}
