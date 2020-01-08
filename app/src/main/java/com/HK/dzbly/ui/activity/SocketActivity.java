package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.wifi.Concerto;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/12/31
 * 描述：
 * 修订历史：
 */
public class SocketActivity extends BaseActivity implements View.OnClickListener {
    private TextView socketData1, socketData2, socketData3;
    private Concerto concerto;//处理wifi传递过来的数据
    private Button startService, stopService;
    private byte[] bytes = {69, 73, 87, 0, 1};
    MyServiceConn myServiceConn;
    TestServiceOne.MyBinder binder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket);

        socketData1 = findViewById(R.id.data1);
        socketData2 = findViewById(R.id.data2);
        socketData3 = findViewById(R.id.data3);
        startService = findViewById(R.id.startService);
        stopService = findViewById(R.id.stopService);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        concerto = new Concerto();
        myServiceConn = new MyServiceConn();
    }

    @Override
    public void onClick(View v) {
        final Intent it = new Intent(this, TestServiceOne.class);
        //用intent启动Service并传值
        it.putExtra("data", bytes);
        switch (v.getId()) {
            case R.id.startService:
                startService(it);
                //绑定Service
                bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
                //注意：需要先绑定，才能同步数据
                if (binder != null) {
                    binder.setData(bytes);
                }
                Log.d("SocketActivity", "---------");
                break;
            case R.id.stopService:
                //stopService(it);
                stopService(new Intent(SocketActivity.this, TestServiceOne.class));
                break;
        }
    }

    class MyServiceConn implements ServiceConnection {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // IBinder service为onBind方法返回的Service实例
            binder = (TestServiceOne.MyBinder) service;
            binder.getService().setDataCallback(new TestServiceOne.DataCallback() {
                //执行回调函数
                @Override
                public void dataChanged(String str) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("str", str);
                    msg.setData(bundle);
                    //发送通知
                    handler.sendMessage(msg);
                    Log.d("--str--", str);
                }
            });
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //在handler中更新UI
                String data = msg.getData().getString("str");
                Log.i("----data----", data);
                if (data.length() == 30) {
                    socketData3.setText(concerto.Dataconversion(data.substring(0, 6)));
                }
            }
        };

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
    }
}
