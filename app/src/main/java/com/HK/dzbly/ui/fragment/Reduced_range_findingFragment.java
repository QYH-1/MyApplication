package com.HK.dzbly.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.HK.dzbly.utils.drawing.Accumulative_rangingDrawing;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/9
 * 描述：累减测距布局
 * 修订历史：
 */
public class Reduced_range_findingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private View view;
    private TextView continuous_ranging;//获取父容器中的控件
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView reset;//重置
    private TextView lock;//锁定
    private TextView save; //保存
    private TextView total_length;//累减距离
    private TextView current_length;//当前距离
    private TextView detailed_data;//每次测量的详细数据
    private ConnectThread connectThread;//wifi连接
    private Concerto concerto;//wifi的数据处理
    private Socket socket;
    private double totalDistance = 0.00;//测量的总的距离
    private static final String DATABASE_NAME = "cqhk.db"; //数据库名称
    private int num = 1; //文件出现次数
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/data";  //文件保存的目录
    SharedPreferences sp = null;
    private double distance = 0; //获取每次测得的距离
    private int number = 1; //测量的次数
    private String content = null; //所有的测量数据
    private Accumulative_rangingDrawing reduced_range_findingDrawing;//画图对象
    private List<Map<String, Object>> list = new ArrayList<>(); //存储所有有效点
    private List<Map<String, Object>> mList = new ArrayList<>(); //用于保存所有的从wifi接受的点
    private float angle;//水平倾角（俯仰角）
    private double x = 0, y = 0;//由传感器传递过来的数据转换为点的坐标
    private float aAzimuth;//方位角
    private OutputStream outputStream;  //数据输出流
    private DataInputStream inputStream;
    private Send send;
    private ReceiveMsg receiveMsg;
    private Timer timer;
    private double Signal_quality = 200; //测距时信号质量参数

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

    public Reduced_range_findingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reduced_range_finding, container, false);
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
        //定时获取向硬件发送信息，得到最新的数据
        send = new Send();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getWifiData();

                Log.i("Signal_quality", String.valueOf(Signal_quality));
                //不能在子线程中更新UI，所以只能再建立一个主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI
                        if (Signal_quality <150) {
                            Toast.makeText(getActivity(), "当前有磁干扰，信号质量差！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, 0, 2000 * 2);
    }

    /**
     * 获取界面中的控件
     *
     * @param view
     */
    private void initView(View view) {
        reduced_range_findingDrawing = view.findViewById(R.id.drawingView_reduced);
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

    /**
     * 获取wifi传递过来的数据
     */
    private void getWifiData() {
        //在子线程获取连接
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.10.100.254", 8899);
                    inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = socket.getOutputStream();
                    try {
                        Log.i("-------------timer", "timer");
                        byte[] bytes = {69,73,87,0,1};
                        send.sendData(outputStream, bytes);
                        Log.i("receiveMsg", "receiveMsg");
                        receiveMsg = new ReceiveMsg();
                        receiveMsg.receiveMsg(inputStream, myHandler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            Log.d("bundle", String.valueOf(bundle));
            Log.w("是否进行赋值", "执行当当前语句");
            String data = bundle.getString("msg");
            Log.d("LineFragmentwifi_data", data);
            if (data.length() < 24) {
                Toast.makeText(getActivity(), "网络错误！请检查网络连接", Toast.LENGTH_SHORT).show();
            }
            //处理wifi传递过来的数据
            concerto = new Concerto();
            distance = Double.parseDouble((concerto.Dataconversion(data.substring(18,24))));
            aAzimuth = Float.parseFloat(concerto.Dataconversion(data.substring(12, 18)));
            angle = Float.parseFloat(concerto.Dataconversion(data.substring(0, 6)));
            Signal_quality = Double.parseDouble(concerto.Dataconversion(data.substring(24)));
            double a = Math.abs((distance));
            x = (a * Math.cos(angle) * Math.sin(aAzimuth));
            y = (a * Math.sin(angle));
            //保存坐标和距离数据
            Map<String, Object> mMap = new HashMap<>();//保存wifi传递过来点的具体坐标
            mMap.put("x", x);
            mMap.put("y", y);
            mMap.put("distance", distance);
            mList.add(mMap);
            current_length.setText("\t\t\t\t" + (double) mList.get(mList.size() - 1).get("distance") + "米");
        }
    };

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
                number = 1;
                content = null;
                list.clear();
                mList.clear();
                callback.getResult(5);
                Toast.makeText(getActivity(), "重置成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lock:
                //getWifiData();
                //setCoordinateSet();
                //当点击有效值，锁定点时，将接受wifi传递过来的最新数据保存到有效数据list中
                if(mList.size() == 1){
                    list.add( mList.get(0));
                }else {
                    list.add(mList.get(mList.size()-1));
                }

                videoData();
                reduced_range_findingDrawing.setData(list);
                number++;
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
                    mMap.put("x", random.nextInt(10));
                } else {
                    mMap.put("y", random.nextInt(10));
                    mMap.put("distance", 5.0);
                }
            }
            mList.add(mMap);
        }
        Log.i("mlist", String.valueOf(mList));
    }

    private void videoData() {
        Log.i("list.size()", String.valueOf(list.size()));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                totalDistance = (double) list.get(i).get("distance");
                BigDecimal b  =new BigDecimal(totalDistance);
                totalDistance  =  b.setScale(3,  RoundingMode.HALF_UP).doubleValue();
            } else {
                totalDistance = totalDistance - (double) list.get(i).get("distance");
                BigDecimal b  =new BigDecimal(totalDistance);
                totalDistance  =  b.setScale(3,  RoundingMode.HALF_UP).doubleValue();
            }
        }
        Log.d("totalDistance", String.valueOf(totalDistance));
        total_length.setText("\t\t\t\t" + totalDistance + "米");
        //current_length.setText("\t\t\t\t" + (double) mList.get(mList.size() - 1).get("distance") + "米");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                BigDecimal b  =new BigDecimal((Double) mList.get(i).get("distance"));
                double f1  =  b.setScale(3,  RoundingMode.HALF_UP).doubleValue();
                content = "第 1 次测量的距离：" + "\n" + "\t\t\t\t" + f1 + "米" + "\n";
            } else {
                BigDecimal b  =new BigDecimal((Double) list.get(i).get("distance"));
                double f1  =  b.setScale(3,  RoundingMode.HALF_UP).doubleValue();
                content = content + "第 " + (i + 1) + " 次测量的距离：" + "\n" + "\t\t\t\t" + f1  + "米" + "\n";
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

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        desc1.setText(date);
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
                        cv.put("name", name); //名称
                        cv.put("val", number); //测量次数
                        cv.put("rollAngle", content); //详细数据
                        cv.put("elevation", "");
                        cv.put("type", "reduced"); //类型
                        cv.put("result", totalDistance); //最终结果
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
                                        "\t累减距离：" + totalDistance + "米" + "\n" +
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
                                        "\t累减距离：" + totalDistance + "米" + "\n" +
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
        timer.cancel();
    }
}
