package com.HK.dzbly.ui.activity;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.ordinary_measurement_fragment;
import com.HK.dzbly.ui.fragment.simple_measurement_fragment;
import com.HK.dzbly.utils.drawing.CompassView;
import com.HK.dzbly.utils.drawing.Elevation;
import com.HK.dzbly.utils.drawing.Rollangle;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

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

    private ConnectThread connectThread; //获取wifi连接对象
    private Socket socket;
    //  private Handler handler;
    private String data;//wifi传递过来的数据
    private SharedPreferences sp; //用来保存数据，传递给fragment进行存储
    private NetConnection netConnection;//检查网络连接
    private Concerto concerto;//处理wifi传递过来的数据

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
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象
        concerto = new Concerto();
        inint(); //获取控件
        selectFragment(0);//设置界面开始加载的fragment
        setSelection_method();//切换fregment

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectThread = new ConnectThread(socket, handler);
                connectThread.start();
                Log.d("connectThread", "启动成功");
            }
        }, 100);
    }

    /**
     * 获取控件
     */
    public void inint() {
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
    public void Compass(float val) {
        mSensorEventListener = new SensorEventListener() {
            //重写onSensorChanged方法进行更新
            @Override
            public void onSensorChanged(SensorEvent event) {
                // <!--接受硬件的方位角的值-->
                //获取当前的方位角
                //通过线程进行view的刷新
                chaosCompassView.setVal(val);
                Log.d("val", String.valueOf(val));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        //注册传感器
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * 传感器取消注册
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
                    mordinary_measurement_fragment = new ordinary_measurement_fragment();
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
    private void setSelection_method() {
        selection_method.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectFragment(0);
                } else {
                    selectFragment(1);
                }
            }
        });
    }

    /**
     * 画仰角图示
     * 根据硬件传值取调整指针的具体指向
     */
    private void setElevation(String x) {
        //Random random = new Random();
        // <!--接受硬件的仰角的值-->
        //获取数据
        float eada = Float.parseFloat(x);
        float el = 90;
        if ((x.substring(0, 1).equals("-"))) {
            el = 90 - Math.abs(Float.parseFloat(x));
        } else {
            el = Float.parseFloat(x) + 90;

        }
        Log.d("DzlpActivity_el", String.valueOf(el));
        Log.d("DzlpActivity_eada", String.valueOf(eada));
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("eada", eada);
        editor.commit();
        elevation.cgangePer(el / 180f, eada);
    }

    /**
     * 画横滚角图示
     */
    private void setRollangle(String x) {
        //Random random = new Random();
        // <!--接受硬件的横滚角的值-->
        //获取数据

        float rana = Math.abs(Float.parseFloat(x));
        float p = Math.abs(Float.parseFloat(x));
        Log.d("rana111111", String.valueOf(rana));
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("rana", rana);
        editor.commit();
        rollangle.cgangePer(p / 360f, rana);
    }

    /**
     * 传递指南针数据
     */
    private void setChaosCompassView(String x) {
        float val = Float.parseFloat(x);
        chaosCompassView.CompassViewdata(val);
    }

    /**
     * 对fragment中ui进行更新
     * 给出结果控件的值
     */
    @SuppressLint("ResourceAsColor")
    private void setFragment(String data) {
        //当选择了产状测量控件后才显示结果
        if (occurrence_survey.isChecked()) {
            TextView textView = mordinary_measurement_fragment.getView().findViewById(R.id.explain);
            textView.setText(data);
            textView.setTextSize(35);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("result", data);
            editor.commit();
        }

    }

    /**
     * 接收wifi的数据，并对控件进行设置
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            data = bundle.getString("msg");

            Log.d("DzlpActivity_data", data);
            String data1 = null;
            String data2 = null;
            String data3 = null;
            String data4 = null;
            String data5 = null;
            if (data.length() == 24) {
                //对wifi获取的数据进行处理
                //俯仰角
                data1 = concerto.Dataconversion(data.substring(0, 5));
                Log.d("data1-dzlpActivity", String.valueOf(data1));
                //横滚角
                data2 = concerto.Dataconversion(data.substring(6, 12));
                //方位角
                data3 = concerto.Dataconversion(data.substring(12, 18));
                Log.d("DzlpActivity_data3", data3);
                //结果显示
                //当俯仰角的值在±1之间时，产状信息显示横滚角
                if (Math.abs(Float.valueOf(data1)) <= 1) {
                    String t = data3.substring(0, data3.indexOf("."));
                    int t1 = Integer.parseInt(t) + 180;
                    if (t1 / 360 > 1) {
                        t1 = t1 - 360;
                    }
                    String tmp = t1 + data3.substring(data3.indexOf("."));
                    float temp = Float.parseFloat(tmp);
                    Log.d("temp", String.valueOf(temp));
                    data5 = String.valueOf(temp);
                    Log.d("data5", data5);
                    data4 = data5 + "∠" + data2;
                    //调用显示
                    setFragment(data4);
                } else if (Math.abs(Float.valueOf(data2)) <= 1) {
                    //当横滚角的值在±1之间时，产状信息使用俯仰角
                    String t = data3.substring(0, data3.indexOf("."));
                    int t1 = Integer.parseInt(t) + 180;
                    if (t1 / 360 > 1) {
                        t1 = t1 - 360;
                    }
                    String tmp = t1 + data3.substring(data3.indexOf("."));
                    float temp = Float.parseFloat(tmp);
                    Log.d("temp", String.valueOf(temp));
                    data5 = String.valueOf(temp);
                    Log.d("data5", data5);
                    data4 = data5 + "∠" + data1;
                    //调用显示
                    setFragment(data4);
                }

                //将最新的数据存储起来
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("setElevation", data1);
                editor.putString("setRollangle", data2);
                editor.putString("Compass", data3);
                editor.putString("setFragment", data4);

            } else {
                //当wifi传递来的数据不完整或者为空时，将前面最新的数据用于显示
                data1 = sp.getString("setElevation", "0.0");
                data2 = sp.getString("setRollangle", "0.0");
                data3 = sp.getString("Compass", "0.0");
                data4 = sp.getString("setFragment", "");
            }
            //改变控件的显示
            setElevation(data1);
            setRollangle(data2);
            //setFragment(data4);
            setChaosCompassView(data3);
        }
    };

}
