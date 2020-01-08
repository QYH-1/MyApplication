package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.Accumulative_rangingFragment;
import com.HK.dzbly.ui.fragment.Continuous_rangingFragment;
import com.HK.dzbly.ui.fragment.LineFragment;
import com.HK.dzbly.ui.fragment.Reduced_range_findingFragment;
import com.HK.dzbly.utils.TestServiceOne;

import java.util.ServiceConfigurationError;


/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：激光测距
 * 修订历史：
 */
public class Laser_rangingActivity extends FragmentActivity implements View.OnClickListener, Continuous_rangingFragment.CallBack, Accumulative_rangingFragment.CallBack, Reduced_range_findingFragment.CallBack {
    private TextView line_ranging; //直线测距
    private TextView twopoint_ranging; //两点测距
    private TextView section_ranging; //断面测距
    private TextView continuous_ranging; //连续测距
    private TextView accumulative_ranging; //累加测距
    private TextView reduced_range_finding; //累减测距
    private LineFragment mlineFragment; //创建直线测距fragment对象
    private Continuous_rangingFragment continuous_rangingFragment; //创建连续测距fragment对象
    private Accumulative_rangingFragment accumulativeFragment; //创建累加测距fragment对象
    private Reduced_range_findingFragment reduced_range_findingFragment;//创建累减测距fragment对象
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
        intentData();
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
                continuous_ranging.setTextColor(Color.WHITE);
                line_ranging.setTextColor(Color.RED);
                accumulative_ranging.setTextColor(Color.WHITE);
                reduced_range_finding.setTextColor(Color.WHITE);
                selectFragment(0);
                break;
            case R.id.twopoint_ranging:
                selectFragment(1);
                break;
            case R.id.section_ranging:
                selectFragment(2);
                break;
            case R.id.Continuous_ranging:
                continuous_ranging.setTextColor(Color.RED);
                line_ranging.setTextColor(Color.WHITE);
                accumulative_ranging.setTextColor(Color.WHITE);
                reduced_range_finding.setTextColor(Color.WHITE);
                selectFragment(3);
                break;
            case R.id.Accumulative_ranging:
                continuous_ranging.setTextColor(Color.WHITE);
                line_ranging.setTextColor(Color.WHITE);
                accumulative_ranging.setTextColor(Color.RED);
                reduced_range_finding.setTextColor(Color.WHITE);
                selectFragment(4);
                break;
            case R.id.Reduced_range_finding:
                continuous_ranging.setTextColor(Color.WHITE);
                line_ranging.setTextColor(Color.WHITE);
                accumulative_ranging.setTextColor(Color.WHITE);
                reduced_range_finding.setTextColor(Color.RED);
                selectFragment(5);
                break;
            default:
                break;
        }
    }

    /**
     * 选择加载的布局
     *
     * @param position
     */
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
            case 3:
                if (continuous_rangingFragment == null) {
                    continuous_rangingFragment = new Continuous_rangingFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options, continuous_rangingFragment).commit();
                break;
            case 4:
                if (accumulativeFragment == null) {
                    accumulativeFragment = new Accumulative_rangingFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options, accumulativeFragment).commit();
                break;
            case 5:
                if (reduced_range_findingFragment == null) {
                    reduced_range_findingFragment = new Reduced_range_findingFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options, reduced_range_findingFragment).commit();
                break;
            default:
                break;
        }
    }

    /**
     * 接受界面跳转的数据，确定fragment的加载
     */
    private void intentData() {
        Intent intent = getIntent();
        int number = intent.getIntExtra("fragmentNumber", 0);
        if (number == 0) {
            continuous_ranging.setTextColor(Color.WHITE);
            line_ranging.setTextColor(Color.RED);
            accumulative_ranging.setTextColor(Color.WHITE);
            reduced_range_finding.setTextColor(Color.WHITE);
            selectFragment(0);
        } else if (number == 3) {
            continuous_ranging.setTextColor(Color.RED);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.WHITE);
            reduced_range_finding.setTextColor(Color.WHITE);
            selectFragment(3);
        } else if (number == 4) {
            continuous_ranging.setTextColor(Color.WHITE);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.RED);
            reduced_range_finding.setTextColor(Color.WHITE);
            selectFragment(4);
        } else {
            continuous_ranging.setTextColor(Color.WHITE);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.WHITE);
            reduced_range_finding.setTextColor(Color.RED);
            selectFragment(5);
        }

    }

    /**
     * 重写Continuous_rangingFragment中的方法,获取fragment传递过来的数据
     *
     * @param content
     */
    @Override
    public void getResult(int content) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        Log.i("content", String.valueOf(content));
        if (content == 3) {
            continuous_ranging.setTextColor(Color.RED);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.WHITE);
            reduced_range_finding.setTextColor(Color.WHITE);
            if (continuous_rangingFragment != null) {
                continuous_rangingFragment = new Continuous_rangingFragment();
            }
            fragmentTransaction.replace(R.id.measurement_options, continuous_rangingFragment).commit();
        } else if (content == 4) {
            continuous_ranging.setTextColor(Color.WHITE);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.RED);
            reduced_range_finding.setTextColor(Color.WHITE);
            if (accumulativeFragment != null) {
                accumulativeFragment = new Accumulative_rangingFragment();
            }
            fragmentTransaction.replace(R.id.measurement_options, accumulativeFragment).commit();
        } else if (content == 5) {
            continuous_ranging.setTextColor(Color.WHITE);
            line_ranging.setTextColor(Color.WHITE);
            accumulative_ranging.setTextColor(Color.WHITE);
            reduced_range_finding.setTextColor(Color.RED);
            if (reduced_range_findingFragment != null) {
                reduced_range_findingFragment = new Reduced_range_findingFragment();
            }
            fragmentTransaction.replace(R.id.measurement_options, reduced_range_findingFragment).commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
