package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

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
    private NetConnection netConnection;
    private Concerto concerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        concerto = new Concerto();

        String a = "100062";
        Log.d("aaaa", String.valueOf(a.length()));
        String b = concerto.Dataconversion(a);
        Log.d("b", b);
        Log.d("调用工具类", "调用成功");
        if (netConnection.checkNetworkConnection(this)) {
            connectThread = new ConnectThread(socket, handler);
            connectThread.start();
        } else {
            Toast.makeText(this, "请连接wifi", Toast.LENGTH_SHORT).show();
        }
//        Timer timer = new Timer();
//            timer.schedule(new TimerTask(){
//                @Override
//                public void run() {
//                    reflush();
//                }
//            },5000);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            data = bundle.getString("msg");
            Log.d("wifi_data", data);
            if(data.length()<24){
                Toast.makeText(wifi.this,"网络错误！请检查网络连接",Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void reflush(){
        Intent intent = new Intent(this,wifi.class);
        startActivity(intent);
        finish();
    }
}
