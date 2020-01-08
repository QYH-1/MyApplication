package com.HK.dzbly.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.ui.activity.Laser_rangingActivity;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.drawing.Drawtriangle;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.ReceiveMsg;
import com.HK.dzbly.utils.wifi.Send;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ServiceConfigurationError;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/6$
 * 描述：直线测距
 * 修订历史：
 */
public class LineFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private View view;
    private TextView line_ranging;//获取父容器中的控件
    private Drawtriangle drawtriangle;//画图对象
    private float angle;//水平倾角
    private float Verticaldistance; //接收wifi传递的垂距
    private float Horizontaldistance;//接收wifi传递的平距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView reset;//重置
    private TextView lock;//锁定
    private TextView save; //保存
    private ConnectThread connectThread;//wifi连接
    private Concerto concerto;//wifi的数据处理
    private Socket socket;
    private String Objectdistance;//目标距离
    private static final String DATABASE_NAME = "cqhk.db"; //数据库名称
    private int num = 1; //文件出现次数
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/测距数据";  //文件保存的目录
    SharedPreferences sp = null;
    private OutputStream outputStream;  //数据输出流
    private DataInputStream inputStream;
    private Send send;
    private ReceiveMsg receiveMsg;
    private Timer timer;
    private double Signal_quality = 200.0; //测距时信号质量参数
    MyServiceConn myServiceConn;
    TestServiceOne.MyBinder binder;
    private byte[] bytes;
    private String wifiData = "1";
    private long time = 500;
    private Intent it;
    private boolean isConnected  = false;

    public LineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_line_ranging, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());//获取了SharePreferences对象
        Content(view);
        return view;
    }

    private void Content(View view) {
        initView(view);

        it = new Intent(getActivity(), TestServiceOne.class);//绑定服务，连接wifi
        //用intent启动Service并传值
        this.bytes = new byte[]{69, 73, 87, 1};
        it.putExtra("data", bytes);
        it.putExtra("time", time);
        getActivity().startService(it);
        //绑定Service
        myServiceConn = new MyServiceConn();
        try {
            getActivity().bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
            isConnected = getActivity().bindService(it, myServiceConn, Context.BIND_AUTO_CREATE);
        } catch (ServiceConfigurationError s) {
            s.getLocalizedMessage();
        }
        Log.d("bytes", String.valueOf(bytes));
        //注意：需要先绑定，才能同步数据
        if (binder != null) {
            Log.d("同步数据", "同步数据");
            binder.setData(bytes);
        }
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
                if (wifiData.length() == 30) {
                    concerto = new Concerto();
                    String distance = concerto.Dataconversion(wifiData.substring(18, 24));
                    angle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));
                    Signal_quality = Double.parseDouble(concerto.Dataconversion(wifiData.substring(24)));
                    float a = Math.abs(Float.parseFloat(distance));
                    Verticaldistance = (float) (a * Math.sin(angle));
                    Horizontaldistance = (float) (a * Math.cos(angle));
                    DecimalFormat df = new DecimalFormat("#.00");
                    float Odistance = (float) Math.sqrt(Verticaldistance * Verticaldistance + Horizontaldistance * Horizontaldistance);
                    String ODistance = String.valueOf(Odistance);
                    Objectdistance = df.format(Double.parseDouble(ODistance));

                    Log.d("LineFragment_angle", String.valueOf(angle));
                    Log.d("LineVerticaldistance", String.valueOf(Verticaldistance));
                    Log.d("LineHorizontaldistance", String.valueOf(Horizontaldistance));

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("angle", angle);
                    editor.putFloat("Verticaldistance", Verticaldistance);
                    editor.putFloat("Horizontaldistance", Horizontaldistance);
                    editor.putString("Objectdistance", Objectdistance);
                    editor.commit();
                    drawtriangle.setData(angle, Verticaldistance, Horizontaldistance);
                }
            }
        };

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }

    private void initView(View view) {
        line_ranging = getActivity().findViewById(R.id.line_ranging);
        Initial_length = view.findViewById(R.id.Initial_length);
        nIncluding_length_length = view.findViewById(R.id.nIncluding_length_length);
        Including_length = view.findViewById(R.id.Including_length);
        reset = view.findViewById(R.id.reset);
        lock = view.findViewById(R.id.lock);
        drawtriangle = view.findViewById(R.id.drawtriangle);
        save = view.findViewById(R.id.Save);
        //单选按钮，判断是否包含仪器长度
        nIncluding_length_length.setChecked(true);
        Initial_length.setOnCheckedChangeListener(this);
        reset.setOnClickListener(this);
        lock.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    /**
     * 获取wifi传递过来的数据
     */
    private void getWifiData() {
        Log.d("---LineFragmentwifi_data--- =  :", wifiData);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (wifiData.length() == 30) {
                    concerto = new Concerto();
                    String distance = concerto.Dataconversion(wifiData.substring(18, 24));
                    angle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));
                    Signal_quality = Double.parseDouble(concerto.Dataconversion(wifiData.substring(24)));
                    float a = Math.abs(Float.parseFloat(distance));
                    Verticaldistance = (float) (a * Math.sin(angle));
                    Horizontaldistance = (float) (a * Math.cos(angle));
                    DecimalFormat df = new DecimalFormat("#.00");
                    float Odistance = (float) Math.sqrt(Verticaldistance * Verticaldistance + Horizontaldistance * Horizontaldistance);
                    String ODistance = String.valueOf(Odistance);
                    Objectdistance = df.format(Double.parseDouble(ODistance));

                    Log.d("LineFragment_angle", String.valueOf(angle));
                    Log.d("LineVerticaldistance", String.valueOf(Verticaldistance));
                    Log.d("LineHorizontaldistance", String.valueOf(Horizontaldistance));

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("angle", angle);
                    editor.putFloat("Verticaldistance", Verticaldistance);
                    editor.putFloat("Horizontaldistance", Horizontaldistance);
                    editor.putString("Objectdistance", Objectdistance);
                    editor.commit();
                    drawtriangle.setData(angle, Verticaldistance, Horizontaldistance);
                }
            }
        }).start();
    }

    //单选按钮，判断是否包含仪器长度
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == Including_length.getId()) {

        } else if (checkedId == nIncluding_length_length.getId()) {

        }
    }

    //重置界面和数据
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.reset:
                Intent intent = new Intent(getActivity(), Laser_rangingActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "重置成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lock:
                Log.d("改变bytes", "改变bytes");
                this.bytes = new byte[]{69, 73, 87, 0, 0};
                if (binder != null) {
                    binder.setData(bytes);
                }
                break;
            case R.id.Save:
                showDialog();
        }
    }

    private void showDialog() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        TextView desc1 = view.findViewById(R.id.desc1);
        EditText fileName = view.findViewById(R.id.name1);

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        desc1.setText(date);
        fileName.setText(date);
        new AlertDialog.Builder(getActivity())
                .setTitle("系统提示")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText text = view.findViewById(R.id.name1);
                        String name = text.getText().toString();

                        Log.d("name", name);

                        SharedPreferences.Editor editor = sp.edit();
                        String Odistance = sp.getString("Objectdistance", Objectdistance);
                        //创建一个DatabaseHelper对象
                        DBhelper dBhelper = new DBhelper(getActivity(), "cqhk.db");
                        //判断数据库是否存在，不存在就创建数据库（0为不存在，1为已经存在）
                        String sqlNumber = sp.getString("sqlNumber", "0");
                        Log.d("sqlNumber", sqlNumber);
                        if (sqlNumber.equals("0")) {
                            SQLiteDatabase db3 = dBhelper.getWritableDatabase();
                            editor.putString("sqlNumber", "1");
                        } else {
                            editor.putString("sqlNumber", "1");
                            editor.commit();
                        }
                        //将数据存储到数据库中
                        ContentValues cv = new ContentValues();
                        cv.put("name", name);
                        cv.put("val", "");
                        cv.put("rollAngle", "");
                        cv.put("elevation", "");
                        cv.put("type", "line");
                        cv.put("result", "111");
                        dBhelper.Insert(getContext(), "DZBLY", cv);

                        Log.i("----", "---------");
                        Log.d("name", name);
                        String dname = name + ".txt";
                        Log.d("name1", dname);
                        try {
                            //如果文件存在则删除文件
                            File file = new File(path, dname);
                            if (file.exists()) {
                                fileOutputStream = new FileOutputStream(file, true);
                                num = sp.getInt("num" + name, 1) + 1;
                                Log.d("num", String.valueOf(num));
                                editor.putInt("num" + name, num);
                                editor.commit();
                                //file.delete();
                                String str = "\n" +
                                        "\t编  号：" + num + "\n" +
                                        "\t" + Odistance + "米" + "\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();

                            } else {
                                fileOutputStream = new FileOutputStream(file);
                                editor.putInt("num" + name, 1);
                                editor.commit();
                                String str = "\n" +
                                        "\t编  号：" + num + "\n" +
                                        "\t" + Odistance + "米" + "\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
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
    public void onDestroy() {
        super.onDestroy();
        Log.d("改变bytes", "改变bytes");
        this.bytes = new byte[]{69, 73, 87, 0, 0};
        if (binder != null) {
            binder.setData(bytes);
        }
        if(isConnected){
            getActivity().unbindService(myServiceConn);
            isConnected = false;
        }
    }
}
