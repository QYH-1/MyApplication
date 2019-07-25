package com.HK.dzbly.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：激光测距
 * 修订历史：
 */
public class Laser_rangingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕旋转
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //锁定屏幕
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.laser_ranging);

    }
}
