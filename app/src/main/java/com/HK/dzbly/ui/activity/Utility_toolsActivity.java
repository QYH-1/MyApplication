package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.auxiliary.PackageInfoManager;

import java.util.List;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/11/4
 * 描述：给出其他工具图标，调用其他工具
 * 修订历史：
 */
public class Utility_toolsActivity extends BaseActivity {
    private ImageButton calculator, CAD,wpsImageButton;
    private TextView calculator1,wpsTextView;
    private PackageInfoManager packageInfoManager = new PackageInfoManager(); //调用第三方应用
    private List<PackageInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.utility_tool);

        inint();
        setCalculator();
        setOther();
        setWps();

        list = packageInfoManager.getAllApps(this);
        Log.i("packageInfoManager", "----------------");
        Log.i("packageInfoManager", String.valueOf(list));
    }

    /**
     * 获取界面控件
     */
    private void inint() {
        calculator = findViewById(R.id.calculator);
        CAD = findViewById(R.id.CAD);
        wpsImageButton  =findViewById(R.id.wps);

        calculator1 = findViewById(R.id.calculator1);
    }

    /**
     * 计算器
     */
    private void setCalculator() {
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent();
                    intent.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
//                    intent.setClassName("com.miui.calculator", ".Calculator");
                    startActivity(intent);
                }catch (Exception e){
                }

            }
        });
    }

    private void setOther() {
        CAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageInfoManager.startThridApp(Utility_toolsActivity.this, "com.glodon.drawingexplorer");
            }
        });
    }

    private void setWps(){
        wpsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageInfoManager.startThridApp(Utility_toolsActivity.this, "cn.wps.moffice_eng");
            }
        });
    }

}
