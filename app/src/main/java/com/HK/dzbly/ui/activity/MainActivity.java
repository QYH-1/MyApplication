package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.view.BatteryView;
import com.HK.dzbly.utils.view.SensorBatterView;
import com.HK.dzbly.utils.wifi.Concerto;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ServiceConfigurationError;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：主界面
 * 修订历史：
 */
public class MainActivity extends BaseActivity {
    private int intLevel;  //手机当前电量
    private int intScale = 100;   //手机总电量参考值
    private int sensorLevel;  //传感器当前电量
    private int sensorScale = 100;   //传感器总电量参考值
    private TextView battery;
    private TextView sensorBattery;
    private TextView tv_voltameter_value; //手机电量百分比
    private TextView sensorBattery_value; //传感器电量百分比
    private BatteryView batteryView; //绘制手机电池
    private SensorBatterView sensorBatterView; //绘制传感器电池

    private static final int REQUEST_TAKE_PHOTO_CODE = 1;
    private ImageButton prompt, setting, data, camera, tools, dzcsy, shutdown, Other_software, laser_control, Level;
    private TextView setting1, data1, camera1, jgcj, dzcsy1, Other_software1, laser_control1, Level1;
    private String AUTHORITY = "com.cs.dzl.fileProvider";//文件工具位置
    private String TAG = MainActivity.class.getSimpleName();//获得类的简称
    SharedPreferences sp = null;  //存储对象
    public static final String ACTION_REBOOT =
            "android.intent.action.REBOOT";
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";

    private MainServiceConn myServiceConn;
    private TestServiceOne.MyBinder binder = null;
    private byte[] bytes = {69, 73, 87, 32};
    private boolean isConnected = false;

    /* 创建BroadcastReceiver */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*
             * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
             */
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                intLevel = intent.getIntExtra("level", 0);
                intScale = intent.getIntExtra("scale", 100);
                Log.i("", "intLevel = " + intLevel);
                Log.i("", "intScale = " + intScale);
                onBatteryInfoReceiver(intLevel, intScale);
                batteryView.setPower(intLevel);

                //sensorBatterView(sensorLevel, sensorScale);
                //sensorBatterView.setPower(sensorLevel);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象

        batteryView = findViewById(R.id.battery_view);
        sensorBatterView = findViewById(R.id.sensorBattery_view);
        battery = findViewById(R.id.battery);
        sensorBattery = findViewById(R.id.sensorBattery);
        tv_voltameter_value = findViewById(R.id.tv_voltameter_value);
        sensorBattery_value = findViewById(R.id.sensorBattery_value);
        // 注册一个系统 BroadcastReceiver，作为访问电池计+量之用
        // 這個不能直接在AndroidManifest.xml中註冊
        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        inint();//获取所有的控件
        camera(); //相机的处理事件
        setting();//设置的处理事件
        data();//数据管理的处理事件
        setDzcsy();//地质参数仪处理事件
        dataDisplay();//手机关机
        setTools();//激光测距
        setLevel(); //水平仪
        setLaser_control(); //激光控制
        setOther_software(); //实用工具

        final Intent it = new Intent(this, TestServiceOne.class);//绑定服务，连接wifi
        //用intent启动Service并传值
        it.putExtra("data", bytes);
        startService(it);
        //绑定Service
        myServiceConn = new MainServiceConn();
        try {
            bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
        } catch (ServiceConfigurationError s) {
            s.getLocalizedMessage();
        }

        //注意：需要先绑定，才能同步数据
        if (binder != null) {
            binder.setData(bytes);
        }
    }

    class MainServiceConn implements ServiceConnection {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //取得Service里的binder对象
            binder = (TestServiceOne.MyBinder) iBinder;
            //自定义回调
            binder.getService().setDataCallback(new TestServiceOne.DataCallback() {
                //执行回调函数
                @Override
                public void dataChanged(String str) {
                    Log.d("--str--", str);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("str", str);
                    msg.setData(bundle);
                    //发送通知
                    handler.sendMessage(msg);

                }
            });
        }

        /**
         * 接收wifi的数据，并对控件进行设置
         */
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //在handler中更新UI
                String data = msg.getData().getString("str");
                Log.i("----MianActivitydata====:", data);
                if (data.length() == 32) {
                    sensorLevel = Integer.parseInt(data.substring(30, 32));

                    Log.d("----sensorLevel----", String.valueOf(sensorLevel));
                    sensorBatterView(sensorLevel, sensorScale);
                    sensorBatterView.setPower(sensorLevel);
                }
            }
        };

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }

    //手机电量百分比
    public void onBatteryInfoReceiver(int intLevel, int intScale) {
        String data = (intLevel * 100 / intScale) + "%";
        Log.d("电量百分比", data);
        if (intLevel <= 20) {
            battery.setText("请充电...");
        }
        tv_voltameter_value.setText(data);
    }

    //传感器电量百分比
    public void sensorBatterView(int sensorLevel, int sensorScale) {
        String data = (sensorLevel * 100 / sensorScale) + "%";
        Log.d("电量百分比", data);
        if (sensorLevel <= 20) {
            sensorBattery.setText("请充电...");
        }
        sensorBattery_value.setText(data);
    }

    /**
     * 获取界面中的各个按钮
     */
    public void inint() {
        //prompt = findViewById(R.id.prompt);//信号连接指示灯
        setting = findViewById(R.id.setting);//设置
        data = findViewById(R.id.data_management);   //数据管理
        camera = findViewById(R.id.camera);//相机
        tools = findViewById(R.id.tools);//工具
        shutdown = findViewById(R.id.shutdown);//数据展示
        dzcsy = findViewById(R.id.dzcsy);//地质参数仪
        Other_software = findViewById(R.id.Other_software);//其他软件
        laser_control = findViewById(R.id.laser_control);  //激光控制
        Level = findViewById(R.id.Level); //水平仪

        setting1 = findViewById(R.id.setting1); //设置
        data1 = findViewById(R.id.data_management1);//数据管理
        camera1 = findViewById(R.id.camera1);//相机
        jgcj = findViewById(R.id.jgcj);//激光测距
        dzcsy1 = findViewById(R.id.dzcsy1);//地质参数仪
        Other_software1 = findViewById(R.id.Other_software1); //其他软件
        laser_control1 = findViewById(R.id.laser_control1); //激光控制
        Level1 = findViewById(R.id.Level1); //水平仪
    }

    /**
     * 相机的处理事件
     */
    public void camera() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置处理事件
     */
    public void setting() {
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                //Intent intent = new Intent(MainActivity.this,LevelActivity.class);
                //Intent intent = new Intent(MainActivity.this, Laser_controlActivity.class);
                startActivity(intent); // 打开系统设置界面
            }
        });
        setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent); // 打开系统设置界面
            }
        });
    }

    /**
     * 数据管理的事件
     */
    public void data() {
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileMainActivity.class);
                startActivity(intent);
            }
        });
        data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileMainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 关机的处理
     */
    public void dataDisplay() {
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dataDisplay();
                Intent intent = new Intent(MainActivity.this, DatadisplayActivity.class);
                //  Intent intent = new Intent(MainActivity.this,wifi.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 地质参数仪的处理事件
     */
    public void setDzcsy() {
        dzcsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DzlpActivity.class);
                startActivity(intent);
            }
        });
        dzcsy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DzlpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 激光测距
     */
    private void setTools() {
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Laser_rangingActivity.class);
                startActivity(intent);
            }
        });
        jgcj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Laser_rangingActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 其他软件
     */
    private void setOther_software() {
        Other_software.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Utility_toolsActivity.class);
                //Intent intent = new Intent(MainActivity.this, BatteryInfo.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 激光控制
     */
    private void setLaser_control() {
        laser_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LaserControlActivity.class);
                startActivity(intent);
            }
        });
        laser_control1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LaserControlActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setLevel() {
        Level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                startActivity(intent);
            }
        });
        Level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置系统返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 传感器取消注册
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //doDestroy();
    }
}
