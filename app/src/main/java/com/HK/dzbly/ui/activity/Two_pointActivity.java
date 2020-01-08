package com.HK.dzbly.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.NonNull;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.utils.TestServiceOne;
import com.HK.dzbly.utils.drawing.FontRenderer;
import com.HK.dzbly.utils.drawing.NoRender;
import com.HK.dzbly.utils.drawing.Threedimensional_coordinates;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/15$
 * 描述：两点测距
 * 修订历史：
 */
public class Two_pointActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Threedimensional_coordinates tdc;
    private Threedimensional_coordinates myRender;
    private FontRenderer fontRenderer;
    private NoRender noRender;
    private Handler drawlineHandler;
    private GLSurfaceView glView;
    private TextView line_ranging, section_ranging;//测距
    private TextView continuous_ranging; //连续测距
    private TextView accumulative_ranging; //累加测距
    private TextView reduced_range_finding; //累减测距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private RadioGroup Initial_length;
    private TextView reSet; //重置按钮
    private TextView lock;  //锁定按钮
    private TextView save; //保存
    private Context context;
    private TextView Adistance;//A点距离
    private TextView Bdistance;//B点距离
    private TextView ABdistance;//AB两点的距离
    private TextView ABVerticalDistance; //两点垂直间距
    private TextView ABHorizontalDistance; //两点水平间距
    private TextView ABHorizontalAngleDistance; //两点水平夹角
    private float aRdistance;  //点到仪器距离
    private float aAzimuth;//方位角
    private float abangle; //俯仰角
    private float bRdistance;  //点到仪器距离
    private float bAzimuth;//方位角
    private float bangle; //俯仰角
    private float Ax = 0.00000001f;
    private float Ay = 0.00000001f;
    private float Az = 0.00000001f;
    private float Bx = 0.00000001f;
    private float By = 0.00000001f;
    private float Bz = 0.00000001f;
    private float abdistance; //AB两点的距离
    private int STATE = 0;//用来判断当前锁定的状态
    private SharedPreferences sp = null;
    private ConnectThread connectThread;//连接wifi,接收数据
    private Concerto concerto;//处理wifi的数据
    private Socket socket;
    private StringBuilder stringBuilder;
    private DBhelper dBhelper;
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/测距数据";  //文件保存的目录
    private int num = 1; //文件出现次数
    private boolean RECORD_VARIABLE = false; //接收标志置（true为正常接收 flase为非正常接收）

    MyServiceConn myServiceConn;
    TestServiceOne.MyBinder binder;
    private byte[] bytes = {69, 73, 87, 1};
    private String wifiData = "1";
    private long time = 1000;
    private Intent it;
    private boolean isConnected = false;
    private boolean addData = false; //判断当前是否接受数据(默认为不接受，当点击按钮后改变状态接受数据)
    private int temp = 0; //用来表明用户当前是需要那个点的坐标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.two_point);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象
        //获取当前的状态
        STATE = sp.getInt("STATE", 0);
        Log.d("------------STATE = ", String.valueOf(STATE));
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
        }, 500);
        inInt();
        setLine_ranging();
        setLock();


        setTdc();
        setDistance();
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
                SharedPreferences.Editor editor = sp.edit();
                Log.i("----data====:", wifiData);
                if (wifiData.length() == 30 && addData) {
                    //获取一组数据
                    concerto = new Concerto();
                    if (temp == 1) {
                        aRdistance = Float.parseFloat(concerto.Dataconversion(wifiData.substring(18)));
                        aAzimuth = Float.parseFloat(concerto.Dataconversion(wifiData.substring(12, 18)));
                        abangle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));


                        editor.putFloat("aRdistance", aRdistance);
                        editor.putFloat("aAzimuth", aAzimuth);
                        editor.putFloat("abangle", abangle);
                        editor.commit();

                        Log.i("获取到A点的值", "获取到A点的值");
                        addData = false;
                        RECORD_VARIABLE = true;
                        setPointData();
                    } else if (temp == 2) {
                        bRdistance = Float.parseFloat(concerto.Dataconversion(wifiData.substring(18)));
                        bAzimuth = Float.parseFloat(concerto.Dataconversion(wifiData.substring(12, 18)));
                        bangle = Float.parseFloat(concerto.Dataconversion(wifiData.substring(0, 6)));

                        editor.putFloat("bRdistance", bRdistance);
                        editor.putFloat("bAzimuth", bAzimuth);
                        editor.putFloat("bangle", bangle);
                        editor.commit();
                        Log.i("获取B点的值", "获取B点的值");
                        addData = false;
                        RECORD_VARIABLE = true;
                        setPointData();
                    } else {
                        setPointData();
                    }
                }

            }
        };

        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }

    private void inInt() {
        glView = (GLSurfaceView) findViewById(R.id.glView);
        line_ranging = findViewById(R.id.line_ranging);
        section_ranging = findViewById(R.id.section_ranging);
        Adistance = findViewById(R.id.Adistance);
        Bdistance = findViewById(R.id.Bdistance);
        ABdistance = findViewById(R.id.ABdistance);
        ABVerticalDistance = findViewById(R.id.ABVerticalDistance);
        ABHorizontalDistance = findViewById(R.id.ABHorizontalDistance);
        ABHorizontalAngleDistance = findViewById(R.id.ABHorizontalAngleDistance);
        nIncluding_length_length = findViewById(R.id.nIncluding_length_length);
        Including_length = findViewById(R.id.Including_length);
        Initial_length = findViewById(R.id.Initial_length);
        continuous_ranging = findViewById(R.id.continuous_measurement);  //连续测距
        accumulative_ranging = findViewById(R.id.Cumulative_measurement);  //累加测距
        reduced_range_finding = findViewById(R.id.Cumulative_reduction_measurement);  //累减测距
        reSet = findViewById(R.id.reset);
        lock = findViewById(R.id.lock);
        save = findViewById(R.id.Save);

        //单选按钮，判断是否包含仪器长度
        nIncluding_length_length.setChecked(true);
        Initial_length.setOnCheckedChangeListener(this);
        line_ranging.setOnClickListener(this);
        section_ranging.setOnClickListener(this);
        continuous_ranging.setOnClickListener(this);
        accumulative_ranging.setOnClickListener(this);
        reduced_range_finding.setOnClickListener(this);

        reSet.setOnClickListener(this);
        save.setOnClickListener(this);

        if (STATE % 3 == 1) {
            lock.setText("有效点B");
        } else if (STATE % 3 == 2) {
            lock.setText("测量完成");
        } else {
            lock.setText("有效点A");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                SharedPreferences.Editor ediTor = sp.edit();
                ediTor.putInt("STATE", 0);
                ediTor.commit();
                Intent intent1 = new Intent(this, Two_pointActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.Save:
                showDialog();
                break;
            case R.id.section_ranging:
                Intent intent2 = new Intent(this, SectionsurveyActivity.class);
                startActivity(intent2);
                finish();
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

    private void setLine_ranging() {
        line_ranging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Two_pointActivity.this, Laser_rangingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setLock() {
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (STATE % 3 == 1) {
                    Log.d("获取B点坐标", "获取B点坐标");
                    addData = true;
                    temp = 2;
                    Log.d("锁定点BSTATE==1", "111111");
                } else if (STATE % 3 == 2) {
                    Log.d("锁定点BSTATE==2", "111111");
                    temp = 3;
                    addData = true;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("STATE", 3);
                    editor.commit();

                    Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
                    startActivity(intent2);
                    finish();
                } else {
                    Log.d("获取A点坐标", "获取A点坐标");
                    addData = true;
                    temp = 1;
                }
            }
        });
    }

    private void setPointData() {
        Log.d("", "执行跳转，刷新图形");
        if (temp == 1 && RECORD_VARIABLE) {
            lock.setClickable(true);
            lock.setEnabled(true);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("STATE", 1);
            editor.commit();
            RECORD_VARIABLE = false;
            Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
            startActivity(intent2);
            finish();
        } else if (temp == 2 && RECORD_VARIABLE) {
            lock.setText("有效点B");
            lock.setClickable(true);
            lock.setEnabled(true);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("STATE", 2);
            editor.commit();
            RECORD_VARIABLE = false;
            Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
            startActivity(intent2);
            finish();
        }
    }

    //单选按钮，判断是否包含仪器长度
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == Including_length.getId()) {

        } else if (checkedId == nIncluding_length_length.getId()) {

        }
    }

    /**
     * 向画三维坐标示意图传递数据
     */
    private void setTdc() {
        Log.i("setTdc", "setTdc");
        if (STATE % 3 == 1) {
            aRdistance = sp.getFloat("aRdistance", 0.0001f);
            aAzimuth = sp.getFloat("aAzimuth", 0.0001f);
            abangle = sp.getFloat("abangle", 0.0001f);

            Ax = (float) (aRdistance * Math.cos(abangle) * Math.sin(aAzimuth)) + 0.0001f;
            Ay = (float) (aRdistance * Math.sin(abangle)) + 0.0001f;
            Az = (float) (aRdistance * Math.cos(abangle) * Math.cos(aAzimuth)) + 0.0001f;
            Log.d("STATE % 3 == 1", Ax + " " + Ay + " " + Az);
            if (Ax != 0 && Ay != 0 && Az != 0) {
                fontRenderer = new FontRenderer(drawlineHandler, this);
                fontRenderer.getData(Ax, Ay, Az);
                Log.i("调用方法", "调用成功");
                glView.setRenderer(fontRenderer);
            }


        } else if (STATE % 3 == 2) {
            aRdistance = sp.getFloat("aRdistance", 0.0001f);
            aAzimuth = sp.getFloat("aAzimuth", 0.0001f);
            abangle = sp.getFloat("abangle", 0.0001f);
            bRdistance = sp.getFloat("bRdistance", 0.0001f);
            bAzimuth = sp.getFloat("bAzimuth", 0.0001f);
            bangle = sp.getFloat("bangle", 0.0001f);

            Ax = (float) (aRdistance * Math.cos(abangle) * Math.sin(aAzimuth)) + 0.0001f;
            Ay = (float) (aRdistance * Math.sin(abangle)) + 0.0001f;
            Az = (float) (aRdistance * Math.cos(abangle) * Math.cos(aAzimuth)) + 0.0001f;
            Bx = (float) (bRdistance * Math.cos(bangle) * Math.sin(bAzimuth)) + 0.0001f;
            By = (float) (bRdistance * Math.sin(bangle)) + 0.0001f;
            Bz = (float) (bRdistance * Math.cos(bangle) * Math.cos(bAzimuth)) + 0.0001f;
            if (Ax != 0 && Ay != 0 && Az != 0 && Bx != 0 && By != 0 && Bz != 0) {
                myRender = new Threedimensional_coordinates(drawlineHandler, this);
                myRender.getData(Ax, Ay, Az, Bx, By, Bz);
                glView.setRenderer(myRender);
            }
        } else {
            noRender = new NoRender(drawlineHandler, this);
            glView.setRenderer(noRender);
        }
    }

    //用于控制数据的显示
    private void setDistance() {
        DecimalFormat df = new DecimalFormat("#.00");
        if (STATE % 3 == 1) {
            String ad = String.valueOf(df.format(sp.getFloat("aRdistance", 0.00f)));
            Adistance.setText("A点距离" + ad + "米");
            Bdistance.setText("B点距离    0.00米");
            ABdistance.setText("AB两点距离0.00米");

        } else if (STATE % 3 == 2) {
            aRdistance = sp.getFloat("aRdistance", 0.00f);
            aAzimuth = sp.getFloat("aAzimuth", 0.00f);
            abangle = sp.getFloat("abangle", 0.00f);
            bRdistance = sp.getFloat("bRdistance", 0.00f);
            bAzimuth = sp.getFloat("bAzimuth", 0.00f);
            bangle = sp.getFloat("bangle", 0.00f);
            //计算两点间的距离
            Ax = (float) (aRdistance * Math.cos(abangle) * Math.sin(aAzimuth));
            Ay = (float) (aRdistance * Math.sin(abangle));
            Az = (float) (aRdistance * Math.cos(abangle) * Math.cos(aAzimuth));
            Bx = (float) (bRdistance * Math.cos(bangle) * Math.sin(bAzimuth));
            By = (float) (bRdistance * Math.sin(bangle));
            Bz = (float) (bRdistance * Math.cos(bangle) * Math.cos(bAzimuth));
            abdistance = (float) Math.abs(Math.sqrt((Ax - Bx) * (Ax - Bx) + (Ay - By) * (Ay - By) + (Az - Bz) * (Az - Bz)));

            String ABdata = "两点的距离  " + abdistance + "米";
            String ad = String.valueOf(df.format(sp.getFloat("aRdistance", 0.00f)));
            String bd = String.valueOf(df.format(sp.getFloat("bRdistance", 0.00f)));
            String Bdata = "B点距离    " + bd + "米";
            String Adata = "A点距离" + ad + "米";
            String ABv = "两点垂直间距" + df.format(Math.abs(Az - Bz)) + "米";
            String ABh = "两点水平间距" + df.format(Math.abs(Ax - Bx)) + "米";
            String ABhad = "两点夹角" + df.format(Math.abs(aAzimuth - bAzimuth)) + "°";

            //存储距离数据
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Adata", Adata);
            editor.putString("Bdata", Bdata);
            editor.putString("ABdata", ABdata);
            editor.commit();
            //通过Txtview显示数据
            Adistance.setText(Adata);
            Bdistance.setText(Bdata);
            ABdistance.setText(ABdata); //两点间距
            ABVerticalDistance.setText(ABv); //两点垂直间距
            ABHorizontalDistance.setText(ABh); //两点水平
            ABHorizontalAngleDistance.setText(ABhad); //两点水平夹角
        } else {
            Adistance.setText("A点距离    0.000米");
            Bdistance.setText("B点距离    0.000米");
            ABdistance.setText("AB两点距离0.000米");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //保存数据
    private void showDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout, null, false);
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

                        SharedPreferences.Editor editor = sp.edit();
                        String Adata = sp.getString("Adata", "0.00米");
                        String Bdata = sp.getString("Bdata", "0.00米");
                        String ABdata = sp.getString("ABdata", "0.00米");
                        Log.d("name", name);

                        DBhelper dbHelper2 = new DBhelper(Two_pointActivity.this, "cqhk.db");
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
                        cv.put("type", "twoPoint");
                        cv.put("result", "111");
                        dbHelper2.Insert(context, "DZBLY", cv);

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
                                        "\t" + Adata + "\t\n" +
                                        "\t" + Bdata + "\t\n" +
                                        "\t" + ABdata + "\t\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();

                            } else {
                                fileOutputStream = new FileOutputStream(file);
                                editor.putInt("num" + name, 1);
                                editor.commit();
                                String str = "\n" +
                                        "\t编  号：" + num + "\n" +
                                        "\t" + Adata + "\t\n" +
                                        "\t" + Bdata + "\t\n" +
                                        "\t" + ABdata + "\t\n" +
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("STATE", 0);
        editor.commit();

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