package com.HK.dzbly.utils.wifi;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/12$
 * 描述：wifi连接线程
 * 修订历史：
 */
public class ConnectThread extends Thread {

    private Socket socket;   //通信socket
    private Handler handler;    //用来更新UI等(发送信息给主线程)
    private OutputStream outputStream;  //数据输出流
    //如果WIFI没有打开，则打开WIFI
    private static final int WIFI_CONNECT_TIMEOUT = 20; //连接WIFI的超时时间

    private static String hexString = "0123456789ABCDEF";
    private String datas = null;
    private StringBuilder stringBuilder = new StringBuilder();
    private byte msg = (byte) 0x01;
    private String sIP = "10.10.100.254";
    private String sPort = "8899"; //找到端口号8899
    private int port = Integer.parseInt(sPort); //String型转换为Int型

    public ConnectThread(Socket socket, Handler handler) {
        Log.w("AAA", "ConnectThread");
        this.socket = socket;
        this.handler = handler;

    }

    @Override
    public void run() {
        Log.d("port", String.valueOf(port));
        Log.d("连接地址", "IP:" + sIP + ":" + port);
        try {
            //创建与热点通信的socket
            socket = new Socket(sIP, port);
            Log.i("接受wifi的数据","数据接受");
            try {
                //向服务器端发送消息
                outputStream = socket.getOutputStream();
                outputStream.write(msg);
                Log.d("向服务器端发送消息", String.valueOf(msg));
                outputStream.flush();

                InetAddress address = InetAddress.getLocalHost();
                Log.d("客户端的IP地址", String.valueOf(address));
                while (true) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    try {
                        //接受wifi的数据
                        int data = dis.read();
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
                        //当接受的wifi的数据为24位的整数倍时，通过handler向界面传递数据
                        while (datas.length() % 24 == 0 && datas.length() != 0) {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", datas);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            Log.w("AAA-11", "c:" + datas);
                            //当数据传递后将接受变量置为空，方便下次接受
                            datas = null;
                            stringBuilder = null;
                            break;
                        }
                        //socket.close();
                    } catch (NullPointerException e) {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
