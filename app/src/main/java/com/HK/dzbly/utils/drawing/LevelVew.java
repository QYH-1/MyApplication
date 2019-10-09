package com.HK.dzbly.utils.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.HK.dzbly.R;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/9
 * 描述：
 * 修订历史：
 */
public class LevelVew extends View {
    //圆柱体高的一半
    private float mLimitRadius = 0;
    //圆柱体的底面园的半径
    private float mLiWidth = 0;
    //气泡半径
    private float mBubbleRadius;
    //背景的位图对象
    private Bitmap limitCircle;
    //小球的位图对象
    private Bitmap bubbleBall;
    private Paint paint = new Paint();
    //中心点坐标
    private PointF centerPnt = new PointF();
    //计算后的气泡点
    private PointF bubblePoint;
    private double pitchAngle = -90;
    private double rollAngle = -90;

    public LevelVew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelVew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LevelVew(Context context) {
        super(context);
    }

    private void init(Context context) {
        limitCircle = BitmapFactory.decodeResource(context.getResources(), R.mipmap.cylinder_v);
        bubbleBall = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ball_orientation);
        //取图片的宽、高来决定当前水平仪的宽和高
        mLimitRadius = limitCircle.getHeight() / 2;
        mLiWidth = limitCircle.getWidth() / 2;
        mBubbleRadius = bubbleBall.getWidth() / 2;
        centerPnt.set(mLiWidth - mBubbleRadius, mLimitRadius - mBubbleRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//获取数值
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        //当父控件已经确切的指定了子查看的大小
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + limitCircle.getWidth() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingBottom() + limitCircle.getHeight() + getPaddingTop();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(limitCircle, 0, 0, paint);
        drawBubble(canvas);
    }

    private boolean isCenter(PointF bubblePoint) {
        if (bubblePoint == null) {
            return false;
        }
        //float threshold = 3.0f;
        return Math.abs(bubblePoint.x - centerPnt.x) < 1 && Math.abs(bubblePoint.y - centerPnt.y) < 1;
    }

    private void drawBubble(Canvas canvas) {
        if (bubblePoint != null) {
            canvas.save();
            canvas.translate(bubblePoint.x, bubblePoint.y);
            canvas.drawBitmap(bubbleBall, 0, 0, paint);
            canvas.restore();
        }
    }

    /**
     * 将角度转换为屏幕坐标点。
     * @param rollAngle  横滚角(弧度)
     * @param pitchAngle 俯仰角(弧度)
     * @return
     */
    private PointF convertCoordinate(double rollAngle, double pitchAngle, double radius) {
        //Math.toRadians（）将以弧度测量的角度转换为以度为单位的近似等效角度。
        double scale = radius / Math.toRadians(90);

        //以圆心为原点，使用弧度表示坐标
        double x0 = 0;
        double y0 = -(pitchAngle * scale);

        //使用屏幕坐标表示气泡点
        double x = centerPnt.x - x0;
        double y = centerPnt.y - y0;

        return new PointF((float) x, (float) y);
    }

    /**
     * @param pitchAngle 俯仰角（弧度）
     * @param rollAngle  横滚角(弧度)
     */
    public void setAngle(double rollAngle, double pitchAngle) {
        this.pitchAngle = pitchAngle;
        this.rollAngle = rollAngle;

        //考虑气泡边界不超出限制圆，此处减去气泡的显示半径，做为最终的限制圆半径
        float limitRadius = mLimitRadius - mBubbleRadius;

        bubblePoint = convertCoordinate(rollAngle, pitchAngle, mLimitRadius);

        //坐标超出最大圆，取法向圆上的点
        if (outLimit(bubblePoint, limitRadius)) {
            onCirclePoint(bubblePoint, limitRadius);
        }
        //刷新界面
        invalidate();
    }

    /**
     * 验证气泡点是否超过限制{@link #mLimitRadius}
     * @param bubblePnt
     * @return
     */
    private boolean outLimit(PointF bubblePnt, float limitRadius) {

        float cSqrt = (bubblePnt.x - centerPnt.x) * (bubblePnt.x - centerPnt.x)
                + (centerPnt.y - bubblePnt.y) * (centerPnt.y - bubblePnt.y);

        if (cSqrt - limitRadius * limitRadius > 0) {
            return true;
        }
        return false;
    }

    /**
     * 计算圆心到 bubblePnt点在圆上的交点坐标
     * 即超出圆后的最大圆上坐标
     *
     * @param bubblePnt   气泡点
     * @param limitRadius 限制圆的半径
     * @return
     */
    private PointF onCirclePoint(PointF bubblePnt, double limitRadius) {
        double azimuth = Math.atan2((bubblePnt.y - centerPnt.y), (bubblePnt.x - centerPnt.x));
        azimuth = azimuth < 0 ? 2 * Math.PI + azimuth : azimuth;

        //圆心+半径+角度 求圆上的坐标
        double x1 = centerPnt.x + limitRadius * Math.cos(azimuth);
        double y1 = centerPnt.y + limitRadius * Math.sin(azimuth);

        bubblePnt.set((float) x1, (float) y1);

        return bubblePnt;
    }
}
