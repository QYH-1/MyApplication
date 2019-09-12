package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.utils.auxiliary.Data_normalization;
import com.HK.dzbly.utils.auxiliary.planar_equation;
import com.HK.dzbly.utils.drawing.PlaneDrawing;
import com.HK.dzbly.utils.drawing.dynamicDrawing;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/3$
 * 描述：断面测量
 * 修订历史：
 */
public class SectionsurveyActivity extends Activity implements View.OnClickListener {
    private TextView line_ranging, twopoint_ranging, Measurement, reset;//测距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView save; //保存
    private NetConnection netConnection;//检查wifi是否连接
    private ConnectThread connectThread;//连接wifi,接收数据
    private Concerto concerto;//处理wifi的数据
    private Socket socket;
    private StringBuilder stringBuilder;
    private DBhelper dBhelper;
    private float Rdistance;  //点到仪器距离
    private float Azimuth;//方位角
    private float angle; //俯仰角
    private double x, y, z;
    //private double[][] coordinateSet = null; //设置二维数组保存坐标数据
    private SharedPreferences sp = null;
    private dynamicDrawing dynamicDrawing;//画图对象
    private PlaneDrawing planeDrawing;
    private Handler drawlineHandler;
    private GLSurfaceView glView;
    private List<Map<String,Object>> dataList = new ArrayList<>();
    private List<Map<String,Object>> mlist = new ArrayList<>(); //用于保存所有的从wifi接受的点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.sectionsurvey);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象

        setCoordinateSet();
        inInt();
        dataList = getPointData(mlist);
        Log.d("dataList", String.valueOf(dataList));
        planeDrawing =  new PlaneDrawing(drawlineHandler,this);
        planeDrawing.getData(dataList);
        glView.setRenderer(planeDrawing);
    }

    private void inInt() {
        line_ranging = findViewById(R.id.line_ranging);
        twopoint_ranging = findViewById(R.id.twopoint_ranging);
        nIncluding_length_length = findViewById(R.id.nIncluding_length_length);
        Including_length = findViewById(R.id.Including_length);
        Initial_length = findViewById(R.id.Initial_length);
        Measurement = findViewById(R.id.measurement);
        reset = findViewById(R.id.reset);
        save = findViewById(R.id.Save);
        //dynamicDrawing = findViewById(R.id.drawingView);
        glView = (GLSurfaceView) findViewById(R.id.glView1);

        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        Measurement.setOnClickListener(this);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.line_ranging:
                Intent intent = new Intent(SectionsurveyActivity.this, Laser_rangingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.twopoint_ranging:
                Intent intent1 = new Intent(SectionsurveyActivity.this, Two_pointActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.measurement:
                //调用画图
//                dataList = getPointData(mlist);
//                Log.d("dataList", String.valueOf(dataList));
//                planeDrawing =  new PlaneDrawing(drawlineHandler,this);
//                planeDrawing.getData(dataList);
//                glView.setRenderer(planeDrawing);
                break;
            case R.id.reset:

                break;
            case R.id.Save:

                break;
        }
    }

    /**
     * 通过wifi获得传感器传递的一组数据，并将数据存入二维数组中
     */
    private void getPointsData() {
        //获得一次连接得到一个空间点的数据
        if (!netConnection.isNetworkConnected(this)) {
            Toast.makeText(SectionsurveyActivity.this, "请连接WiFi", Toast.LENGTH_LONG).show();
        } else {
            connectThread = new ConnectThread(socket, myhandler);
            connectThread.start();
        }

    }

    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            concerto = new Concerto();
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            String data = bundle.getString("msg");
            Log.d("TWO_wifi_data1", data);
            if (data.length() < 24) {
                Toast.makeText(SectionsurveyActivity.this, "网络错误！请检查网络连接", Toast.LENGTH_SHORT).show();
            }
            //获取一组数据
            Rdistance = Float.parseFloat(concerto.Dataconversion(data.substring(18)));
            Azimuth = Float.parseFloat(concerto.Dataconversion(data.substring(12, 18)));
            angle = Float.parseFloat(concerto.Dataconversion(data.substring(0, 6)));
            //根据距离和角度求出空间点的坐标
            x = Rdistance * Math.cos(angle) * Math.sin(Azimuth);
            y = Rdistance * Math.sin(angle);
            z = Rdistance * Math.cos(angle) * Math.cos(Azimuth);
            //将数据存入到list中
            Map<String,Object> mmap = new HashMap<>();//保存wifi传递过来点的具体坐标
            mmap.put("x",x);
            mmap.put("y",y);
            mmap.put("z",z);
            mlist.add(mmap);
        }
    };

    /**
     * 用于测试的数据,向数组中添加数据
     */
    private void setCoordinateSet() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Map<String,Object> mmap = new HashMap<>();//保存wifi传递过来点的具体坐标
            for (int j = 0; j < 3; j++) {
                if(j ==0){
                    mmap.put("x",random.nextInt(10));
                }else if(j ==1){
                    mmap.put("y",random.nextInt(10));
                }else mmap.put("z",random.nextInt(10));
            }
            mlist.add(mmap);
        }
        Log.i("mlist", String.valueOf(mlist));
    }

    /**
     * 返回所有投影点，用List<Map<String,Object>>形式返回
     * @param data
     * @return
     */
    private List<Map<String,Object>> getPointData(List<Map<String,Object>> data){
        List<Map<String,Object>> list = new ArrayList<>();
        double[] datax = new double[data.size()];
        double[] datay = new double[data.size()];
        double[] dataz = new double[data.size()];
        //得到对应的坐标的数组
        for (int i= 0;i < data.size();i++){
            datax[i] = Double.valueOf(data.get(i).get("x").toString()) ;
            datay[i] = Double.valueOf(data.get(i).get("y").toString());
            dataz[i] = Double.valueOf(data.get(i).get("z").toString());
        }
        //获取计算对象
        planar_equation pe = new planar_equation();
        //得到所有的投影点
        list = pe.Get_equation(datax,datay,dataz);

        Log.d("SecActivity-list", String.valueOf(list));
        return list;
    }

}