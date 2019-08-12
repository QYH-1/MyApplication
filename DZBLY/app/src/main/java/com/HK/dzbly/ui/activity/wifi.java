package com.HK.dzbly.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.wifi.ConnectThread;

import java.net.Socket;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/12$
 * 描述：
 * 修订历史：
 */
public class wifi extends BaseActivity {
    private String data;
    private ConnectThread connectThread;
    private Socket socket;
   // private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("调用工具类","调用成功");
        connectThread  = new ConnectThread(socket,handler);
        connectThread.start();


    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            data =  bundle.getString("msg");
            Log.d("wifi_data",data);
        }

    };
}
