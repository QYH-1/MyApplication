package com.HK.dzbly.utils.wifi;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

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
     *
     * @param msg
     */
    public void laserControl(byte[] msg) {
        try {
            try {
                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress("10.10.100.254", 8899);
                socket.connect(socketAddress, 3000);
                outputStream = socket.getOutputStream();
                outputStream.write(msg);
            } catch (SocketException e) {
                System.out.println("socket连接建立失败");
            }
            Log.d("向服务器端发送消息", String.valueOf(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
