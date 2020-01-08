package com.HK.dzbly.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2020/1/2
 * 描述：
 * 修订历史：
 */
public class TestServiceOne extends Service {
    private final String TAG = "TestServiceOne";
    private ConnectThread mConnectThread = null;
    private ConnectedThread mConnectedThread = null;
    private WeakReference<Socket> mWeakReferSocket = null;
    private byte[] byteSS = null;
    private String message = "1";
    private DataCallback dataCallback = null;
    private List<DataCallback> list = null;
    private boolean isFlag = true;
    private long time = 200;

    //必须实现的方法  通过bindService()绑定到服务的客户端
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind方法被调用");
        System.out.println("--onBind()--");

        return new MyBinder();
    }

    // 解绑Servcie调用该方法
    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("--onUnbind()--");
        return super.onUnbind(intent);
    }

    //Service被创建时调用
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate方法被调用");
        super.onCreate();
        connect(); //连接
    }

    //Service被启动时调用  调用startService()启动服务时回调
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand方法被调用");
        byteSS = intent.getByteArrayExtra("data");
        time = intent.getLongExtra("time", 200);
        Log.e(TAG + "---", Arrays.toString(byteSS));
        Thread readthread = new Thread(MyRunnable);//启动一个线程
        readthread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable MyRunnable = new Runnable() {
        public void run() {
            //System.out.println(this.currentThread().getName() + "开始时间[" + new java.util.Date().getTime());
            while (true) {
                try {
                    Thread.sleep(285);
                    if (dataCallback != null) {
                        dataCallback.dataChanged(message);//传送数据到activity 数据可以为jason数据等 自己定义
                        message = "1";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //Service被销毁时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy方法被调用");
    }

    private synchronized void connect() {
        this.mConnectThread = new ConnectThread(this);
        this.mConnectThread.start();
    }

    //启动连接
    public class ConnectThread extends Thread {
        private TestServiceOne mmService = null;

        public ConnectThread(TestServiceOne service) {
            this.mmService = service;
        }

        @Override
        public void run() {
            super.run();
            Log.w(TAG, "BEGIN mConnectThread");
            boolean flag = true;
            while (flag) {
                try {
                    Thread.sleep(50L, 0);
                    try {
                        // Socket so = new Socket(mServerIp, mServerPort);
                        Socket so = new Socket();
                        so.setTcpNoDelay(false);
                        so.setKeepAlive(true);
                        so.setOOBInline(true);
                        so.connect(new InetSocketAddress("10.10.100.254", 8899), 5 * 1000);
                        mWeakReferSocket = new WeakReference<Socket>(so);
                    } catch (Exception e) {
                        return;
                    }
                    synchronized (TestServiceOne.this) {
                        this.mmService.mConnectThread = null;
                    }
                    this.mmService.connected();
                    flag = false;

                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    flag = true;
                }
            }
        }
    }

    public synchronized void connected() {
        this.mConnectedThread = new ConnectedThread(this, this.mWeakReferSocket.get());
        this.mConnectedThread.start();
    }

    private class ConnectedThread extends Thread {
        private TestServiceOne mmService = null;
        private Socket mmSocket = null;
        private DataInputStream mmInputStream = null;
        private OutputStream mmOutputStream = null;
        //StringBuilder stringBuilder = new StringBuilder();

        private boolean mIsStart = true;

        public ConnectedThread(TestServiceOne service, Socket socket) {
            mmService = service;
            mmSocket = socket;
            try {
                mmInputStream = new DataInputStream(socket.getInputStream());
                mmOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mIsStart = true;
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            //super.run();
            while (mIsStart == true && !mmSocket.isClosed()) {
                System.out.println(this.currentThread().getName() + "开始时间[" + new java.util.Date().getTime());
                StringBuilder stringBuilder = new StringBuilder();
                message = "1";
                Log.w(TAG, "BEGIN mConnectedThread");
                String buffer = null;
                int bytes = 0;
                Log.e(TAG, Arrays.toString(byteSS));
                if (mmSocket == null || mmInputStream == null
                        || mmOutputStream == null)
                    return;
                while (message.length() < 30) {
                    try {
                        mmOutputStream.write(byteSS);
                        bytes = mmInputStream.read();
                        Log.d(TAG, String.valueOf(bytes));
                        //将十六进制的数转换为二进制
                        String n = Integer.toHexString(bytes);
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
                        Log.i("====message=====", message);
                        message = String.valueOf(stringBuilder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "得到的数据： " + message);
                }
                System.out.println(this.currentThread().getName() + "结束时间[" + new java.util.Date().getTime());
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭连接", e);
            }
        }
    }

    public class MyBinder extends Binder {
        public TestServiceOne getService() {
            return TestServiceOne.this;
        }

        public void setData(byte[] bytes) {
            TestServiceOne.this.byteSS = bytes;
        }
    }

    public DataCallback getDataCallback() {
        return dataCallback;
    }

    public void setDataCallback(DataCallback dataCallback) {//注意这里以单个回调为例  如果是向多个activity传送数据 可以定义一个回调集合 在此处进行集合的添加
        this.dataCallback = dataCallback;
    }

    // 通过回调机制，将Service内部的变化传递到外部
    public interface DataCallback {
        void dataChanged(String str);
    }

}
