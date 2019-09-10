package com.HK.dzbly.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.HK.dzbly.collector.ActivityCollector;

/* 基类Activity 方便日志中查询界面所处的Activity*/
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity",getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
