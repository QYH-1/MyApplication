package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.HK.dzbly.R;
import com.HK.dzbly.utils.drawing.LevelHew;
import com.HK.dzbly.utils.drawing.LevelVew;
import com.HK.dzbly.utils.drawing.LevelView;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ReceiveMsg;
import com.HK.dzbly.utils.wifi.Send;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/8
 * 描述：实现水平仪
 * 修订历史：
 */
public class LevelActivity extends AppCompatActivity implements SensorEventListener {
    private static final int PITCH = 1;   //水平上下 y
    private static final int ROLL = 2;     //水平左右 x

    private SensorManager mSensorManager;
    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];
    // 旋转矩阵，用来保存磁场和加速度的数据
    private float[] mRotationMatrix = new float[9];
    // 模拟方向传感器的数据（原始数据为弧度）
    private float[] mOrientationAngles = new float[3];

    private LevelView levelView;
    private LevelHew levelHew;
    private LevelVew levelVew;
    private TextView tvHorz, tvl_horz;
    private TextView tvVert, tvl_vertical;
    private Timer timer;
    private Socket socket;
    private OutputStream outputStream;  //数据输出流
    private DataInputStream inputStream;
    private Send send;
    private ReceiveMsg receiveMsg;
    private String data;//wifi传递过来的数据
    private String data1 = null; //俯仰角
    private String data2 = null; //横滚角
    private SharedPreferences sp; //用来保存数据，传递给fragment进行存储
    private Concerto concerto;//处理wifi传递过来的数据

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//当activity继承AppCompatActivity时隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.level);

        levelView = findViewById(R.id.level_view);
        levelHew = findViewById(R.id.level_h);
        levelVew = findViewById(R.id.level_v);
        tvVert = findViewById(R.id.tvv_vertical);
        tvl_vertical = findViewById(R.id.tvl_vertical);
        tvl_horz = findViewById(R.id.tvl_horz);
        tvHorz = findViewById(R.id.tvv_horz);

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Data();
//            }
//        }, 0, 2000 * 2);
        //获取传感器服务
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    private void Data() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.10.100.254", 8899);
                    inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = socket.getOutputStream();
                    try {
                        Log.i("-------------timer", "timer");
                        byte[] bytes = {69, 73, 87, 0, 0};
                        send.sendData(outputStream, bytes);
                        Log.i("receiveMsg", "receiveMsg");
                        receiveMsg = new ReceiveMsg();
                        receiveMsg.receiveMsg(inputStream, handler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 接收wifi的数据，并对控件进行设置
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            data = bundle.getString("msg");
            Log.d("DzlpActivity_data", data);
            if (data.length() == 30) {
                //对wifi获取的数据进行处理
                //俯仰角
                data1 = concerto.Dataconversion(data.substring(0, 6));
                Log.d("data1-dzlpActivity", String.valueOf(data1));
                //横滚角
                data2 = concerto.Dataconversion(data.substring(6, 12));
                //向画图类传递传感器读取到的值
                updateOrientationAngles(Float.valueOf(data2), Float.valueOf(data1));
            }
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 给传感器注册监听：
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // 取消方向传感器的监听
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // 取消方向传感器的监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 获取手机触发event的传感器的类型
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
                break;

        }
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        // 获取沿着X轴倾斜时与Y轴的夹角
        float pitchAngle = mOrientationAngles[PITCH];
        // 获取沿着Y轴的滚动时与X轴的角度
        float rollAngle = -mOrientationAngles[ROLL];

        //当出现水平时，改变字体的颜色
        if (Math.abs(Math.toDegrees(rollAngle)) < 1) {
            tvHorz.setTextColor(Color.GREEN);
            tvl_horz.setTextColor(Color.GREEN);
        } else {
            tvHorz.setTextColor(Color.WHITE);
            tvl_horz.setTextColor(Color.WHITE);
        }
        if (Math.abs(Math.toDegrees(pitchAngle)) < 1) {
            tvVert.setTextColor(Color.GREEN);
            tvl_vertical.setTextColor(Color.GREEN);
        } else {
            tvVert.setTextColor(Color.WHITE);
            tvl_vertical.setTextColor(Color.WHITE);
        }
       updateOrientationAngles(rollAngle, pitchAngle);
    }

    /**
     * 角度变更后显示到界面
     *
     * @param rollAngle
     * @param pitchAngle
     */
    public void updateOrientationAngles(float rollAngle, float pitchAngle) {
        levelView.setAngle(rollAngle, pitchAngle);
        levelHew.setAngle(rollAngle, pitchAngle);
        levelVew.setAngle(rollAngle, pitchAngle);

        tvHorz.setText(String.valueOf((int) Math.toDegrees(rollAngle)) + "°");
        tvVert.setText(String.valueOf((int) Math.toDegrees(pitchAngle)) + "°");
    }
}
