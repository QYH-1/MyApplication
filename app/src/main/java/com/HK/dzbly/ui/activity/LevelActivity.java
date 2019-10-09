package com.HK.dzbly.ui.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.HK.dzbly.R;
import com.HK.dzbly.utils.drawing.LevelHew;
import com.HK.dzbly.utils.drawing.LevelVew;
import com.HK.dzbly.utils.drawing.LevelView;

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
    private TextView tvHorz;
    private TextView tvVert;

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
        tvHorz = findViewById(R.id.tvv_horz);

        //获取传感器服务
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

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
