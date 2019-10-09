package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLSurfaceView;
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
import android.widget.*;

import androidx.annotation.NonNull;

import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.utils.drawing.FontRenderer;
import com.HK.dzbly.utils.drawing.NoRender;
import com.HK.dzbly.utils.drawing.Threedimensional_coordinates;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private NetConnection netConnection;//检查wifi是否连接
    private ConnectThread connectThread;//连接wifi,接收数据
    private Concerto concerto;//处理wifi的数据
    private Socket socket;
    private StringBuilder stringBuilder;
    private DBhelper dBhelper;
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/data";  //文件保存的目录
    private int num = 1; //文件出现次数
    private boolean RECORD_VARIABLE = true; //接收标志置（true为正常接收 flase为非正常接收）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.two_point);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象
        STATE = sp.getInt("STATE", 0);

        Log.d("STATE", String.valueOf(STATE));
        netConnection = new NetConnection();
        boolean ts = netConnection.isNetworkConnected(Two_pointActivity.this);
        Log.d("ts", String.valueOf(ts));
        if (netConnection.isNetworkConnected(Two_pointActivity.this)) {
            connectThread = new ConnectThread(socket, handler);
            connectThread.start();
            inInt();
            setLine_ranging();
            setLock();
            setTdc();
            setDistance();
        } else {
            Toast.makeText(Two_pointActivity.this, "WIFI没有连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void inInt() {
        glView = (GLSurfaceView) findViewById(R.id.glView);
        line_ranging = findViewById(R.id.line_ranging);
        section_ranging = findViewById(R.id.section_ranging);
        Adistance = findViewById(R.id.Adistance);
        Bdistance = findViewById(R.id.Bdistance);
        ABdistance = findViewById(R.id.ABdistance);
        nIncluding_length_length = findViewById(R.id.nIncluding_length_length);
        Including_length = findViewById(R.id.Including_length);
        Initial_length = findViewById(R.id.Initial_length);
        reSet = findViewById(R.id.reset);
        lock = findViewById(R.id.lock);
        save = findViewById(R.id.Save);

        //单选按钮，判断是否包含仪器长度
        nIncluding_length_length.setChecked(true);
        Initial_length.setOnCheckedChangeListener(this);
        line_ranging.setOnClickListener(this);
        section_ranging.setOnClickListener(this);

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
                    lock.setClickable(false);
                    lock.setText("锁定点B");
                    Log.d("锁定点BSTATE==1", "111111");
                    Log.i("RECORD_VARIABLE-B", String.valueOf(RECORD_VARIABLE));
                    if (RECORD_VARIABLE) {
                        lock.setClickable(true);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("STATE", STATE + 1);
                        editor.commit();
                        Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
                        startActivity(intent2);
                        finish();
                    } else {
                        lock.setClickable(false);
                    }

                } else if (STATE % 3 == 2) {
                    lock.setText("测量完成");
                    Log.d("锁定点BSTATE==2", "111111");

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("STATE", STATE + 1);
                    editor.commit();
                        connectThread = new ConnectThread(socket, handler);
                        connectThread.start();

                        Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
                        startActivity(intent2);
                        finish();
                } else {
                    lock.setText("锁定点A");
                    //当没有正常接收wifi的数据时
                    Log.i("RECORD_VARIABLE-A", String.valueOf(RECORD_VARIABLE));
                        connectThread = new ConnectThread(socket, myhandler);
                        connectThread.start();
                        if (RECORD_VARIABLE) {
                            lock.setClickable(true);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("STATE", STATE + 1);
                            editor.commit();

                            Intent intent2 = new Intent(Two_pointActivity.this, Two_pointActivity.class);
                            startActivity(intent2);
                            finish();
                        }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            concerto = new Concerto();
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            String data = bundle.getString("msg");
            Log.d("TWO_wifi_data1", data);
            if (data.length() < 24) {
                Toast.makeText(Two_pointActivity.this, "网络错误！请检查网络连接", Toast.LENGTH_SHORT).show();
                RECORD_VARIABLE = false;
            }
            aRdistance = Float.parseFloat(concerto.Dataconversion(data.substring(18)));
            aAzimuth = Float.parseFloat(concerto.Dataconversion(data.substring(12, 18)));
            abangle = Float.parseFloat(concerto.Dataconversion(data.substring(0, 6)));


            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("aRdistance", aRdistance);
            editor.putFloat("aAzimuth", aAzimuth);
            editor.putFloat("abangle", abangle);
            editor.commit();
        }
    };
    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            concerto = new Concerto();
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            String data = bundle.getString("msg");
            Log.d("TWO_wifi_data2", data);
            //当接收wifi的数据不是24位，就提示信息并将接收标志置为false
            if (data.length() < 24) {
                Toast.makeText(Two_pointActivity.this, "网络错误！请检查网络连接", Toast.LENGTH_SHORT).show();
                RECORD_VARIABLE = false;
            }
            bRdistance = Float.parseFloat(concerto.Dataconversion(data.substring(18)));
            bAzimuth = Float.parseFloat(concerto.Dataconversion(data.substring(12, 18)));
            bangle = Float.parseFloat(concerto.Dataconversion(data.substring(0, 6)));

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("bRdistance", bRdistance);
            editor.putFloat("bAzimuth", bAzimuth);
            editor.putFloat("bangle", bangle);
            editor.commit();
        }

    };

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
        Log.i("setTdc","setTdc");
        if (STATE % 3 == 1) {
            aRdistance = sp.getFloat("aRdistance", 0.00001f);
            aAzimuth = sp.getFloat("aAzimuth", 0.00001f);
            abangle = sp.getFloat("abangle", 0.00001f);

            Ax = (float) (aRdistance * Math.cos(abangle) * Math.sin(aAzimuth)) + 0.00000001f;
            Ay = (float) (aRdistance * Math.sin(abangle)) + 0.00000001f;
            Az = (float) (aRdistance * Math.cos(abangle) * Math.cos(aAzimuth)) + 0.00000001f;

            fontRenderer = new FontRenderer(drawlineHandler, this);
            fontRenderer.getData(Ax, Ay, Az);
            Log.i("调用方法","调用成功");
            glView.setRenderer(fontRenderer);

        } else if (STATE % 3 == 2) {
            aRdistance = sp.getFloat("aRdistance", 0.00001f);
            aAzimuth = sp.getFloat("aAzimuth", 0.00001f);
            abangle = sp.getFloat("abangle", 0.00001f);
            bRdistance = sp.getFloat("bRdistance", 0.00001f);
            bAzimuth = sp.getFloat("bAzimuth", 0.00001f);
            bangle = sp.getFloat("bangle", 0.00001f);

            Ax = (float) (aRdistance * Math.cos(abangle) * Math.sin(aAzimuth)) + 0.00000001f;
            Ay = (float) (aRdistance * Math.sin(abangle)) + 0.00000001f;
            Az = (float) (aRdistance * Math.cos(abangle) * Math.cos(aAzimuth)) + 0.00000001f;
            Bx = (float) (bRdistance * Math.cos(bangle) * Math.sin(bAzimuth)) + 0.00000001f;
            By = (float) (bRdistance * Math.sin(bangle)) + 0.00000001f;
            Bz = (float) (bRdistance * Math.cos(bangle) * Math.cos(bAzimuth)) + 0.00000001f;

            myRender = new Threedimensional_coordinates(drawlineHandler, this);
            myRender.getData(Ax, Ay, Az, Bx, By, Bz);
            glView.setRenderer(myRender);
        } else {
            noRender = new NoRender(drawlineHandler, this);
            glView.setRenderer(noRender);
        }
    }

    //用于控制数据的显示
    private void setDistance() {
        DecimalFormat df = new DecimalFormat("#.00");
        if (STATE % 3 == 1) {
            String ad = String.valueOf(sp.getFloat("aRdistance", 0.00f));
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

            String ABdata = "AB两点的距离  " + abdistance + "米";
            String ad = String.valueOf(sp.getFloat("aRdistance", 0.00f));
            String bd = String.valueOf(sp.getFloat("bRdistance", 0.00f));
            ;
            String Bdata = "B点距离    " + bd + "米";
            String Adata = "A点距离" + ad + "米";
            //存储距离数据
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Adata", Adata);
            editor.putString("Bdata", Bdata);
            editor.putString("ABdata", ABdata);
            editor.commit();
            //通过Txtview显示数据
            Adistance.setText(Adata);
            Bdistance.setText(Bdata);
            ABdistance.setText(ABdata);
        } else {
            Adistance.setText("A点距离    0.000米");
            Bdistance.setText("B点距离    0.000米");
            ABdistance.setText("AB两点距离0.000米");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //glView.onResume();
        Log.w("glView1", "glView1");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //glView.onPause();
        Log.w("glView", "glView");
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

}
