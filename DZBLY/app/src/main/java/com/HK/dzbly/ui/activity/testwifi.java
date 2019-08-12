package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.HK.dzbly.R;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/9$
 * 描述：
 * 修订历史：
 */
public class testwifi extends Activity {
    private EditText editText; //要发送的内容
    private Button sendButton;//发送按钮
    private TextView get_data;//显示WIFI传递过来的数据

    private boolean isConnecting = false;
    private Thread mThreadClient = null;
    private Socket mSocketClient = null;
    static BufferedReader mBufferedReaderClient = null;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String text;
   // private boolean socketStatus = false;


    //定义一个handler对象,用来刷新界面
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            String data = bundle.getString("msg");
           // Log.d("data",data);
           // get_data.setText(data);
          //  Log.d("读取到数据data",data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testwifi);
        editText = (EditText) this.findViewById(R.id.edittext);
        sendButton = (Button) this.findViewById(R.id.sendbutton);
        get_data = findViewById(R.id.get_data);

        mThreadClient = new Thread(mRunnable);
        mThreadClient.start();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 text = editText.getText().toString();
                 sendData(text);
                Log.d("发送的内容",text);
            }
        });
    }

    private Runnable mRunnable = new Runnable(){
            @Override
            public void run() {

                String sIP = "10.10.100.254";
                String sPort = "8899"; //找到端口号8899
                int port = Integer.parseInt(sPort); //String型转换为Int型
                Log.d("port", String.valueOf(port));
                Log.d("连接地址", "IP:" + sIP + ":" + port);

//                if(!socketStatus){
                    try {
                        //连接服务器
                        mSocketClient = new Socket(sIP, port);
//                        if(mSocketClient != null){
//                            socketStatus =true;
//                        }
                        //获取数据流
                        //getData();
                        try {
                            inputStream = mSocketClient.getInputStream();
                            outputStream = mSocketClient.getOutputStream();
                            //获取客户端的IP地址
                            InetAddress address = InetAddress.getLocalHost();
                            Log.d("客户端的IP地址", String.valueOf(address));
                            byte[] buffer = new byte[1024];
                            int bytes;
                            while (true) {
                                //读取数据
                                bytes = inputStream.read(buffer);
                                if (bytes > 0) {
                                    final byte[] data = new byte[bytes];
                                    System.arraycopy(buffer, 0, data, 0, bytes);

                                    Message msg = new Message();
                                    msg.what = 1;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("msg", new String(data));
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                    if (data != null) {
                                        Log.w("AAA", "c:" + bytes);
                                    }

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.w("提示", "主机无法连接");
                    }
                }

//            }
        };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnecting) {
            isConnecting = false;
        }
            if (mSocketClient != null) {
                try {
                    mSocketClient.close();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mThreadClient.interrupt();
            }
    }

    /**
     * 获取数据
     */
    private void getData() throws IOException {
        try {
            inputStream = mSocketClient.getInputStream();
            outputStream = mSocketClient.getOutputStream();
            //获取客户端的IP地址
            InetAddress address = InetAddress.getLocalHost();
            Log.d("客户端的IP地址", String.valueOf(address));
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                //读取数据
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);

                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", new String(data));
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    if (data != null) {
                        Log.w("AAA", "读取到数据:" + new String(data));
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     * @param text1 String(data)
     */
    public void sendData(final String text1){

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
//                if (socketStatus){
                    try {
                        outputStream = mSocketClient.getOutputStream();
                        outputStream.write(text1.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//            }
        };
        thread.start();
    }
}
