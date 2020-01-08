package com.HK.dzbly.utils.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.HK.dzbly.R;

import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/3$
 * 描述：动态画断面测量的图形
 * 修订历史：
 */
public class dynamicDrawing extends View {
    private String TAG = "--dynamicDrawing--";
    // 用于存放将要画线的点
    float[] tx = new float[100];
    float[] ty = new float[100];

    // 创建画笔
    private Paint p = new Paint();
    private float x;
    private float y;
    private int width;
    private int height;
    private int data; //用于记录点的个数
    private Bitmap baseBitmap;
    private Canvas canvas;

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
        //画图形
        int x = width / 2;
        int y = height / 2;
        initTriangle(canvas, x, y);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        //优化组件高度
        setMeasuredDimension(width, height);
        Log.d("width", String.valueOf(width));
        Log.d("heitht", String.valueOf(height));
    }

    //画三角形
    private void initTriangle(Canvas canvas, int x, int y) {
        // p.setColor(0xff0000ff);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(5);
        Log.d("width", String.valueOf(x));
        Log.d("height", String.valueOf(y));
        //实例化路径
        Path path = new Path();
        if (data == 0) {
            p.setStrokeWidth(10);
            canvas.drawPoint(x + tx[0] * x, y + ty[0] * y, p);
            Log.w(TAG, "画点");
        } else {
            for (int i = 0; i < data; i++) {
                if (i == 0) {
                    if (tx[i] < 0) {
                        path.moveTo(x + tx[i] * x, y + ty[i] * y);// 此点为多边形的起点 A
                    }

                } else
                    path.lineTo(x + tx[i] * x, y + ty[i] * y);   //B
            }
            path.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path, p);
        }
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
            dx[i] = Float.valueOf(list.get(i).get("xp").toString());
            dy[i] = Float.valueOf(list.get(i).get("yp").toString());
        }
        this.tx = dx;
        this.ty = dy;
        this.data = list.size();
        //进行View的刷新
        invalidate();
    }
}
