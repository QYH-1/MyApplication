package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：进入时的欢迎界面
 * 修订历史：
 */
public class WelcomeActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.welcome);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                WelcomeActivity.this.finish();
            }
        };
        timer.schedule(timerTask, 1000 * 2);
    }
}
