package com.HK.dzbly.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2020/3/16
 * 描述：自定义view  传感器电池电量条
 * 修订历史：
 */
public class SensorBatterView extends View {
    private int mPower;

    public SensorBatterView(Context context) {
        super(context);
    }

    public SensorBatterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int battery_left = 0;
        int battery_top = 0;
        int battery_width = 60; //电池电量长度
        int battery_height = 30; //电池电量高度

        int battery_head_width = 8;  //电池头宽度
        int battery_head_height = 8;  //电池头高度

        int battery_inside_margin = 3;

        //先画外框
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        Rect rect = new Rect(battery_left, battery_top,
                battery_left + battery_width, battery_top + battery_height);
        canvas.drawRect(rect, paint);

        float power_percent = mPower / 100.0f;

        Paint paint2 = new Paint(paint);
        if (mPower <= 20) {
            paint2.setColor(Color.RED);
        } else if (mPower <= 50 && mPower > 20) {
            paint2.setColor(Color.BLUE);
        } else if (mPower > 50 && mPower <= 100) {
            paint2.setColor(Color.GREEN);
        }
        paint2.setStyle(Paint.Style.FILL);
        //画电量
        if (power_percent != 0) {
            int p_left = battery_left + battery_inside_margin;
            int p_top = battery_top + battery_inside_margin;
            int p_right = p_left - battery_inside_margin + (int) ((battery_width - battery_inside_margin) * power_percent);
            int p_bottom = p_top + battery_height - battery_inside_margin * 2;
            Rect rect2 = new Rect(p_left, p_top, p_right, p_bottom);
            canvas.drawRect(rect2, paint2);
        }

        //画电池头
        int h_left = battery_left + battery_width;
        int h_top = battery_top + battery_height / 2 - battery_head_height / 2;
        int h_right = h_left + battery_head_width;
        int h_bottom = h_top + battery_head_height;
        Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
        canvas.drawRect(rect3, paint2);
    }

    public void setPower(int power) {
        this.mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        invalidate();
    }
}
