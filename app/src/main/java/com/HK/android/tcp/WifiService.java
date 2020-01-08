package com.HK.android.tcp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.HK.dzbly.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2020/1/2
 * 描述：
 * 修订历史：
 */
public class WifiService extends Service {
    public final static boolean D = false;
    public final static String TAG = "wifiService";
    public final static String DEFAULT_SERVER_IP = "10.10.100.254";//连接ip
    public final static int DEFAULT_SERVER_PORT = 8899; //连接端口号
    private final static int SOCKET_CONNECT_TIMOUT = 5 * 1000;  //连接超时时间
    private static final long HEART_BEAT_RATE = 3 * 1000;

    public static final String CQCCTEG_SEND_MESSAGE_ACTION = "com.cqccteg.send_message_ACTION";
    public static final String CQCCTEG_END_SERVICE_ACTION = "com.cqccteg.ends_service_ACTION";
    public static final String CQCCTEG_MESSAGE_ACTION = "com.cqccteg.message_ACTION";
    public static final String CQCCTEG_HEART_BEAT_ACTION = "com.cqccteg.heart_beat_ACTION";
    public static final String CQCCTEG_ERROR_ACTION = "com.cqccteg.error_ACTION";
    public static final String CQCCTEG_RECEIVE_LEN = "com.cqccteg.receive_len";
    public static final String CQCCTEG_RECEIVE_DAT = "com.cqccteg.receive_dat";
    public static final String CQCCTEG_REQUEST_CLASS = "com.cqccteg.request_class";
    public static final String CQCCTEG_REQUEST_RESID = "com.cqccteg.request_resid";


    public static final int STATE_NONE = 0; //无任何操作
    public static final int STATE_CONNECTING = 1; //现在启动传出连接
    public static final int STATE_CONNECTED = 2;  //现在连接到远程设备
    private long mSendTime = 0L;
    private boolean mDisableAutoReconnect = true;
    private int mTryReconnect = 5;
    private int mPingType = 1;
    private boolean mContinuePing = true;
    private int mPingResult = -1;
    private int mResId = 0;

    private String mServerIp = DEFAULT_SERVER_IP; //连接ip
    private int mServerPort = DEFAULT_SERVER_PORT;//连接端口号
    private int mState = STATE_NONE;  //状态标志

    private ConnectThread mConnectThread = null;
    private ConnectedThread mConnectedThread = null;
    private LocalBroadcastManager mLocalBroadcastManager = null;
    private WeakReference<Socket> mWeakReferSocket = null;
    private TCPPing mTCPPing = new TCPPing();
    private Class<?> mNotificationClass = null;
    private ForegroundService mForegroundService = null;
    private MessageSendEndBackReciver mSendEndReciver = null;

    /**
     * 当服务被创建时调用.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "--- ON CREATE ---");

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mState = STATE_NONE;
        connect(); //连接
    }

    /**
     * 通过bindService()绑定到服务的客户端
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iTCPClientService;
    }

    /**
     * 调用startService()启动服务时回调
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get parameter
        Log.w(TAG,"onStartCommand");
        if (D) {
            Log.w(TAG, "--- ON_START_COMMAND ---");
        }

        mSendEndReciver = new MessageSendEndBackReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CQCCTEG_SEND_MESSAGE_ACTION);
        filter.addAction(CQCCTEG_END_SERVICE_ACTION);
        registerReceiver(mSendEndReciver, filter);

        try {
            mState = STATE_NONE;

            if (intent != null) {
                mResId = intent.getIntExtra(CQCCTEG_REQUEST_RESID, 0);
                String sendClass = (String) intent
                        .getSerializableExtra(CQCCTEG_REQUEST_CLASS);

                mNotificationClass = Class.forName(sendClass);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通过bindService()将客户端绑定到服务时调用
     */
    @Override
    public void onRebind(Intent intent) {

        super.onRebind(intent);
    }

    /**
     * 通过unbindService()解除所有客户端绑定时调用
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 服务不再有用且将要被销毁时调用
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (D) {
            Log.w(TAG, "--- ON_DESTORY ---");
        }

        try {
            if (mSendEndReciver != null) {
                unregisterReceiver(mSendEndReciver);
                mSendEndReciver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mForegroundService != null)
            mForegroundService
                    .stopForegroundCompat(ForegroundService.NOTIFICATION_ID);
    }

    private synchronized void connect() {
        if (mState == STATE_CONNECTED)
            return;
        // =======================================================================
        if (mState == STATE_CONNECTING || mState == STATE_CONNECTED)
            return;

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        this.mConnectThread = new ConnectThread(this);
        this.mConnectThread.start();

        setState(STATE_CONNECTING);
    }
    //启动连接
    public class ConnectThread extends Thread {
        private WifiService mmService = null;

        public ConnectThread(WifiService service) {
            this.mmService = service;
        }

        @Override
        public void run() {
            super.run();

            Log.w(TAG, "BEGIN mConnectThread");
            setName("TCPClient_ConnectThread");

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

                        so.connect(
                                new InetSocketAddress(mServerIp, mServerPort),
                                SOCKET_CONNECT_TIMOUT);

                        mWeakReferSocket = new WeakReference<Socket>(so);

                        mHandler.postDelayed(mHeartBeatRunnable,
                                HEART_BEAT_RATE);
                    } catch (Exception e) {
                        this.mmService.connectionFailed();
                        mHandler.postDelayed(mHeartBeatRunnable,
                                HEART_BEAT_RATE);
                        return;
                    }

                    synchronized (WifiService.this) {
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

        public void cancel() {
            try {
                if (null != mWeakReferSocket) {
                    Socket sk = mWeakReferSocket.get();
                    if (!sk.isClosed()) {
                        sk.close();
                    }
                    sk = null;
                    mWeakReferSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private WifiService mmService = null;
        private Socket mmSocket = null;
        private InputStream mmInputStream = null;
        private OutputStream mmOutputStream = null;

        private boolean mIsStart = true;

        public ConnectedThread(WifiService service, Socket socket) {
            mmService = service;
            mmSocket = socket;
            try {
                mmInputStream = socket.getInputStream();
                mmOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mIsStart = true;
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            Log.w(TAG, "BEGIN mConnectedThread");
            setName("TCPClient_ConnectedThread");

            byte[] buffer = new byte[1024];
            int bytes = 0;

            if (mmSocket == null || mmInputStream == null
                    || mmOutputStream == null)
                return;

            while (mIsStart == true && !mmSocket.isClosed()) {
                try {
                    bytes = mmInputStream.read(buffer);
                    String message = new String(Arrays.copyOf(buffer, bytes))
                            .trim();
                    if (message.equals("ok")) {
                        Intent intent = new Intent(CQCCTEG_HEART_BEAT_ACTION);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(CQCCTEG_MESSAGE_ACTION);
                        intent.putExtra(CQCCTEG_RECEIVE_LEN, bytes);
                        intent.putExtra(CQCCTEG_RECEIVE_DAT, buffer);
                        mLocalBroadcastManager.sendBroadcast(intent);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "disconnected", e);
                    this.mmService.connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mmOutputStream.write(buffer);
            } catch (Exception e) {
                e.printStackTrace();

                Intent intent = new Intent(CQCCTEG_ERROR_ACTION);
                intent.putExtra(CQCCTEG_ERROR_ACTION, "д???????");
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "????????????", e);
            }
        }
    }
    //打印当前状态
    private synchronized void setState(int state) {
        if (D)
            Log.w(TAG, "setState() " + mState + " -> " + state);
        this.mState = state;
    }

    private Handler mHandler = new Handler();
    private Runnable mHeartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            if (System.currentTimeMillis() - mSendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = false;
                if (mPingType == 1)
                    pingServer(mPingType);
                else
                    isSuccess = pingServer(mPingType);

                Intent intent = new Intent(CQCCTEG_HEART_BEAT_ACTION);
                mLocalBroadcastManager.sendBroadcast(intent);

                if (mPingType != 1) {
                    if (isSuccess == false || mState == STATE_NONE) {
                        try {
                            mHandler.removeCallbacks(mHeartBeatRunnable);
                            connect();
                        } catch (Exception e) {
                        }
                    }
                }

            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };
    //连接失败
    private void connectionFailed() {
        setState(STATE_NONE);

        Intent intent = new Intent(CQCCTEG_ERROR_ACTION);
        intent.putExtra(CQCCTEG_ERROR_ACTION, "?豸????????");
        mLocalBroadcastManager.sendBroadcast(intent);
        //Device Connected Defail

        // reconnect if connect failed
        if (this.mTryReconnect > 0) {
            this.mTryReconnect--;

            try {
                mHandler.removeCallbacks(mHeartBeatRunnable);
                connect();
            } catch (Exception e) {
            }
        }
    }
    public synchronized void connected() {

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        this.mConnectedThread = new ConnectedThread(this,
                this.mWeakReferSocket.get());
        this.mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    private void connectionLost() {
        setState(STATE_NONE);

        Intent intent = new Intent(CQCCTEG_ERROR_ACTION);
        intent.putExtra(CQCCTEG_ERROR_ACTION, "连接丢失！");
        mLocalBroadcastManager.sendBroadcast(intent);

        if (!this.mDisableAutoReconnect) {
            this.mTryReconnect = 5;

            try {
                mHandler.removeCallbacks(mHeartBeatRunnable);
                this.connect();
            } catch (Exception e) {
            }
        }
    }
    //返回连接类型
    private boolean pingServer(int pingType) {
        boolean flag = false;

        if (pingType == 1) { // ping command
            if (mContinuePing == true) {
                mContinuePing = false;
                mTCPPing.threadExecuteCommand("ping -c 1 -w 100 " + mServerIp);
                new Thread() {
                    public void run() {
                        while (mTCPPing.isPingEnd() == false) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mPingResult = mTCPPing.getStatus();
                        mContinuePing = true;

                        if (mPingResult != 0 || mState == STATE_NONE) {
                            mState = STATE_NONE;
                            mHandler.removeCallbacks(mHeartBeatRunnable);
                            connect();
                        }
                    }
                }.start();
            }
        } else if (pingType == 2) { // sendUrgentData
            try {
                this.mWeakReferSocket.get().sendUrgentData(0xFF);
                mSendTime = System.currentTimeMillis();
                flag = true;
            } catch (java.net.SocketException ese) {
                ese.printStackTrace();
                flag = false;
            } catch (java.io.IOException eio) {
                eio.printStackTrace();
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        } else { // write own define data
            flag = write("\r\n".getBytes());
        }

        return flag;
    }
    public boolean write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            r = this.mConnectedThread;
        }

        try {
            r.write(out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mSendTime = System.currentTimeMillis();
        return true;
    }
    private ITCPClientBackService.Stub iTCPClientService = new ITCPClientBackService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public boolean sendData(byte[] data) throws RemoteException {
            return write(data);
        }

        @Override
        public void foregroundService(String notifyTitle, String notifyInfo) throws RemoteException {
            foregroundNotification(notifyTitle, notifyInfo);
        }

        @Override
        public boolean isConnected() throws RemoteException {
            return (mState == STATE_CONNECTED);
        }
    };

    /**
     * @param notifyTitle
     * @param notifyInfo
     */
    @SuppressWarnings("unused")
    @SuppressLint("NewApi")
    public void foregroundNotification(String notifyTitle, String notifyInfo) {

        try {
            if (this.mNotificationClass == null)
                return;

            if (mForegroundService == null)
                mForegroundService = new ForegroundService(this);

            Notification.Builder builder = new Notification.Builder(this);

            Intent notificationIntent = new Intent(this,
                    this.mNotificationClass);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if (contentIntent == null)
                return;
            builder.setContentIntent(contentIntent);

            builder.setSmallIcon(mResId == 0 ? R.drawable.ic_launcher : mResId);
            builder.setTicker("Foreground Service Start");
            builder.setAutoCancel(true);
            builder.setOngoing(false);
            builder.setContentTitle(notifyTitle);
            builder.setContentText(notifyInfo);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            mForegroundService.startForegroundCompat(
                    ForegroundService.NOTIFICATION_ID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class MessageSendEndBackReciver extends BroadcastReceiver {
        public MessageSendEndBackReciver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CQCCTEG_SEND_MESSAGE_ACTION)) {
                byte[] data = intent.getByteArrayExtra(CQCCTEG_SEND_MESSAGE_ACTION);
                write(data);
            } else if (action.equals(CQCCTEG_END_SERVICE_ACTION)) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(mHeartBeatRunnable);
                }
                if (mConnectedThread != null) {
                    mConnectedThread.cancel();
                }
                if (mConnectThread != null)
                    mConnectThread.cancel();
                stopSelf();
            }
        }
    }
}
