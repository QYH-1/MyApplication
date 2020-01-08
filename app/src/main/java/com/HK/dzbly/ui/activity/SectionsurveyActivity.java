package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.ui.fragment.LineFragment;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.auxiliary.Calculated_area;
import com.HK.dzbly.utils.auxiliary.Data_normalization;
import com.HK.dzbly.utils.file.Screenshot;
import com.HK.dzbly.utils.auxiliary.planar_equation;
import com.HK.dzbly.utils.drawing.dynamicDrawing;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.ReceiveMsg;
import com.HK.dzbly.utils.wifi.Send;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ServiceConfigurationError;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/3$
 * 描述：断面测量
 * 修订历史：
 */
public class SectionsurveyActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private TextView line_ranging, twopoint_ranging, Measurement, reset;//测距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private TextView continuous_ranging; //连续测距
    private TextView accumulative_ranging; //累加测距
    private TextView reduced_range_finding; //累减测距
    private TextView area; //面积
    private RadioGroup Initial_length;
    private TextView save; //保存
    private ConnectThread connectThread;//连接wifi,接收数据
    private Concerto concerto;//处理wifi的数据
    private Socket socket;
    private StringBuilder stringBuilder;
    private DBhelper dBhelper;
    private float Rdistance;  //点到仪器距离
    private float Azimuth;//方位角
    private float angle; //俯仰角
    private double x, y, z;
    private SharedPreferences sp = null;
    private dynamicDrawing dynamicDrawing;  //画图对象
    private Handler drawlineHandler;
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private List<Map<String, Object>> drawingList = new ArrayList<>();
    private List<Map<String, Object>> mlist = new ArrayList<>(); //用于保存所有的从wifi接受的点
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/capture";  //文件保存的目录
    String path1 = root.getAbsolutePath() + "/CameraDemo" + "/测距数据";  //文件保存的目录
    private int num = 1; //文件出现次数
    private Context context;
    private Screenshot screenshot;
    private OutputStream outputStream;  //数据输出流
    private DataInputStream inputStream;
    private Send send;
    private ReceiveMsg receiveMsg;
    private Calculated_area calculated_area = new Calculated_area(); //计算面积
    private DecimalFormat df = new DecimalFormat("0.###");
    private double Signal_quality = 200; //测距时信号质量参数

    MyServiceConn myServiceConn;
    TestServiceOne.MyBinder binder;
    private byte[] bytes = {69, 73, 87, 1};
    private String wifiData = "1";
    private long time = 1000;
    private Intent it;
    private boolean isConnected = false;
    private boolean addData = false;
    private int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.sectionsurvey);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象

        //setCoordinateSet();
        inInt();

        //定时获取向硬件发送信息，得到最新的数据
        it = new Intent(this, TestServiceOne.class);
        bytes = new byte[]{69, 73, 87, 1};
        //用intent启动Service并传值
        it.putExtra("data", bytes);
        it.putExtra("time", time);
        startService(it);
        //绑定Service
        myServiceConn = new MyServiceConn();
        try {
            bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
            isConnected = bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
            Log.d("isConnected", String.valueOf(isConnected));
        } catch (ServiceConfigurationError s) {
            s.getLocalizedMessage();
        }
        //注意：需要先绑定，才能同步数据
        if (binder != null) {
            System.out.println("同步数据");
            binder.setData(bytes);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                binder.setData(bytes);
            }
        }, 1000);
    }

    private void inInt() {
        line_ranging = findViewById(R.id.line_ranging);
        twopoint_ranging = findViewById(R.id.twopoint_ranging);
        nIncluding_length_length = findViewById(R.id.nIncluding_length_length);
        Including_length = findViewById(R.id.Including_length);
        Initial_length = findViewById(R.id.Initial_length);
        continuous_ranging = findViewById(R.id.continuous_measurement);  //连续测距
        accumulative_ranging = findViewById(R.id.Cumulative_measurement);  //累加测距
        reduced_range_finding = findViewById(R.id.Cumulative_reduction_measurement);  //累减测距
        area = findViewById(R.id.area);
        Measurement = findViewById(R.id.measurement);
        reset = findViewById(R.id.reset);
        save = findViewById(R.id.Save);
        dynamicDrawing = findViewById(R.id.drawingView);

        nIncluding_length_length.setChecked(true);
        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        continuous_ranging.setOnClickListener(this);
        accumulative_ranging.setOnClickListener(this);
        reduced_range_finding.setOnClickListener(this);
        Measurement.setOnClickListener(this);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);
        Initial_length.setOnCheckedChangeListener(this);
    }

    class MyServiceConn implements ServiceConnection {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //取得Service里的binder对象
            binder = (TestServiceOne.MyBinder) iBinder;
            //自定义回调
            binder.getService().setDataCallback(new TestServiceOne.DataCallback() {
                //执行回调函数
                @Override
                public void dataChanged(String str) {
                    Log.d("--str--", str);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("str", str);
                    msg.setData(bundle);
                    //发送通知
                    handler.sendMessage(msg);
                }
            });
        }

        /**
         * 接收wifi的数据，并对控件进行设置
         */
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //在handler中更新UI
                wifiData = msg.getData().getString("str");
                Log.i("----data====:", wifiData);
                if (wifiData.length() == 30 && addData) {
                    //获取一组数据
                    concerto = new Concerto();
                    angle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));
                    Azimuth = Float.parseFloat(concerto.Dataconversion(wifiData.substring(12, 18)));
                    Rdistance = Float.parseFloat(concerto.Dataconversion(wifiData.substring(18, 24)));
                    Signal_quality = Double.parseDouble(concerto.Dataconversion(wifiData.substring(24)));
                    //根据距离和角度求出空间点的坐标
                    x = Double.parseDouble(df.format(Rdistance * Math.cos(angle) * Math.sin(Azimuth)));
                    y = Double.parseDouble(df.format(Rdistance * Math.sin(angle)));
                    z = Double.parseDouble(df.format(Rdistance * Math.cos(angle) * Math.cos(Azimuth)));
                    //将数据存入到list中
                    Map<String, Object> mmap = new HashMap<>();//保存wifi传递过来点的具体坐标
                    mmap.put("x", x);
                    mmap.put("y", y);
                    mmap.put("z", z);
                    mlist.add(mmap);
                    Log.d("将数据存入到list中", String.valueOf(mlist));
                    temp++;
                    addData = false;
                }
                Log.d("temp", String.valueOf(temp));

            }
        };

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }

    //单选按钮，判断是否包含仪器长度
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == Including_length.getId()) {

        } else if (checkedId == nIncluding_length_length.getId()) {

        }
    }

    //点击事件监听
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
                addData = true;
                if (mlist.size() > 0) {
                    Log.d("------mlist------", String.valueOf(mlist));
                    dataList = getPointData(mlist);
                    Log.d("dataList", String.valueOf(dataList));
                    drawingList = drawingData(dataList);
                    Log.d("dataList", String.valueOf(drawingList));
                    dynamicDrawing.setData(drawingList);
                    // area.setText("所测的面积:" + df.format(calculated_area.area(dataList)) + "平方米");
                    //SharedPreferences.Editor editor = sp.edit();
                    //editor.putString("area", df.format(calculated_area.area(dataList)) + "平方米");
                    // editor.commit();
                }
                break;
            case R.id.reset:
                Intent intent2 = new Intent(SectionsurveyActivity.this, SectionsurveyActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.Save:
                showDialog();
                break;
            case R.id.continuous_measurement:
                Intent intent3 = new Intent(this, Laser_rangingActivity.class);
                intent3.putExtra("fragmentNumber", 3);
                startActivity(intent3);
                finish();
                break;
            case R.id.Cumulative_measurement:
                Intent intent4 = new Intent(this, Laser_rangingActivity.class);
                intent4.putExtra("fragmentNumber", 4);
                startActivity(intent4);
                finish();
                break;
            case R.id.Cumulative_reduction_measurement:
                Intent intent5 = new Intent(this, Laser_rangingActivity.class);
                intent5.putExtra("fragmentNumber", 5);
                startActivity(intent5);
                finish();
                break;
        }
    }

    /**
     * 用于测试的数据
     */
    private void setCoordinateSet() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mMap = new HashMap<>();//保存wifi传递过来点的具体坐标
            for (int j = 0; j < 3; j++) {
                if (j == 0) {
                    mMap.put("x", random.nextInt(10));
                } else if (j == 1) {
                    mMap.put("y", random.nextInt(10));
                } else mMap.put("z", random.nextInt(10));
            }
            mlist.add(mMap);
        }
        Log.i("mlist", String.valueOf(mlist));
    }

    /**
     * 返回投影点的凸包点，用List<Map<String,Object>>形式返回
     *
     * @param data
     * @return
     */
    private List<Map<String, Object>> getPointData(List<Map<String, Object>> data) {
        List<Map<String, Object>> list;
        double[] datax = new double[data.size()];
        double[] datay = new double[data.size()];
        double[] dataz = new double[data.size()];
        //得到对应的坐标的数组
        for (int i = 0; i < data.size(); i++) {
            datax[i] = Double.valueOf(data.get(i).get("x").toString());
            datay[i] = Double.valueOf(data.get(i).get("y").toString());
            dataz[i] = Double.valueOf(data.get(i).get("z").toString());
        }
        //获取计算对象
        planar_equation pe = new planar_equation();
        //得到所有的投影点
        list = pe.Get_equation(datax, datay, dataz);

        Log.d("SecActivity-list", String.valueOf(list));
        return list;
    }

    private List<Map<String, Object>> drawingData(List<Map<String, Object>> list) {
        //定义数组接受凸包算法得到的坐标的集合
        List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
        double[] detox = new double[list.size()]; //x
        double[] decoy = new double[list.size()]; //y
        double[] deco = new double[list.size()]; //z
        for (int i = 0; i < list.size(); i++) {
            detox[i] = Double.valueOf(list.get(i).get("xp").toString());
            decoy[i] = Double.valueOf(list.get(i).get("yp").toString());
            deco[i] = Double.valueOf(list.get(i).get("zp").toString());
        }
        //将得到的凸包坐标进行归一化
        Data_normalization dn = new Data_normalization();
        Map<String, double[]> mapData = new HashMap<>();
        mapData = dn.normalization(detox,decoy);
        double[] dxTemp = mapData.get("pX");
        double[] dyTemp = mapData.get("pY");
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> pMap = new HashMap<String, Object>();
            //将数据存储在list中
            pMap.put("xp", dxTemp[i]);
            pMap.put("yp", dyTemp[i]);
            pMap.put("zp", deco[i]);
            datalist.add(pMap);
        }
        return datalist;
    }

    //保存数据
    private void showDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layoutjpg, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        TextView desc1 = view.findViewById(R.id.desc1);
        EditText fileName = view.findViewById(R.id.name1);

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        desc1.setText(date);
        fileName.setText(date);
        new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText text = view.findViewById(R.id.name1);
                        String name = text.getText().toString();
                        String area = sp.getString("area", "0.00平方米");
                        try {
                            // 通过bitmap保存当前截图
                            screenshot = new Screenshot();
                            Bitmap bd = screenshot.myShot(SectionsurveyActivity.this);
                            screenshot.saveToSD(bd, path, name);
                            //将数据存入数据库
                            DBhelper dbHelper = new DBhelper(SectionsurveyActivity.this, "cqhk.db");
                            SharedPreferences.Editor editor = sp.edit();
                            //判断数据库是否存在，不存在就创建数据库（0为不存在，1为已经存在）
                            String sqlNumber = sp.getString("sqlNumber", "0");
                            Log.d("sqlNumber", sqlNumber);
                            if (sqlNumber.equals("0")) {
                                SQLiteDatabase db3 = dbHelper.getWritableDatabase();
                                editor.putString("sqlNumber", "1");
                            } else {
                                editor.putString("sqlNumber", "1");
                                editor.commit();
                            }
                            if (!dbHelper.IsTableExist("File")) {
                                dbHelper.CreateTable(SectionsurveyActivity.this, "File");
                            }
                            ContentValues cv = new ContentValues();
                            cv.put("name", name + ".jpg");
                            cv.put("type", "jpg");
                            dbHelper.Insert(SectionsurveyActivity.this, "File", cv);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("改变bytes", "改变bytes");
        this.bytes = new byte[]{69, 73, 87, 0, 0};
        if (binder != null) {
            binder.setData(bytes);
        }
        if (isConnected) {
            unbindService(myServiceConn);
            isConnected = false;
        }
    }
}