package com.HK.dzbly.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

import java.io.File;

public class MainActivity extends BaseActivity{
    private static final int REQUEST_TAKE_PHOTO_CODE = 1;
    private ImageButton prompt,setting,data,camera,tools,dzcsy,shutdown;
    private  String FILE_PATH = "/sdcard/syscamera.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        inint();//获取所有的控件
        camera(); //相机的处理事件
        setting();//设置的处理事件
        data();//数据管理的处理事件
        setDzcsy();//地质参数仪处理事件
    }

    /**
     * 获取界面中的各个按钮
     */
    public void inint(){
        prompt =findViewById(R.id.prompt);//信号连接指示灯
        setting =findViewById(R.id.setting);//设置
        data =findViewById(R.id.data_management);//数据管理
        camera =findViewById(R.id.camera);//相机
        tools =findViewById(R.id.tools);//工具
        dzcsy =findViewById(R.id.dzcsy);//地质参数仪
        shutdown =findViewById(R.id.shutdown);//关机
    }
    /**
     * 相机的处理事件
     */
    public void camera(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置处理事件
     */
    public void setting(){
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);startActivity(intent); // 打开系统设置界面
            }
        });
    }
    /**
     * 数据管理的事件
     */
    public void data(){
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//调用文件浏览器API
                intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
//                Intent intent = new Intent(MainActivity.this,File_mannagementActicity.class);
//                startActivity(intent);
            }
        });
    }
    /**
     * 关机的处理
     */
    public void setShutdown(){
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    /**
     * 地质参数仪的处理事件
     */
    public void setDzcsy(){
        dzcsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DzlpActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 打开指定文件夹
     */
    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
