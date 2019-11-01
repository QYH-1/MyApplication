package com.HK.dzbly.utils.wifi;

import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/31
 * 描述：
 * 修订历史：向硬件发送信息
 */
public class Laser_control {
    private Socket socket;
    private OutputStream outputStream;  //数据输出流

    /**
     * 向硬件发送信息
     * @param msg
     */
    public void laserControl(byte msg){
        try {
            socket = new Socket("10.10.100.254", 8899);
            outputStream = socket.getOutputStream();
            outputStream.write(msg);
            Log.d("向服务器端发送消息", String.valueOf(msg));
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
