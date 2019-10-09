package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.LineFragment;


/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：激光测距
 * 修订历史：
 */
public class Laser_rangingActivity extends FragmentActivity implements View.OnClickListener {
    private TextView line_ranging; //直线测距
    private TextView twopoint_ranging; //两点测距
    private TextView section_ranging; //断面测距
    private TextView continuous_ranging; //连续测距
    private TextView accumulative_ranging; //累加测距
    private TextView reduced_range_finding; //累减测距
    private LineFragment mlineFragment; //创建直线测距fragment对象
    private static boolean enableExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕旋转
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //锁定屏幕
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.laser_ranging);

        inintView();
        selectFragment(0);

    }

    /**
     * 获取控件和点击事件
     */
    private void inintView() {
        line_ranging = findViewById(R.id.line_ranging);   //直线测距
        twopoint_ranging = findViewById(R.id.twopoint_ranging);  //两点测距
        section_ranging = findViewById(R.id.section_ranging);  //断面测距
        continuous_ranging = findViewById(R.id.Continuous_ranging);  //连续测距
        accumulative_ranging = findViewById(R.id.Accumulative_ranging);  //累加测距
        reduced_range_finding = findViewById(R.id.Reduced_range_finding);  //累减测距
        //点击事件
        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        section_ranging.setOnClickListener(this);
        continuous_ranging.setOnClickListener(this);
        accumulative_ranging.setOnClickListener(this);
        reduced_range_finding.setOnClickListener(this);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.line_ranging:
                selectFragment(0);
                break;
            case R.id.twopoint_ranging:
                selectFragment(1);
                break;
            case R.id.section_ranging:
                selectFragment(2);
            case R.id.Continuous_ranging:
                continuous_ranging.setTextColor(Color.RED);
                selectFragment(3);
            case R.id.Accumulative_ranging:
                selectFragment(4);
            case R.id.Reduced_range_finding:
                selectFragment(5);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void selectFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (position) {
            case 0:
                if (mlineFragment == null) {
                    mlineFragment = new LineFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options, mlineFragment).commit();
                break;
            case 1:
                Intent intent = new Intent(this, Two_pointActivity.class);
                startActivity(intent);
                finish();
                break;
            case 2:
                Intent intent1 = new Intent(Laser_rangingActivity.this, SectionsurveyActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
    }
}
