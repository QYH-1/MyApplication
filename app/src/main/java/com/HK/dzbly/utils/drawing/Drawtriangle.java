package com.HK.dzbly.utils.drawing;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.HK.dzbly.R;

import java.text.DecimalFormat;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/6$
 * 描述：画直线测距图示
 * 修订历史：
 */
public class Drawtriangle extends View {

    private Paint p = new Paint();//创建画笔
    private float Horizontaldistance;//平距
    private float Verticaldistance; //垂距
    private float Objectdistance; //目标距离
    private float angle;//水平倾角
    private float x;
    private float y;
    private double tan;
    private double angleA;//计算得出的水平倾角的角度
    private int width;
    private int heitht;
    // 缩放后的图片
    private Bitmap bitmap;

    public Drawtriangle(Context context) {
        super(context);
    }

    public Drawtriangle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Drawtriangle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        //计算变化
        Calculating();
        //画三角形
        initTriangle(canvas, x, y);
        //画扇形
        initSector(canvas, x, y);
        //画扇形外的水平倾角
        initScale(canvas);
        //画圆及文字
//        initGarden(canvas, x, y);
        //画目标距离
        initObjectdistance(canvas);
        //画垂距
        initVerticaldistance(canvas, x, y);
        //画水平距离
        initHorizontaldistance(canvas, x, y);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        heitht = width * 10 / 6;
        //优化组件高度
        setMeasuredDimension(width, heitht);
        Log.d("width", String.valueOf(width));
        Log.d("heitht", String.valueOf(heitht));
    }

    private void initHorizontaldistance(Canvas canvas, double x, double y) {
        p.setColor(0xff0000ff);
        Path path = new Path();
        p.setAntiAlias(true);
        if (angle >= 0 && angle <= 80) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 19 / 2);// 起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 19 / 2);   //B
        } else if (angle > 80 && angle < 90) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 25 / 2);// 起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 25 / 2);   //B
        } else if (angle == 0) {
            path.moveTo(width / 10 * 6, (float) (heitht / 14 * 6));// 起点 A
            path.lineTo(width / 10 * 8, heitht / 14 * 6);
        } else if (angle == 90 || angle == -90) {
            path.moveTo(width / 10 * 6, (float) (heitht / 14 * 5));// 起点 A
            path.lineTo(width, heitht / 14 * 5);
        } else if (angle < 0 && angle >= -80) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 9 / 2);// 起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 9 / 2);
        } else if (angle < -80 && angle > -90) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 3 / 2);// 起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 3 / 2);
        }

        p.setTextSize(50);
        canvas.drawTextOnPath("平距  " + Horizontaldistance, path, 0f, 0f, p);
    }

    private void initVerticaldistance(Canvas canvas, float x, float y) {
        p.setColor(0xff0000ff);
        Path path = new Path();
        p.setAntiAlias(true);
        if (angle > 0 && angle <= 80) {
            path.moveTo(width / 10 * 17 / 2, (float) (heitht / 14 * 6 - y / 2));// 起点 A
            path.lineTo(width / 10 * 17 / 2, heitht / 14 * 12);   //B
        } else if (angle > 80 && angle < 90) {
            path.moveTo(width / 10 * 17 / 2, (float) (heitht / 14 * 8 - y / 2));// 起点 A
            path.lineTo(width / 10 * 17 / 2, heitht / 14 * 14);   //B
        } else if (angle == 0) {
            path.moveTo(width / 10 * 6, (float) (heitht / 14 * 5));// 起点 A
            path.lineTo(width, heitht / 14 * 5);
        } else if (angle == 90 || angle == -90) {
            path.moveTo(width / 10 * 9 / 2, (float) (heitht / 14 * 5));// 起点 A
            path.lineTo(width / 10 * 9 / 2, heitht / 14 * 9);
        } else if (angle < 0 && angle >= -80) {
            path.moveTo(width / 10 * 17 / 2, (float) (heitht / 14 * 6));// 起点 A
            path.lineTo(width / 10 * 17 / 2, (float) (heitht / 14 * 10 - y / 2));
        } else if (angle < -80 && angle > -90) {
            path.moveTo(width / 10 * 17 / 2, (float) heitht / 14 * 4);// 起点 A
            path.lineTo(width / 10 * 17 / 2, (float) (heitht / 14 * 8 - y / 2));   //B
        }
        p.setTextSize(50);
        Log.w("画垂距", "垂距");
        canvas.drawTextOnPath("垂距  " + Verticaldistance, path, 0f, 0f, p);
    }

    private void initObjectdistance(Canvas canvas) {
        Path path = new Path();
        p.setAntiAlias(true);
        if (angle > 45 && angle <= 80) {
            path.moveTo((float) (width / 10 * 4), (float) (heitht / 14 * 7 - y / 2));// 起点 A
            path.lineTo((float) (width / 10 * 9), (float) (heitht / 14 * 4 - y * 2));   //B
        } else if (angle > 0 && angle <= 45) {
            path.moveTo((float) (width / 10 * 4), (float) (heitht / 14 * 7 - y / 3));// 起点 A
            path.lineTo((float) (width / 10 * 9), (float) (heitht / 14 * 3 - y));   //B
        } else if (angle > 80 && angle < 90) {
            path.moveTo((float) (width / 10 * 3), (float) (heitht / 14 * 7));// 起点 A
            path.lineTo((float) (width / 10 * 5), (float) (heitht / 14));   //B
        } else if (angle == 0) {
            path.moveTo((float) (width / 10 * 4), (float) (heitht / 14 * 8));// 起点 A
            path.lineTo((float) (width / 10 * 9), (float) (heitht / 14 * 8));
        } else if (angle == 90 || angle == -90) {
            path.moveTo(width / 10 * 3, (float) (heitht / 14 * 4));// 起点 A
            path.lineTo(width / 10 * 3, heitht / 14 * 9);
        } else if (angle < 0 && angle >= -45) {
            path.moveTo((float) (width / 10 * 3), (float) (heitht / 14 * 7 - y / 3));// 起点 A
            path.lineTo((float) (width / 10 * 9), (float) (heitht / 14 * 12 - y - y / 5));   //B
        } else if (angle < -45 && angle >= -80) {
            path.moveTo((float) (width / 10 * 4), (float) (heitht / 14 * 6 - y));// 起点 A
            path.lineTo((float) (width / 10 * 9), (float) (heitht / 14 * 11 - y * 3 / 2));   //B
        } else if (angle < -80 && angle > -90) {
            path.moveTo((float) (width / 10 * 2), (float) (heitht / 14 * 5 - y / 3));// 起点 A
            path.lineTo((float) (width / 10 * 5), (float) (heitht / 14 * 9 - y));   //B
        }
        p.setTextSize(80);
        p.setColor(Color.RED);
        canvas.drawTextOnPath("目标距离  " + Objectdistance, path, 0f, 0f, p);
    }

//    //画圆及文字
//    private void initGarden(Canvas canvas, double x, double y) {
//        // 从资源文件中生成位图bitmap
//        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.point);
//        if (angle > 0 && angle <= 80) {
//            canvas.drawBitmap(bitmap, (float) (width / 10), (float) (heitht / 14 * 17 / 2), p); //开始绘制图片
//        } else if (angle > 80 && angle < 90) {
//            canvas.drawBitmap(bitmap, (float) (width / 10), (float) (heitht / 14 * 23 / 2), p);
//        } else if (angle == 0) {
//            canvas.drawBitmap(bitmap, (float) (width / 10), (float) (heitht / 14 * 17 / 2), p); //开始绘制图片
//        } else if (angle == 90) {
//            canvas.drawBitmap(bitmap, (float) (width / 10 * 17 / 5), (float) (heitht / 14 * 17 / 2), p); //开始绘制图片
//        } else if (angle == -90) {
//            canvas.drawBitmap(bitmap, (float) (width / 10 * 17 / 5), (float) (heitht / 14 * 5 / 2), p);
//        } else if (angle < 0 && angle >= -80) {
//            canvas.drawBitmap(bitmap, (float) (width / 10), (float) (heitht / 14 * 9 / 2), p);
//        } else if (angle < -80 && angle > -90) {
//            canvas.drawBitmap(bitmap, (float) (width / 10), (float) (heitht / 14 * 3 / 2), p);
//        }
//
//        bitmap.recycle();
//
//    }

    //画扇形外的角度
    private void initScale(Canvas canvas) {
        p.setColor(0xff0000ff);
        p.setStyle(Paint.Style.FILL);
        Path path = new Path();
        p.setAntiAlias(true);
        p.setStrokeWidth(0.2f);
        p.setTextSize(40);
        if (angle > 0 && angle <= 80) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 17 / 2 - y / 8);// 此点为多边形的起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 15 / 2 - y / 6);   //B
            // canvas.drawPath(path, p); // 把 Path 也绘制出来，理解起来更方便
        } else if (angle > 80 && angle < 90) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 23 / 2 - y / 8);// 此点为多边形的起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 20 / 2 - y / 6);
        } else if (angle == 0 || angle == 90 || angle == -90) {
            path.moveTo(width / 10 * 6, (float) (heitht / 14 * 6));// 起点 A
            path.lineTo(width, heitht / 14 * 6);
        } else if (angle < 0 && angle >= -80) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 12 / 2 - y / 6);// 此点为多边形的起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 14 / 2 - y / 4);
        } else if (angle < -80 && angle > -90) {
            path.moveTo((float) (width / 10 * 4), heitht / 14 * 6 / 2 - y / 8);// 此点为多边形的起点 A
            path.lineTo(width / 10 * 9, heitht / 14 * 8 / 2 - y / 6);
        }
        p.setTextSize(50);
        canvas.drawTextOnPath("水平倾角  " + angle + "°", path, 10f, 0f, p);
    }

    //画扇形
    private void initSector(Canvas canvas, double x, double y) {
        p.setColor(0xff0000ff);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        int i = (int) (angle - 45);
        if (angle >= 45 && angle <= 80) {
            RectF rectF = new RectF((float) (width / 10), (float) (heitht / 14 * 7), (float) (width / 10 * 4), (float) (heitht / 14 * 11));
            canvas.drawArc(rectF, (float) (-42 - (angle - 50)), 40 + i, false, p);
        } else if (angle > 0 && angle < 45) {
            RectF rectF = new RectF((float) (width / 10 + x), (float) (heitht / 14 * 7), (float) (width / 10 * 4 + x), (float) (heitht / 14 * 11));
            canvas.drawArc(rectF, (float) (-45 - (angle - 45)), 44 + i, false, p);
        } else if (angle > 80 && angle < 90) {
            RectF rectF = new RectF((float) (width / 10), (float) (heitht / 14 * 10), (float) (width / 10 * 4), (float) (heitht / 14 * 14));
            canvas.drawArc(rectF, (float) (-42 - (angle - 54)), 38 + i, false, p);
        } else if (angle < 0 && angle > -45) {
            RectF rectF = new RectF((float) (width / 10), (float) (heitht / 14 * 3), (float) (width / 10 * 4), (float) (heitht / 14 * 7));
            canvas.drawArc(rectF, (float) (-45 - (angle - 45)), 44 + i, false, p);
        } else if (angle <= -45 && angle >= -80) {
            RectF rectF = new RectF((float) (width / 10), (float) (heitht / 14 * 3), (float) (width / 10 * 4), (float) (heitht / 14 * 7));
            canvas.drawArc(rectF, (float) (-44 - (angle - 50)), 38 + i, false, p);
        } else if (angle < -80 && angle > -90) {
            RectF rectF = new RectF((float) (width / 10), 0, (float) (width / 10 * 4), (float) (heitht / 14 * 4));
            canvas.drawArc(rectF, (float) (-44 - (angle - 50)), 38 + i, false, p);
        }

    }

    //画三角形
    private void initTriangle(Canvas canvas, double x, double y) {
        p.setColor(0xff0000ff);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        float xa = (float) (width / 10 * 2);
        float xb = width / 10 * 8;
        float xc = width / 10 * 8;
        Log.d("xa", String.valueOf(xa));
        Log.d("xb", String.valueOf(xb));
        Log.d("xc", String.valueOf(xc));

        Paint mPaint= new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);
        //实例化路径
        Path path = new Path();
        if (angle > 0 && angle <= 80) {
            //屏幕的水平为横坐标，垂直为纵坐标
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 9),mPaint);
            path.moveTo(xa, (float) (heitht / 14 * 9));// 此点为多边形的起点 A
            path.lineTo(xb, (float) (heitht / 14 * 9));   //B
            path.lineTo(xc, (float) (heitht / 14 * 4 - y));  //C
            Log.d("heitht/14*4+y", String.valueOf(heitht / 14 * 4 - y));
        } else if (angle > 80 && angle < 90) {
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 12),mPaint);
            path.moveTo(xa, (float) (heitht / 14 * 12));// 此点为多边形的起点 A
            path.lineTo(xb, (float) (heitht / 14 * 12));   //B
            path.lineTo(xc, (float) (heitht / 14 - y));  //C
        } else if (angle == 0) {
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 9),mPaint);
            path.moveTo(xa, (float) (heitht / 14 * 9));// 此点为多边形的起点 A
            path.lineTo(xb, (float) (heitht / 14 * 9));
        } else if (angle == 90 || angle == -90) {
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 9),mPaint);
            path.moveTo(width / 10 * 4, (float) (heitht / 14 * 9));// 此点为多边形的起点 A
            path.lineTo(width / 10 * 4, (float) (heitht / 14 * 3));
        } else if (angle < 0 && angle >= -80) {
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 5),mPaint);
            path.moveTo(xa, (float) (heitht / 14 * 5));// 此点为多边形的起点 A
            path.lineTo(xb, (float) (heitht / 14 * 5));   //B
            path.lineTo(xc, (float) (heitht / 14 * 10 - y));  //C
        } else if (angle < -80 && angle > -90) {
            canvas.drawPoint(xa-10,(float) (heitht / 14 * 2),mPaint);
            path.moveTo(xa, (float) (heitht / 14 * 2));// 此点为多边形的起点 A
            path.lineTo(xb, (float) (heitht / 14 * 2));   //B
            path.lineTo(xc, (float) (heitht / 14 * 14 - y));  //C
        }

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
    }

    /**
     * 接受fragment传递的数据
     */
    public void setData(float angle, float valance, float haldane) {
        DecimalFormat df = new DecimalFormat("#.00");
        String angle1 = String.valueOf(angle);
        String valance1 = String.valueOf(valance);
        String haldane1 = String.valueOf(haldane);

        this.angle = Float.parseFloat(angle1.substring(0, angle1.indexOf(".") + 2));
        this.Verticaldistance =Math.abs(Float.parseFloat(df.format(Double.parseDouble(valance1)))) ;
        this.Horizontaldistance = Math.abs(Float.parseFloat(df.format(Double.parseDouble(haldane1))));
        float Odistance = (float) Math.sqrt(Verticaldistance * Verticaldistance + Horizontaldistance * Horizontaldistance);
        String ODistance = String.valueOf(Odistance);
        this.Objectdistance = Float.parseFloat(df.format(Double.parseDouble(ODistance)));
        Log.d("DW_angle", String.valueOf(angle));
        Log.d("DW_Verticaldistance", String.valueOf(valance));
        Log.d("DW_Horizontaldistance", String.valueOf(haldane));

        invalidate();//进行View的刷新
    }

    private void Calculating() {
        if ((angle > 45 && angle < 90) || (angle < 0 && angle > -45)) {
            x = 0;
            y = (float) ((Math.abs(Math.abs(angle) - 45)) * 71 / 5);
            Log.d("aaa", String.valueOf(x));

        } else if ((angle > 0 && angle < 45)) {
            x = 0;
            y = (float) -((45 - angle) * 71 / 5);
        } else if (angle < -45 && angle > -90) {
            x = 0;
            y = (float) -(Math.abs(45 - Math.abs(angle)) * 71 / 5);
        }
        Log.d("x", String.valueOf(x));
        Log.d("y ", String.valueOf(y));
    }

}
