package com.HK.dzbly.utils.wifi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/12$
 * 描述：连接线程
 * 修订历史：
 */
public class ConnectThread extends Thread {

    private  Socket socket = null;
    private Handler handler = null;
    private InputStream inputStream;
    private OutputStream outputStream;
   // private Thread mThreadClient = null;
  //  private String data = null;
    private static String hexString = "0123456789ABCDEF";
    private String datas = null;
    private StringBuilder stringBuilder = new StringBuilder();

    public ConnectThread(Socket socket,Handler handler) {

        Log.w("AAA","ConnectThread");
        this.socket = socket;
        this.handler = handler;

    }
        @Override
        public void run() {
            String sIP = "10.10.100.254";
            String sPort = "8899"; //找到端口号8899
            int port = Integer.parseInt(sPort); //String型转换为Int型
            Log.d("port", String.valueOf(port));
            Log.d("连接地址", "IP:" + sIP + ":" + port);
            try {
                socket = new Socket(sIP, port);
                try {
                    //inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    //获取客户端的IP地址
                    InetAddress address = InetAddress.getLocalHost();
                    Log.d("客户端的IP地址", String.valueOf(address));
                  // byte[] buffer = new byte[1024];
                   int bytes = 0;
                    while (bytes<=8){
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        byte data = dis.readByte();
                        // Log.d("bytes", String.valueOf(bytes));
                        // if (bytes > 0) {
                        Log.d("data", String.valueOf(data));
                        String n = Integer.toHexString(data);

                        Log.d("n", String.valueOf(n));
                        //拼接字符串
                        if (n.equals("0")) {
                            stringBuilder.append(n);
                            stringBuilder.append(0);
                        } else {
                            stringBuilder.append(n);
                        }
                        Log.d("stringBuilder", String.valueOf(stringBuilder));
                        datas = String.valueOf(stringBuilder);
                        bytes+=2;
                        Log.d("datas", String.valueOf(datas));
                        //int i = datas.length();
                        while (datas.length() ==8){
                            Message msg =Message.obtain();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", datas);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            // if (data != null) {
                            Log.w("AAA-11", "c:" +datas);
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    /**
     * 发送数据
     */
    public void sendData(String msg) {
        try {
            outputStream = socket.getOutputStream();
            //向服务器端发送消息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
