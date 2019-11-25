package com.HK.dzbly.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.wifi.ControlData;
import com.HK.dzbly.utils.wifi.Laser_control;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/11/22
 * 描述：
 * 修订历史：
 */
public class LaserControlActivity extends BaseActivity implements View.OnClickListener {
    private Button topPoint,topLine,leftPoint,leftLine,rightPoint,rightLine,bottomPoint,bottomLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.lasercontrol);
        //获取控件
        inInt();
    }
    private void inInt(){
        topPoint = findViewById(R.id.topPoint);
        topLine = findViewById(R.id.topLine);
        leftPoint = findViewById(R.id.leftPoint);
        leftLine = findViewById(R.id.leftLine);
        rightPoint = findViewById(R.id.rightPoint);
        rightLine = findViewById(R.id.rightLine);
        bottomPoint = findViewById(R.id.bottomPoint);
        bottomLine = findViewById(R.id.bottomLine);

        topPoint.setOnClickListener(this);
        topLine.setOnClickListener(this);
        leftPoint.setOnClickListener(this);
        leftLine.setOnClickListener(this);
        rightPoint.setOnClickListener(this);
        rightLine.setOnClickListener(this);
        bottomPoint.setOnClickListener(this);
        bottomLine.setOnClickListener(this);
    }

    /**
     * 重写点击事件
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.topPoint:
                String topPointText = topPoint.getText().toString();
                Log.d("topPointText",topPointText);
                if(topPointText.equals("打开上点")){
                    topPoint.setText("关闭上点");
                    topPoint.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    topPoint.setTextColor(Color.BLACK);
                }else{
                    topPoint.setText("打开上点");
                    topPoint.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    topPoint.setTextColor(Color.WHITE);
                }
                controlLaser(topPointText);
                break;
            case R.id.topLine:
                String topLineText = topLine.getText().toString();
                if(topLineText.equals("打开上线")){
                    topLine.setText("关闭上线");
                    topLine.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    topLine.setTextColor(Color.BLACK);
                }else{
                    topLine.setText("打开上线");
                    topLine.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    topLine.setTextColor(Color.WHITE);
                }
                controlLaser(topLineText);
                break;
            case R.id.leftPoint:
                String leftPointText = leftPoint.getText().toString();
                if(leftPointText.equals("打开左点")){
                    leftPoint.setText("关闭左点");
                    leftPoint.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    leftPoint.setTextColor(Color.BLACK);
                }else{
                    leftPoint.setText("打开左点");
                    leftPoint.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    leftPoint.setTextColor(Color.WHITE);
                }
                controlLaser(leftPointText);
                break;
            case R.id.leftLine:
                String leftLineText = leftLine.getText().toString();
                if(leftLineText.equals("打开左线")){
                    leftLine.setText("关闭左线");
                    leftLine.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    leftLine.setTextColor(Color.BLACK);
                }else{
                    leftLine.setText("打开左线");
                    leftLine.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    leftLine.setTextColor(Color.WHITE);
                }
                controlLaser(leftLineText);
                break;
            case R.id.rightPoint:
                String rightPointText = rightPoint.getText().toString();
                if(rightPointText.equals("打开右点")){
                    rightPoint.setText("关闭右点");
                    rightPoint.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    rightPoint.setTextColor(Color.BLACK);
                }else{
                    rightPoint.setText("打开右点");
                    rightPoint.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    rightPoint.setTextColor(Color.WHITE);
                }
                controlLaser(rightPointText);
                break;
            case R.id.rightLine:
                String rightLineText = rightLine.getText().toString();
                if(rightLineText.equals("打开右线")){
                    rightLine.setText("关闭右线");
                    rightLine.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    rightLine.setTextColor(Color.BLACK);
                }else{
                    rightLine.setText("打开右线");
                    rightLine.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    rightLine.setTextColor(Color.WHITE);
                }
                controlLaser(rightLineText);
                break;
            case R.id.bottomPoint:
                String bottomPointText = bottomPoint.getText().toString();
                if(bottomPointText.equals("打开下点")){
                    bottomPoint.setText("关闭下点");
                    bottomPoint.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    bottomPoint.setTextColor(Color.BLACK);
                }else{
                    bottomPoint.setText("打开下点");
                    bottomPoint.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    bottomPoint.setTextColor(Color.WHITE);
                }
                controlLaser(bottomPointText);
                break;
            case R.id.bottomLine:
                String bottomLineText = bottomLine.getText().toString();
                if(bottomLineText.equals("打开下线")){
                    bottomLine.setText("关闭下线");
                    bottomLine.setBackground(getResources().getDrawable(R.drawable.boder_round));
                    bottomLine.setTextColor(Color.BLACK);
                }else{
                    bottomLine.setText("打开下线");
                    bottomLine.setBackground(getResources().getDrawable(R.drawable.boder_lasercontrol));
                    bottomLine.setTextColor(Color.WHITE);
                }
                controlLaser(bottomLineText);
                break;
        }
    }
    private void controlLaser(String clickText){
        ControlData controlData = new ControlData();
        //利用子线程向硬件发送控制指令
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("laser_control","laser_control");
                Laser_control laser_control = new Laser_control();
                laser_control.laserControl(controlData.setData(clickText));
            }
        }).start();
    }
}
