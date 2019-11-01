package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：主界面
 * 修订历史：
 */
public class MainActivity extends BaseActivity {
    private static final int REQUEST_TAKE_PHOTO_CODE = 1;
    private ImageButton prompt, setting, data, camera, tools, dzcsy, shutdown;
    private TextView setting1, data1, camera1, jgcj, dzcsy1;
    private String AUTHORITY = "com.cs.dzl.fileProvider";//文件工具位置
    private String TAG = MainActivity.class.getSimpleName();//获得类的简称
    SharedPreferences sp = null;  //存储对象
    public static final String ACTION_REBOOT =
            "android.intent.action.REBOOT";
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象
        inint();//获取所有的控件
        camera(); //相机的处理事件
        setting();//设置的处理事件
        data();//数据管理的处理事件
        setDzcsy();//地质参数仪处理事件
        setShutdown();//手机关机
        setTools();//激光测距
        //saveData();
    }

    /**
     * 获取界面中的各个按钮
     */
    public void inint() {
        prompt = findViewById(R.id.prompt);//信号连接指示灯
        setting = findViewById(R.id.setting);//设置
        data = findViewById(R.id.data_management);//数据管理
        camera = findViewById(R.id.camera);//相机
        tools = findViewById(R.id.tools);//工具
        shutdown = findViewById(R.id.shutdown);//关机
        dzcsy = findViewById(R.id.dzcsy);//地质参数仪

        setting1 = findViewById(R.id.setting1);
        data1 = findViewById(R.id.data_management1);//数据管理
        camera1 = findViewById(R.id.camera1);//相机
        jgcj = findViewById(R.id.jgcj);//工具
        dzcsy1 = findViewById(R.id.dzcsy1);//地质参数仪


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
                //Intent intent = new Intent(Settings.ACTION_SETTINGS);
                //Intent intent = new Intent(MainActivity.this,LevelActivity.class);
                Intent intent = new Intent(MainActivity.this, Laser_controlActivity.class);
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
    public void setShutdown() {
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shutDowm();
            }
        });
    }

    private void shutDowm() {
        Intent intent = new Intent(MainActivity.this, DatadisplayActivity.class);
        //  Intent intent = new Intent(MainActivity.this,wifi.class);
        startActivity(intent);
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

    //遍历文件
    public List<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            s.add(files[i].getName());
        }
        return s;
    }
}
