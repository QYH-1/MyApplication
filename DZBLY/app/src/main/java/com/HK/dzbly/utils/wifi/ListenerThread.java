package com.HK.dzbly.utils.wifi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.HK.dzbly.R;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/5$
 * 描述：wifi监听线程
 * 修订历史：
 */
public class ListenerThread extends Thread {
    private ServerSocket serverSocket = null;
    private Handler handler;
    private int port;
    private Socket socket;

    public ListenerThread(int port, Handler handler){
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        try {
            serverSocket=new ServerSocket(port);//监听本机的12345端口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true){
            try {
                Log.w("AAA","阻塞");
                //阻塞，等待设备连接
                socket = serverSocket.accept();
                Message message = Message.obtain();
                message.what = R.string.DEVICE_CONNECTING;
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.w("AAA","error:"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
