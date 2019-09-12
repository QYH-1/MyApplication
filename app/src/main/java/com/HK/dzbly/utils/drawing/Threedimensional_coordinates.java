package com.HK.dzbly.utils.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/14$
 * 描述：三维坐标系两个点
 * 修订历史：
 */
public class Threedimensional_coordinates implements Renderer {
    float x = -0.5f, y = -0.5f, z = -0.5f;
    private float r = 0;
    Handler handler, handler2;
    private Timer timer = new Timer();
    private TimerTask task;
    private float x1 = 0f, y1 = 0f, z1 = 0f, x2 = 0f, y2 = 0f, z2 = 0f;
    private final Context mContext;

    // 定义Open GL ES绘制所需要的Buffer对象
    FloatBuffer lineVerticesBuffer;
    FloatBuffer xyzVerticesBuffer;

    FloatBuffer poointVerticesBuffer;

    ByteBuffer lineFacetsBuffer;
    ByteBuffer xiangliangFacetsBuffer;
    ByteBuffer XFacetsBuffer;
    ByteBuffer YFacetsBuffer;
    ByteBuffer ZFacetsBuffer;
    ByteBuffer AFacetsBuffer;
    ByteBuffer BFacetsBuffer;

    private LabelMaker mLabels;
    private int mString1;
    private Paint mLabelPaint;
    private int mWidth;
    private int mHeight;
    private Typeface mFace;
    float[] lineVertices;
    //位图
    private Bitmap bitmap;


    void updateXYZ() {  //2.1创建各种数组
        //纹理坐标系

        // 定义直线的点                                                       
        lineVertices = new float[]{
                x1, y1, z1,//0
                x2, y2, z2//1

        };
        //画特殊点
        float[] pointFacets = new float[]{
                x1, y1, z1,//0
                x2, y2, z2//1
        };
        //定义XYZ坐标和显示的字
        float xyzVertices[] = new float[]{
                -2.0f, 0f, 0f,//0 x起点，画坐标轴的
                2.0f, 0f, 0f,//1 X轴的终点
                1.8f, 0.1f, 0f,//2 X轴箭头1
                1.8f, -0.1f, 0f,//3 X轴箭头2

                0f, -2.4f, 0f,//4 Y轴起点
                0f, 2.0f, 0f,//5 Y轴终点
                0.1f, 1.8f, 0f,//6 Y轴箭头1
                -0.1f, 1.8f, 0f,//7 Y轴箭头2

                0f, 0f, -2.0f,//8 Z轴起点
                0f, 0f, 2.0f,//9 Z轴终点
                0f, 0.1f, 1.8f,//10 Z轴箭头1
                0f, -0.1f, 1.8f,//11 Z轴箭头2

                2.1f, 0f, 0f,//12 绘制字X
                2.15f, 0.1f, 0f,//13
                2.05f, 0.1f, 0f,//14
                2.05f, -0.1f, 0f,//15
                2.15f, -0.1f, 0f,//16

                0f, 2.3f, 0f,//17 绘制字Y
                0f, 2.1f, 0f,//18
                0.05f, 2.5f, 0f,//19
                -0.05f, 2.5f, 0f,//20

                -0.05f, 0.05f, 2.05f,//21  绘制字Z
                0.05f, 0.05f, 2.05f,//22
                -0.05f, -0.05f, 2.05f,//23
                0.05f, -0.05f, 2.05f,//24

                //刻度X轴刻度
                1.0f, 0f, 0f,//25
                1.0f, 0.1f, 0f,//26
                -1.0f, 0f, 0f,//27
                -1.0f, 0.1f, 0f,//28

                //刻度y轴刻度
                0f, 1.0f, 0f,//29
                -0.1f, 1.0f, 0f,//30
                0f, -1.0f, 0f,//31
                -0.1f, -1.0f, 0f,//32
                //刻度Z轴刻度
                0f, 0f, 1.0f,//33
                0f, 0.1f, 1.0f,//34
                0f, 0f, -1.0f,//35
                0f, 0.1f, -1.0f,//36

                //画第一个点A
                (float) (x1 + 0.1), (float) (y1 + 0.2), z1,//37
                (float) (x1 + 0.08), y1, z1,//38
                (float) (x1 + 0.25), y1, z1,//39
                (float) (x1 + 0.1), (float) (y1 - 0.1), z1,//40
                (float) (x1 + 0.3), (float) (y1 - 0.1), z1,//41
                //画第二个点b
                (float) (x2 + 0.1), (float) (y2 - 0.1), z2,//42

                (float) (x2 + 0.11), (float) (y2 - 0.11), z2,//43
                (float) (x2 + 0.15), (float) (y2 - 0.12), z2,//44
                (float) (x2 + 0.15), (float) (y2 - 0.13), z2,//45
                (float) (x2 + 0.17), (float) (y2 - 0.14), z2,//46
                (float) (x2 + 0.17), (float) (y2 - 0.15), z2,//47
                (float) (x2 + 0.19), (float) (y2 - 0.16), z2,//48
                (float) (x2 + 0.19), (float) (y2 - 0.17), z2,//49
                (float) (x2 + 0.2), (float) (y2 - 0.18), z2,//50
                (float) (x2 + 0.2), (float) (y2 - 0.19), z2,//51

                (float) (x2 + 0.2), (float) (y2 - 0.2), z2,//52

                (float) (x2 + 0.2), (float) (y2 - 0.21), z2,//53
                (float) (x2 + 0.2), (float) (y2 - 0.22), z2,//54
                (float) (x2 + 0.19), (float) (y2 - 0.23), z2,//55
                (float) (x2 + 0.19), (float) (y2 - 0.24), z2,//56
                (float) (x2 + 0.17), (float) (y2 - 0.25), z2,//57
                (float) (x2 + 0.17), (float) (y2 - 0.26), z2,//58
                (float) (x2 + 0.15), (float) (y2 - 0.27), z2,//59
                (float) (x2 + 0.15), (float) (y2 - 0.28), z2,//60
                (float) (x2 + 0.11), (float) (y2 - 0.29), z2,//61

                (float) (x2 + 0.1), (float) (y2 - 0.3), z2,//62

                (float) (x2 + 0.11), (float) (y2 - 0.31), z2,//63
                (float) (x2 + 0.15), (float) (y2 - 0.32), z2,//64
                (float) (x2 + 0.15), (float) (y2 - 0.33), z2,//65
                (float) (x2 + 0.17), (float) (y2 - 0.34), z2,//66
                (float) (x2 + 0.17), (float) (y2 - 0.35), z2,//67
                (float) (x2 + 0.19), (float) (y2 - 0.36), z2,//68
                (float) (x2 + 0.19), (float) (y2 - 0.37), z2,//69
                (float) (x2 + 0.2), (float) (y2 - 0.38), z2,//70
                (float) (x2 + 0.2), (float) (y2 - 0.39), z2,//71

                (float) (x2 + 0.2), (float) (y2 - 0.4), z2,//72

                (float) (x2 + 0.2), (float) (y2 - 0.41), z2,//73
                (float) (x2 + 0.2), (float) (y2 - 0.42), z2,//74
                (float) (x2 + 0.19), (float) (y2 - 0.43), z2,//75
                (float) (x2 + 0.19), (float) (y2 - 0.44), z2,//76
                (float) (x2 + 0.17), (float) (y2 - 0.45), z2,//77
                (float) (x2 + 0.17), (float) (y2 - 0.46), z2,//78
                (float) (x2 + 0.15), (float) (y2 - 0.47), z2,//79
                (float) (x2 + 0.15), (float) (y2 - 0.48), z2,//80
                (float) (x2 + 0.11), (float) (y2 - 0.49), z2,//81

                (float) (x2 + 0.1), (float) (y2 - 0.5), z2//82

        };
        //点画直线
        byte[] lineFacets = new byte[]{
                0, 1
        };
        //向量从原点6指向长方体的0点  
        byte[] xiangliangFacets = new byte[]{
                //2,0,//2,0
                //2,1 //2,1
        };

        //X坐标及其箭头  
        byte[] XFacets = new byte[]{
                //起终点
                0, 1,
                //箭头
                1, 2,
                1, 3,
                //X
                12, 13,
                12, 14,
                12, 15,
                12, 16,
                //X坐标
                25, 26,
                27, 28

        };
        //Y坐标及其箭头  
        byte[] YFacets = new byte[]{
                //起终点
                4, 5,
                //箭头
                5, 6,
                5, 7,
                //字Y
                17, 18,
                17, 19,
                17, 20,
                //Y轴刻度
                29, 30,
                31, 32

        };
        //Z坐标及其箭头  
        byte[] ZFacets = new byte[]{
                //起终点
                8, 9,
                //箭头
                9, 10,
                9, 11,
                //字Z
                21, 22,
                22, 23,
                23, 24,
                //Z轴刻度
                33, 34,
                35, 36
        };
        byte[] AFacets = new byte[]{
                37, 40,
                38, 39,
                37, 41
        };
        byte[] BFacets = new byte[]{
                42, 43,
                43, 44,
                44, 45,
                45, 46,
                42, 46,
                46, 47,
                47, 48,
                48, 49,
                49, 50,
                50, 51,
                51, 52,
                52, 53,
                53, 54,
                54, 55,
                55, 56,
                56, 57,
                57, 58,
                58, 59,
                59, 60,
                60, 61,
                61, 62,
                62, 63,
                63, 64,
                64, 65,
                65, 66,
                66, 67,
                67, 68,
                68, 69,
                69, 70,
                70, 71,
                71, 72,
                72, 73,
                73, 74,
                74, 75,
                75, 76,
                76, 77,
                77, 78,
                78, 79,
                79, 80,
                80, 81,
                81, 82,
                42, 82

        };
        // 将立方体的顶点位置数据数组包装成FloatBuffer;
        lineVerticesBuffer = floatBufferUtil(lineVertices);
        xyzVerticesBuffer = floatBufferUtil(xyzVertices);
        //绘制两个点
        poointVerticesBuffer = floatBufferUtil(pointFacets);
        // 将直线的数组包装成ByteBuffer
        lineFacetsBuffer = ByteBuffer.wrap(lineFacets);
        xiangliangFacetsBuffer = ByteBuffer.wrap(xiangliangFacets);
        XFacetsBuffer = ByteBuffer.wrap(XFacets);
        YFacetsBuffer = ByteBuffer.wrap(YFacets);
        ZFacetsBuffer = ByteBuffer.wrap(ZFacets);
        AFacetsBuffer = ByteBuffer.wrap(AFacets);
        BFacetsBuffer = ByteBuffer.wrap(BFacets);
    }

    //构造函数带了参数，是因为不同类间传递参数，将接受方的handler实例传递过来
    @SuppressLint("HandlerLeak")
    public Threedimensional_coordinates(Handler handler_zjk, Context context) {
        this.mContext = context;
        handler2 = handler_zjk;//实际上handler2就是接收方的实例了，巧妙的转换方法实现不同类间handler传递数据
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 200)//这是接收本类中定时器发送过来的信号用来更新正方体
                    updateXYZ();// 2.2
            }

        };
        //定时器任务中发送了两个信号，给本类中发送了一个，给activity类中发了一个
        task = new TimerTask() {
            public void run() {
                String[] xyz = new String[5];//发送给activity用的
                x = x1;
                y = y1;
                z = z1;
                Log.d("xxxxxx", String.valueOf(x));
                Log.d("yyyyyy", String.valueOf(y));
                Log.d("zzzzzz", String.valueOf(z));
                //设定一下要显示的XYZ位数,不管正负都显示小数点后两位
                if (x > 0)
                    xyz[0] = String.valueOf(x).substring(0, 4);
                else
                    xyz[0] = String.valueOf(x).substring(0, 5);
                if (y > 0)
                    xyz[1] = String.valueOf(y).substring(0, 4);
                else
                    xyz[1] = String.valueOf(y).substring(0, 5);
                if (z > 0)
                    xyz[2] = String.valueOf(z).substring(0, 4);
                else
                    xyz[2] = String.valueOf(z).substring(0, 5);

                if (x2 > 0)
                    xyz[0] = String.valueOf(x2).substring(0, 4);
                else
                    xyz[0] = String.valueOf(x2).substring(0, 5);
                if (y2 > 0)
                    xyz[1] = String.valueOf(y2).substring(0, 4);
                else
                    xyz[1] = String.valueOf(y2).substring(0, 5);
                if (z2 > 0)
                    xyz[2] = String.valueOf(z2).substring(0, 4);
                else
                    xyz[2] = String.valueOf(z2).substring(0, 5);

                Message msg = new Message();
                msg.what = 200;//这是发送给当前类中用来更新立方体的
                handler.sendEmptyMessage(msg.what);

                msg.what = 0x123;//这是发送给activity类中用来更新文本框中XYZ值得
                Bundle bundle = new Bundle();//对数据包装后用MSG发送
                //要发送的是一个字符串数组，第一个参数是指定数据的名，当接收数据时可以用这个名来选择获取哪个数据，很方便，第二个参数是发送的数组
                bundle.putStringArray("xyz", xyz);
                msg.setData(bundle);
                // handler2.sendMessage(msg);
                //handler2指的是接收处的Handler实例，这里因为不是同一类，所以要用构造函数将接受类的handler实例传递过来

            }
        };
        timer.schedule(task, 0, 7000);
    }

    //2.3 实现接口里的三个方法
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // initFontBitmap();
        //关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);
        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0, 0, 0, 0);
        //设置清理屏幕的颜色
        gl.glClearColor(0, 0, 0, 1);
        // 设置阴影平滑模式
        gl.glShadeModel(GL10.GL_SMOOTH);
        Log.d("onSurfaceCreated", "onSurfaceCreated");
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 设置深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);

        //线性滤波
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);//放大时
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);//缩小时

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)//2.3.2
    {
        mWidth = width;
        mHeight = height;
        // 设置3D视窗的大小及位置
        gl.glViewport(0, 0, width, height);
        // 将当前矩阵模式设为投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 初始化单位矩阵
        // gl.glLoadIdentity();
        // 计算透视视窗的宽度、高度比
        float ratio = (float) width / height;
        // 调用此方法设置透视视窗的空间大小。
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

        // 设置观察模型
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    // 绘制图形的方法
    @Override
    public void onDrawFrame(GL10 gl)//2.3.3 
    {
        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // 启用顶点座标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  //2.3.3.1
        // 启用顶点颜色数据
        //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);                   //2.3.3.2
        // 设置当前矩阵模式为模型视图。
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // --------------------绘制直线---------------------
        // 重置当前的模型视图矩阵
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, -0.0f, -3.0f);//移动中心
        // 沿着Y轴旋转
        gl.glRotatef(r, 0f, 0.1f, 0.0f);
        r++;
        // 沿着X轴旋转
        //gl.glRotatef(0f, 0.1f, 0f, 0f);
        gl.glLineWidth(2.0f);
        // 设置顶点的位置数据 因为所有的数据都在次数组中，所以长方体和向量的只要设置这一次就好
        //Log.d("lineVerticesBuffer", String.valueOf(lineVerticesBuffer));
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBuffer); //2.3.3.3
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // 2.3.3.4
        //这里不用二维，用三维的画法，注意是GL_LINES三维中画线             
        gl.glDrawElements(GL10.GL_LINES, lineFacetsBuffer.remaining(),//2.3.3.5
                GL10.GL_UNSIGNED_BYTE, lineFacetsBuffer);

        // --------------------绘制点---------------------
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, poointVerticesBuffer);
        gl.glColor4f(1f, 0f, 0f, 0f);
        gl.glPointSize(10f);
        gl.glDrawArrays(GL10.GL_POINTS, 0, 2);


        // --------------------绘制向量---------------------
        //绘制向量
        gl.glLineWidth(6.0f);//直线宽度 5倍于其他线
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBuffer);//向量
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);//向量
        gl.glDrawElements(GL10.GL_LINES, xiangliangFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, xiangliangFacetsBuffer);//向量

        // --------------------绘制X坐标---------------------
        //绘制x坐标
        gl.glLineWidth(3.0f);//直线宽度
        //设置XYZ的顶点 因为所有XYZ的数据都在次数组中，所以XYZ的只要设置这一次就好
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, xyzVerticesBuffer);
        // 设置顶点的颜色数据
        gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);//X
        gl.glDrawElements(GL10.GL_LINES, XFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, XFacetsBuffer);//X

        // --------------------绘制Y坐标---------------------
        //绘制Y坐标
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBufferY);//Y
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);//Y
        gl.glDrawElements(GL10.GL_LINES, YFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, YFacetsBuffer);//Y
        // --------------------绘制Z坐标---------------------
        //绘制Z坐标
        //无需再设置点了，都是用的上面的数组中的
        // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVerticesBufferZ);//Y
        // 设置顶点的颜色数据
        gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);//z
        gl.glDrawElements(GL10.GL_LINES, ZFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, ZFacetsBuffer);//Z

        // --------------------绘制A点标记---------------------
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);//
        gl.glDrawElements(GL10.GL_LINES, AFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, AFacetsBuffer);

        // --------------------绘制B点标记---------------------
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);//
        gl.glDrawElements(GL10.GL_LINES, BFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, BFacetsBuffer);

        // 绘制结束
        gl.glFinish();//2.3.3.6
        // 禁止顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        timer.cancel();
        // 旋转角度增加1
        //rotate+=1;


    }

    //绘制文字
    private void initText(GL10 gl) {
        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        //消除锯齿
        mLabelPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        // mFace = Typeface.createFromAsset(mContext.getAssets(),"")
        if (mLabels != null) {
            mLabels.shutdown(gl);
        } else {
            mLabels = new LabelMaker(true, 200, 380);
        }
        mLabels.initialize(gl);
        mLabels.beginAdding(gl);
        mLabelPaint.setARGB(255, 0, 0, 0);
        mLabelPaint.setTextSize(15);
        mString1 = mLabels.add(gl, "测试画图", mLabelPaint);
        mLabels.endAdding(gl);
        Log.e("mmmm", "测试画图");
    }

    private void drawText(GL10 gl) {
        //开始绘制
        //Log.d("mWidth", String.valueOf(mWidth));
        //Log.d("mHeight", String.valueOf(mHeight));
        //mLabels = new LabelMaker();
        mLabels.beginDrawing(gl, mWidth, mHeight);
        //mString1 = mLabels.add(gl,"测试画图",mLabelPaint);
        float x = (mWidth - mLabels.getWidth(mString1)) / 2;
        mLabels.draw(gl, x, 2, mString1);

//        x = (mWidth - mLabels.getWidth(mString2))/2;
//        mLabels.draw(gl,x,350-mLabels.getHeight(mString1),mString2);
        mLabels.endDrawing(gl);
    }

    // 定义一个工具方法，将float[]数组转换为OpenGL ES所需的FloatBuffer
    private FloatBuffer floatBufferUtil(float[] arr) {
        FloatBuffer mBuffer = null;
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
        qbb.order(ByteOrder.nativeOrder()); // 设置字节顺序
        mBuffer = qbb.asFloatBuffer(); //转换为Float型缓冲
        mBuffer.put(arr);//向缓冲区中放入顶点坐标数据
        mBuffer.position(0);// 设置缓冲区起始位置

        return mBuffer;
    }

    /**
     * 接收activity传递过来的数据
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public void getData(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
}
