package com.HK.dzbly.utils.drawing;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/3$
 * 描述：动态画断面测量的图形
 * 修订历史：
 */
public class dynamicDrawing extends View {
    private Paint p = new Paint();//创建画笔
    private float x;
    private float y;
    private int width;
    private int heitht;
    // 缩放后的图片
    private Bitmap bitmap;

    public dynamicDrawing(Context context) {
        super(context);
    }

    public dynamicDrawing(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public dynamicDrawing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        //画三角形
        x = (float) width / 10;
        y = (float) heitht / 10;
        initTriangle(canvas, x, y);


    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        heitht = MeasureSpec.getSize(heightMeasureSpec);

        //优化组件高度
        setMeasuredDimension(width, heitht);
        Log.d("width", String.valueOf(width));
        Log.d("heitht", String.valueOf(heitht));
    }

    //画三角形
    private void initTriangle(Canvas canvas, float x, float y) {
        p.setColor(0xff0000ff);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);

        //实例化路径
        Path path = new Path();
        path.moveTo(x, y);// 此点为多边形的起点 A
        path.lineTo(x * 5, y * 5);   //B
        path.lineTo(x * 4, y * 6);  //C

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
    }

    /**
     * 接收activity得到的wifi传递的数据
     *
     * @param coordinateSet
     */
    public void setData(float[][] coordinateSet) {
        //将三维坐标分开存储
        float[] X = new float[coordinateSet.length];
        float[] Y = new float[coordinateSet.length];
        float[] Z = new float[coordinateSet.length];
        for (int i = 0; i < coordinateSet.length; i++) {
            X[i] = coordinateSet[i][1];
            Y[i] = coordinateSet[i][2];
            Z[i] = coordinateSet[i][3];
        }
//        //得到不在一条直线上的三个点
//        for(int i = 0;i<coordinateSet.length;i++){
//            float x = X[i+2];
//            float y = Y[i+2];
//            float z = Z[i+2];
//            Log.i("x,y,z",x+"  "+y+"  "+z);
//            if(!(x-X[1]/X[2]-X[1] == y-Y[1]/Y[2]-Y[1]) || !(y-Y[1]/Y[2]-Y[1]== z - Z[1]/Z[2]-Z[1]) || !(x-X[1]/X[2]-X[1]== z - Z[1]/Z[2]-Z[1])){
//            }
//        }

        invalidate();//进行View的刷新
    }
}
