package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
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
import com.HK.dzbly.ui.fragment.Two_poointFragment;


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
    private LineFragment mlineFragment; //创建直线测距fragment对象
    private Two_poointFragment mtwo_poointFragment; //创建两点测距fragment对象
    private static boolean enableExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕旋转
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //锁定屏幕
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.laser_ranging);

        inintView();
        selectFragment(0);


    }

    /**
     * 获取控件和点击事件
     */
    private void inintView(){
        line_ranging = findViewById(R.id.line_ranging);
        twopoint_ranging = findViewById(R.id.twopoint_ranging);
        section_ranging = findViewById(R.id.section_ranging);
        //点击事件
        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        section_ranging.setOnClickListener(this);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.line_ranging :
                selectFragment(0);
                break;
            case R.id.twopoint_ranging :
                selectFragment(1);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void selectFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (position){
            case 0:
                if (mlineFragment == null) {
                    mlineFragment =new LineFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options, mlineFragment);
                break;
            case 1:
                if(mtwo_poointFragment ==null){
                    mtwo_poointFragment = new Two_poointFragment();
                }
                fragmentTransaction.replace(R.id.measurement_options,mtwo_poointFragment);
        }
        fragmentTransaction.commit();
    }
}
