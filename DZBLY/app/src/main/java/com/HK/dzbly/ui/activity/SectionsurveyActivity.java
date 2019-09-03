package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/3$
 * 描述：断面测量
 * 修订历史：
 */
public class SectionsurveyActivity extends BaseActivity implements View.OnClickListener {
    private TextView line_ranging,twopoint_ranging,start,stop;//测距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView save; //保存
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.sectionsurvey);

        inInt();
    }
    private void inInt(){
        line_ranging = findViewById(R.id.line_ranging);
        twopoint_ranging = findViewById(R.id.twopoint_ranging);
        nIncluding_length_length = findViewById(R.id.nIncluding_length_length);
        Including_length = findViewById(R.id.Including_length);
        Initial_length = findViewById(R.id.Initial_length);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        save = findViewById(R.id.save);

        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        save.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.line_ranging :
                Intent intent = new Intent(SectionsurveyActivity.this,Laser_rangingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.twopoint_ranging:
                Intent intent1 = new Intent(SectionsurveyActivity.this,Two_pointActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.start:

                break;
            case R.id.stop:

                break;
            case R.id.save:

                break;
        }
    }
}
