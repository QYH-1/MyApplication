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
 * 描述：
 * 修订历史：
 */
public class Utility_toolsActivity extends BaseActivity {
    private ImageButton calculator, other;
    private TextView calculator1;
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
        //packageInfoManager = new PackageInfoManager();
        list = packageInfoManager.getAllApps(this);
        Log.i("packageInfoManager", "----------------");
        Log.i("packageInfoManager", String.valueOf(list));
    }

    /**
     * 获取界面控件
     */
    private void inint() {
        calculator = findViewById(R.id.calculator);
        other = findViewById(R.id.other);

        calculator1 = findViewById(R.id.calculator1);
    }

    /**
     * 计算器
     */
    private void setCalculator() {
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
                startActivity(intent);
            }
        });
    }

    private void setOther() {
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packageInfoManager.startThridApp(Utility_toolsActivity.this, "com.sankuai.meituan");
            }
        });
    }

}
