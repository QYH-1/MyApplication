package com.HK.dzbly.ui.activity;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import com.HK.dzbly.utils.wifi.ConnectThread;

import java.net.Socket;

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

        connectThread = new ConnectThread(socket,handler);
        connectThread.start();
        Log.d("connectThread","启动成功");
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
    public void Compass(final float val){
        mSensorEventListener = new SensorEventListener() {
            //重写onSensorChanged方法进行更新
            @Override
            public void onSensorChanged(SensorEvent event) {
               // <!--接受硬件的方位角的值-->
                //获取当前的方位角
               // val = event.values[0];
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
    private void setElevation(String x){
        //Random random = new Random();
        // <!--接受硬件的仰角的值-->
        //获取数据
        float eada = Float.parseFloat(x);
        float el = 90;
        if((x.substring(0,1).equals("-"))){
            el = 90-Math.abs(Float.parseFloat(x));
        }else {
             el =Float.parseFloat(x)+90;

        }
        Log.d("DzlpActivity_el", String.valueOf(el));
        Log.d("DzlpActivity_eada", String.valueOf(eada));
        elevation.cgangePer(el / 180f,eada);
    }
    /**
     * 画横滚角图示
     */
    private void setRollangle(String x){
        //Random random = new Random();
        // <!--接受硬件的横滚角的值-->
        //获取数据

        float rana =Math.abs(Float.parseFloat(x));
        float p =Math.abs(Float.parseFloat(x));
        Log.d("rana111111", String.valueOf(rana));

        rollangle.cgangePer(p / 360f,rana);
    }
    /**
     * 传递指南针数据
     */
    private void setChaosCompassView(String x){
        float val = Float.parseFloat(x);
        chaosCompassView.CompassViewdata(val);
    }

    /**
     * 接收wifi的数据，并对控件进行设置
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            data =  bundle.getString("msg");

            Log.d("DzlpActivity_data",data);
            //对wifi获取的数据进行处理
            String data1 = Concerto(data);
            Log.d("DzlpActivity_data1",data1);
            //改变控件的显示
            setElevation(data1);
            setRollangle(data1);
            Compass(120);
            //setChaosCompassView(String.valueOf(120));
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask(){
//                @Override
//                public void run() {
//                    reflush();
//                }
//            },1000);

        }
    };
    private void reflush(){
//            connectThread.stop();
//            connectThread = new ConnectThread(socket,handler);
//            connectThread.start();
       // finish();
       // Intent intent = new Intent(this, DzlpActivity.class);
       // startActivity(intent);

    }
    /**
     * 处理wifi传递过来的数据
     */
    private String Concerto(String data){
        String integrate = null;
        String decimal = null;
        String dana = null;
        Log.d("DzlpActivity_datav",data);

        //接收第三位为符号位
        String str1 = data.substring(2,3);
        Log.d("str1",str1);

        //接收的4、5、6位为整数部分
        String str2 = data.substring(3,6);
        Log.d("str2",str2);
        if(str2.substring(0,1).equals("0")){
           if(str2.substring(1,2).equals("0")){
               integrate = str2.substring(2);
           }else{
               integrate = str2.substring(1);
           }
        }else {
            integrate = str2;
        }
        //后两位为小数部分
        String str3 = data.substring(6);
        Log.d("str3",str3);
        if(str3.substring(0,1).equals("0") && str3.substring(1).equals("0")){
            decimal = "0";
        }else if(str3.substring(0,1).equals("0") && !str3.substring(1).equals("0")){
            decimal = str3.substring(1);
        }else {
            decimal = str3;
        }

        Log.d("小数部分",decimal);
        if(str1.equals("1")){
            dana = integrate+"."+decimal;
        }else {
            dana = "-"+integrate+"."+decimal;
        }

        Log.d("结果",dana);


        return dana;
    }
}
