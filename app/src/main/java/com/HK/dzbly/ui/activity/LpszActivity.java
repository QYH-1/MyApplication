package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：罗盘设置
 * 修订历史：
 */
public class LpszActivity extends BaseActivity{
    private EditText threshold;//阀值
    private EditText declination;//磁偏角
    private Button Confirm_settings;//确认设置
    private Button retreat;//退出
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.lpsz);
        Inint();
        Deal();
    }
    //获取控件
    private void Inint(){
        threshold = findViewById(R.id.threshold);
        declination = findViewById(R.id.declination);
        Confirm_settings = findViewById(R.id.Confirm_settings);
        retreat = findViewById(R.id.retreat);
    }
    //相对应的处理
    private void Deal(){
        //处理确认设置
        Confirm_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LpszActivity.this,DzlpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        //处理退出
        retreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //界面跳转，并且清除上一层栈，这样可以保证一个界面不会重复的出现在栈中.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                Intent intent = new Intent(LpszActivity.this,DzlpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
