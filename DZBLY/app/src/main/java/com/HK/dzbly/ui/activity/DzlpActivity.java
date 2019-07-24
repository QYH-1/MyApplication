package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.CompassView;

public class DzlpActivity extends BaseActivity {
    //指南针变量
    private SensorManager mSensorManager;//获取传感器管理对象
    private SensorEventListener mSensorEventListener;//获取加速传感器对象
    private CompassView chaosCompassView; //获取指南针对象
    private float val;
    //顶端选项栏
    private TextView jgcj;//激光测距
    private TextView lpsz;//罗盘设置
    private TextView sjgl;//数据管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dzlp);
        inint(); //获取控件
        setLpsz();//罗盘设置
        setSjgl();//数据管理
    }

    /**
     * 获取控件
     */
    public void inint(){
        //指南针变量
        chaosCompassView = (CompassView) findViewById(R.id.activity_compass_compassview);
        //获取SensorManager实例
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取顶端选项
        jgcj = findViewById(R.id.jgcj);
        lpsz = findViewById(R.id.lpsz);
        sjgl = findViewById(R.id.sjgl);

    }
    /**
     * 对指南针进行操作
     */
    public void Compass(){
        mSensorEventListener = new SensorEventListener() {
            //重写onSensorChanged方法进行更新
            @Override
            public void onSensorChanged(SensorEvent event) {
                val = event.values[0];
                //通过线程进行view的刷新
                chaosCompassView.setVal(val);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        //注册传感器
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     *传感器取消注册
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorEventListener);
    }
    /**
     * 罗盘设置
     */
    private void setLpsz(){
        lpsz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DzlpActivity.this,LpszActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 数据管理
     */
    private void setSjgl(){
        sjgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//调用文件浏览器API
                intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
    }
}
