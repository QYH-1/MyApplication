package com.HK.dzbly.ui.fragment;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.drawing.Continuous_rangingDrawing;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * 创建日期：2019/10/9
 * 描述：连续测距fragment
 * 修订历史：
 */
public class Continuous_rangingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private View view;
    private TextView continuous_ranging;//获取父容器中的控件
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView reset;//重置
    private TextView lock;//锁定
    private TextView save; //保存
    private TextView total_length;//累加距离
    private TextView current_length;//当前距离
    private TextView detailed_data;//每次测量的详细数据
    private ConnectThread connectThread;//wifi连接
    private Concerto concerto;//wifi的数据处理
    private Socket socket;
    private double totalDistance = 00.000;//测量的总的距离
    private static final String DATABASE_NAME = "cqhk.db"; //数据库名称
    private int num = 2; //文件出现次数
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/测距数据";  //文件保存的目录
    SharedPreferences sp = null;
    private double distance = 00.000; //获取每次测得的距离
    private int number = 1; //测量的次数
    private String content = null; //所有的测量数据
    private Continuous_rangingDrawing continuous_rangingDrawing;//画图对象
    private List<Map<String, Object>> list = new ArrayList<>(); //存储所有有效点
    private List<Map<String, Object>> mList = new ArrayList<>(); //用于保存所有的从wifi接受的点
    private List<Map<String, Object>> List = new ArrayList<>(); //存储每两点之间的距离
    private float angle;//水平倾角（俯仰角）
    private double x = 0, y = 0, z = 0;//由传感器传递过来的数据转换为点的坐标
    private float aAzimuth;//方位角
    private OutputStream outputStream;  //数据输出流
    private DataInputStream inputStream;
    private Send send;
    private ReceiveMsg receiveMsg;
    private Timer timer;
    private double Signal_quality = 200; //测距时信号质量参数

    MyServiceConn myServiceConn;
    TestServiceOne.MyBinder binder;
    private byte[] bytes = {69, 73, 87, 1};
    private String wifiData = "1";
    private long time = 500;
    private Intent it;
    private boolean isConnected = false;

    //义用来与外部activity交互，获取到宿主activity
    private Continuous_rangingFragment.CallBack callback;

    //定义一个回调接口
    public interface CallBack {
        void getResult(int content);
    }

    // 当fragmnt被加载到activity的时候会被回调
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Continuous_rangingFragment.CallBack) {
            callback = (Continuous_rangingFragment.CallBack) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements CallBack");
        }
    }

    public Continuous_rangingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.continuous_ranging, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());//获取了SharePreferences对象
        Content(view);
        return view;
    }

    /**
     * fragment中的总体处理
     *
     * @param view
     */
    private void Content(View view) {
        initView(view);
        it = new Intent(getActivity(), TestServiceOne.class);//绑定服务，连接wifi
        //用intent启动Service并传值
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

    /**
     * 获取界面中的控件
     *
     * @param view
     */
    private void initView(View view) {
        continuous_rangingDrawing = view.findViewById(R.id.drawingView);
        continuous_ranging = getActivity().findViewById(R.id.Continuous_ranging);
        Initial_length = view.findViewById(R.id.Initial_length);
        nIncluding_length_length = view.findViewById(R.id.nIncluding_length_length); //不包含仪器长度
        Including_length = view.findViewById(R.id.Including_length);//包含仪器长度
        total_length = view.findViewById(R.id.total_length);
        detailed_data = view.findViewById(R.id.detailed_data);
        current_length = view.findViewById(R.id.current_length);
        reset = view.findViewById(R.id.reset);
        lock = view.findViewById(R.id.lock);
        save = view.findViewById(R.id.Save);
        //单选按钮，判断是否包含仪器长度
        nIncluding_length_length.setChecked(true);
        Initial_length.setOnCheckedChangeListener(this);
        reset.setOnClickListener(this);
        lock.setOnClickListener(this);
        save.setOnClickListener(this);
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
                    //处理wifi传递过来的数据
                    concerto = new Concerto();
                    distance = Double.parseDouble((concerto.Dataconversion(wifiData.substring(18, 24))));
                    aAzimuth = Float.parseFloat(concerto.Dataconversion(wifiData.substring(12, 18)));
                    angle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));
                    Signal_quality = Double.parseDouble(concerto.Dataconversion(wifiData.substring(24)));
                    double a = Math.abs((distance));
                    x = (a * Math.cos(angle) * Math.sin(aAzimuth));
                    y = (a * Math.sin(angle));
                    z = (a * Math.cos(angle) * Math.cos(aAzimuth));

                    //保存坐标和距离数据
                    Map<String, Object> mMap = new HashMap<>();//保存wifi传递过来点的具体坐标
                    mMap.put("x", x);
                    mMap.put("y", y);
                    mMap.put("z", z);
                    mMap.put("distance", distance);
                    mList.add(mMap);
                    current_length.setText("\t\t\t\t" + (double) mList.get(mList.size() - 1).get("distance") + "米");
                }
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

    //重置界面和数据
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.reset:
                number = 2;
                content = null;
                mList.clear();
                list.clear();
                List.clear();
                callback.getResult(3);
                Toast.makeText(getActivity(), "重置成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lock:
                //getWifiData();
                //setCoordinateSet();
                //当点击有效值，锁定点时，将接受wifi传递过来的最新数据保存到有效数据list中
//                list.add( mList.get(mList.size()));
                if (mList.size() == 1) {
                    list.add(mList.get(0));
                } else {
                    list.add(mList.get(mList.size() - 1));
                }
                videoData();
                continuous_rangingDrawing.setData(list);
                number++;
                List.clear();
                break;
            case R.id.Save:
                showDialog();
        }
    }

    /**
     * 用于测试的数据
     */
    private void setCoordinateSet() {
        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            Map<String, Object> mMap = new HashMap<>();//保存wifi传递过来点的具体坐标
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    mMap.put("x", random.nextDouble() * 10);
                } else {
                    mMap.put("y", random.nextDouble() * 10);
                    mMap.put("z", random.nextDouble() * 10);
                    mMap.put("distance", 5.0);
                }
            }
            mList.add(mMap);
        }
        Log.i("mlist", String.valueOf(mList));
    }

    /**
     * 数据的显示
     */
    private void videoData() {
        Log.i("list.size()", String.valueOf(list.size()));
        //连续测距时，当存在两个点后才能给出一段距离
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                totalDistance = 0;
            } else {
                Map<String, Object> map = new HashMap<>();//保存wifi传递过来点的具体坐标
                double ax = (double) list.get(i).get("x");
                double bx = (double) list.get(i - 1).get("x");
                double ay = (double) list.get(i).get("y");
                double by = (double) list.get(i - 1).get("y");
                double az = (double) list.get(i).get("z");
                double bz = (double) list.get(i - 1).get("z");
                //计算两点之间的距离
                double abDistance = Math.abs(Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by) + (az - bz) * (az - bz)));
                map.put("abDistance", abDistance);
                List.add(map);
                totalDistance = totalDistance + abDistance;
                BigDecimal b = new BigDecimal(totalDistance);
                totalDistance = b.setScale(3, RoundingMode.HALF_UP).doubleValue();

            }
        }
        total_length.setText("\t\t\t\t" + totalDistance + "米");
        //current_length.setText("\t\t\t\t" + (double) list.get(list.size() - 1).get("distance") + "米");
        if (List.size() > 0) {
            for (int i = 0; i < List.size(); i++) {
                if (i == 0) {
                    BigDecimal b = new BigDecimal((Double) List.get(i).get("abDistance"));
                    Log.i("-------b", String.valueOf(b));
                    double f1 = b.setScale(3, RoundingMode.HALF_UP).doubleValue();
                    content = "第 " + 1 + " 段测量的距离：" + "\n" + "\t\t\t\t" + f1 + "米" + "\n";
                } else {
                    BigDecimal b = new BigDecimal((Double) List.get(i).get("abDistance"));
                    Log.i("-------b", String.valueOf(b));
                    double f1 = b.setScale(3, RoundingMode.HALF_UP).doubleValue();
                    content = content + "第 " + (i + 1) + " 段测量的距离：" + "\n" + "\t\t\t\t" + f1 + "米" + "\n";
                }

            }
        }
        detailed_data.setText(content);
    }

    /**
     * 以对话框的方式实现数据保存
     */
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
                        String Odistance = sp.getString("Objectdistance", String.valueOf(totalDistance));
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
                        cv.put("val", number);
                        cv.put("rollAngle", content);
                        cv.put("elevation", "");
                        cv.put("type", "continuous");
                        cv.put("result", totalDistance);
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
                                        "\t编    号：" + num + "\n" +
                                        "\t测量次数：" + number + "\n" +
                                        "\t详细数据：" + content + "\n" +
                                        "\t最终距离：" + totalDistance + "米" + "\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();

                            } else {
                                fileOutputStream = new FileOutputStream(file);
                                editor.putInt("num" + name, 1);
                                editor.commit();
                                String str = "\n" +
                                        "\t编    号：" + num + "\n" +
                                        "\t测量次数：" + number + "\n" +
                                        "\t详细数据：" + content + "\n" +
                                        "\t最终距离：" + totalDistance + "米" + "\n" +
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
        if (isConnected) {
            getActivity().unbindService(myServiceConn);
            isConnected = false;
        }
    }
}
