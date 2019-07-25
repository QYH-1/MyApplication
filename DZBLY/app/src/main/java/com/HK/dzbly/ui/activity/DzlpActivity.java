package com.HK.dzbly.ui.activity;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.ordinary_measurement_fragment;
import com.HK.dzbly.ui.fragment.simple_measurement_fragment;
import com.HK.dzbly.utils.CompassView;

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
    private float val;
    //顶端选项栏
    private ImageView indicator_light;//指示灯
    private TextView jgcj;//激光测距
    private TextView lpsz;//罗盘设置
    private TextView sjgl;//数据管理
    private TextView elevation;//仰角
    private TextView roll_angle;//横滚角
    private CheckBox occurrence_survey;//产状测量
    private Switch selection_method;//测量模式选择
    //创建fragment对象
    private ordinary_measurement_fragment mordinary_measurement_fragment;
    private simple_measurement_fragment msimple_measurement_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dzlp);
        inint(); //获取控件
        setLpsz();//罗盘设置
        setSjgl();//数据管理
        selectFragment(0);//设置界面开始加载的fragment
        setSelection_method();//切换fregment
        setJgcj();//激光测距
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
        indicator_light = findViewById(R.id.indicator_light);
        elevation = findViewById(R.id.elevation);
        roll_angle = findViewById(R.id.roll_angle);
        occurrence_survey = findViewById(R.id.occurrence_survey);
        selection_method = findViewById(R.id.selection_method);
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
    //激光测距
    private void setJgcj(){
        jgcj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DzlpActivity.this,Laser_rangingActivity.class);
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
}
