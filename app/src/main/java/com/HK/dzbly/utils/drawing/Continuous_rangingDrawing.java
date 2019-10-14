package com.HK.dzbly.utils.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.HK.dzbly.utils.auxiliary.Data_normalization;

import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/11
 * 描述：
 * 修订历史：
 */
public class Continuous_rangingDrawing extends View {
    // 用于存放将要画线的点
    float[] tx = new float[100];
    float[] ty = new float[100];

    // 创建画笔
    private Paint p = new Paint();
    private Paint mPaint;
    private float x1;
    private float y1;
    private int width;
    private int height;
    private int data; //用于记录点的个数

    public Continuous_rangingDrawing(Context context) {
        super(context);
    }

    public Continuous_rangingDrawing(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Continuous_rangingDrawing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        //画图形
        int x = width / 10;
        int y = height / 10;
        initPaint();
        initTriangle(canvas, x, y);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        //优化组件高度
        setMeasuredDimension(width, height);
        Log.d("width", String.valueOf(width));
        Log.d("heitht", String.valueOf(height));
    }

    /**
     * 2.初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        //设置画笔颜色
        mPaint.setColor(Color.BLUE);
        //设置字体大小
        mPaint.setTextSize(40);
        //设置画笔模式
        mPaint.setStyle(Paint.Style.FILL);
        //设置画笔宽度为30px
        mPaint.setStrokeWidth(20f);
    }

    /**
     * 画三角形
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void initTriangle(Canvas canvas, int x, int y) {
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        Log.i("--width--", String.valueOf(width));
        Log.i("--height--", String.valueOf(height));
        Log.i("data", String.valueOf(data));
        Log.i("xSize", String.valueOf(tx.length));
        //实例化路径
        Path path = new Path();
        x1 = width / 2;
        y1 = height / 2;
        canvas.drawPoint(x1, y1, mPaint);
        canvas.drawText("测量点", x1 - 20, y1 + 50, mPaint);//写字
        if (data > 0) {
            for (int i = 1; i < data; i++) {
                path.moveTo(tx[i - 1] * 10 * x, ty[i - 1] * 10 * y);
                path.lineTo(tx[i] * 10 * x, ty[i] * 10 * y);
                path.close(); // 使这些点构成封闭的多边形
                canvas.drawPoint(tx[i - 1] * 10 * x, ty[i - 1] * 10 * y, mPaint);
                canvas.drawPoint(tx[i] * 10 * x, ty[i] * 10 * y, mPaint);
                canvas.drawText(String.valueOf(i), tx[i - 1] * 10 * x, ty[i - 1] * 10 * y + 50, mPaint);//写字
                canvas.drawText(String.valueOf(i + 1), tx[i] * 10 * x, ty[i] * 10 * y + 50, mPaint);//写字
            }
        }
        canvas.drawPath(path, p);
    }

    /**
     * 接收activity得到的wifi传递的数据
     * 刷新界面
     *
     * @param list
     */
    public void setData(List<Map<String, Object>> list) {
        Log.i("dynamicDrawing_list", String.valueOf(list));
        float[] dx = new float[list.size()];
        float[] dy = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            dx[i] = Float.valueOf(list.get(i).get("x").toString());
            dy[i] = Float.valueOf(list.get(i).get("y").toString());
        }
        //将得到的坐标的值设置在1到10之间
        Data_normalization dn = new Data_normalization();
        dx = dn.normalization(dx);
        dy = dn.normalization(dy);
        this.tx = dx;
        this.ty = dy;
        this.data = list.size();
        //进行View的刷新
        invalidate();
    }
}
