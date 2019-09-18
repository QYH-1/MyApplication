package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.HK.dzbly.utils.auxiliary.Screenshot;
import com.HK.dzbly.utils.auxiliary.planar_equation;
import com.HK.dzbly.utils.drawing.dynamicDrawing;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class SectionsurveyActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
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
    private SharedPreferences sp = null;
    private dynamicDrawing dynamicDrawing;  //画图对象
    private Handler drawlineHandler;
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private List<Map<String, Object>> mlist = new ArrayList<>(); //用于保存所有的从wifi接受的点
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/capture";  //文件保存的目录
    private int num = 1; //文件出现次数
    private Context context;
    private Screenshot screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.sectionsurvey);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象

        setCoordinateSet();
        inInt();
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
        dynamicDrawing = findViewById(R.id.drawingView);

        nIncluding_length_length.setChecked(true);
        line_ranging.setOnClickListener(this);
        twopoint_ranging.setOnClickListener(this);
        Measurement.setOnClickListener(this);
        reset.setOnClickListener(this);
        save.setOnClickListener(this);
        Initial_length.setOnCheckedChangeListener(this);

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
                dataList = getPointData(mlist);
                Log.d("dataList", String.valueOf(dataList));
                dynamicDrawing.setData(dataList);
                break;
            case R.id.reset:
                Intent intent2 = new Intent(SectionsurveyActivity.this, SectionsurveyActivity.class);
                startActivity(intent2);
                finish();
            case R.id.Save:
                showDialog();
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
            connectThread = new ConnectThread(socket, myHandler);
            connectThread.start();
        }

    }

    Handler myHandler = new Handler() {
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
            Map<String, Object> mmap = new HashMap<>();//保存wifi传递过来点的具体坐标
            mmap.put("x", x);
            mmap.put("y", y);
            mmap.put("z", z);
            mlist.add(mmap);
        }
    };

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

    //保存数据
    private void showDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        TextView desc1 = view.findViewById(R.id.desc1);

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        desc1.setText(date);
        new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText text = view.findViewById(R.id.name1);
                        String name = text.getText().toString();

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
}