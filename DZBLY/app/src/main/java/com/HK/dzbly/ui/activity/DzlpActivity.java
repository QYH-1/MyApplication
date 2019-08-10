package com.HK.dzbly.ui.activity;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.ordinary_measurement_fragment;
import com.HK.dzbly.ui.fragment.simple_measurement_fragment;
import com.HK.dzbly.utils.drawing.CompassView;
import com.HK.dzbly.utils.drawing.Elevation;
import com.HK.dzbly.utils.drawing.Rollangle;

import java.util.Random;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：地质参数仪首页
 * 修订历史：
 */
public class DzlpActivity extends FragmentActivity {
    //指南针变量
    private SensorManager mSensorManager;//获取传感器管理对象
    private SensorEventListener mSensorEventListener;//获取加速传感器对象
    private CompassView chaosCompassView; //获取指南针对象
    private float val;//方位角
    private CheckBox occurrence_survey;//产状测量
    private Switch selection_method;//测量模式选择
    private Elevation elevation;//仰角图示
    private Rollangle rollangle;//横滚角图示
    //创建fragment对象
    private ordinary_measurement_fragment mordinary_measurement_fragment;
    private simple_measurement_fragment msimple_measurement_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕旋转
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //锁定屏幕
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        //去掉标题栏（ActionBar实际上是设置在标题栏上的）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉状态栏(顶部显示时间、电量的部分)，设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dzlp);

        inint(); //获取控件
        selectFragment(0);//设置界面开始加载的fragment
        setSelection_method();//切换fregment
        setElevation(1); //设置仰角
        setRollangle(1); //设置横滚角
    }
    /**
     * 获取控件
     */
    public void inint(){
        //指南针变量
        chaosCompassView = (CompassView) findViewById(R.id.activity_compass_compassview);
        //获取SensorManager实例
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        occurrence_survey = findViewById(R.id.occurrence_survey);
        selection_method = findViewById(R.id.selection_method);
        elevation = findViewById(R.id.elevation);
        rollangle = findViewById(R.id.roll_angle);
    }
    /**
     * 对指南针进行操作
     */
    public void Compass(){
        mSensorEventListener = new SensorEventListener() {
            //重写onSensorChanged方法进行更新
            @Override
            public void onSensorChanged(SensorEvent event) {
               // <!--接受硬件的方位角的值-->
                //获取当前的方位角
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
     * 选择加载fragment
     */
    public void selectFragment(int position) {//设置传入第几值显示第几个fragment
        //创建FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //创建FragmentTransaction事务对象
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        switch (position) {
            case 0:
                //判断ordinary_measurement_fragment是否为空，无则创建fragment对象
                if (mordinary_measurement_fragment == null) {
                    mordinary_measurement_fragment =new ordinary_measurement_fragment();
                }
                //将原来的Fragment替换掉---此处R.id.fragmen指的是FrameLayout
                fragmentTransaction.replace(R.id.measurement_content, mordinary_measurement_fragment);
                break;
            case 1:
                if (msimple_measurement_fragment == null) {
                    msimple_measurement_fragment = new simple_measurement_fragment();
                }
                fragmentTransaction.replace(R.id.measurement_content, msimple_measurement_fragment);
                break;
            default:
                break;
        }
        //提交事务
        fragmentTransaction.commit();
    }
    /**
     * 通过Switch控件去切换fragment
     */
    private void setSelection_method(){
            selection_method.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                    {
                        selectFragment(0);
                    }else{
                        selectFragment(1);
                    }
                }
            });
    }
    /**
     * 画仰角图示
     * 根据硬件传值取调整指针的具体指向
     */
    private void setElevation(float x){
        Random random = new Random();
        // <!--接受硬件的仰角的值-->
        //获取数据
        elevation.cgangePer(90 / 180f);
    }
    /**
     * 画横滚角图示
     */
    private void setRollangle(float x){
        Random random = new Random();
        // <!--接受硬件的横滚角的值-->
        //获取数据
        float p = 0;
        rollangle.cgangePer(p / 360f);
    }
}
