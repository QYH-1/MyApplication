package com.HK.dzbly.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.activity.Laser_rangingActivity;
import com.HK.dzbly.utils.drawing.Drawtriangle;
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
 * 创建日期：2019/8/6$
 * 描述：
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
    private TextView reset;//重置
    private TextView lock;//锁定
    private TextView save; //保存
    private RadioGroup Initial_length;
    private ConnectThread connectThread;//wifi连接
    private NetConnection netConnection;//wifi连接检查
    private Concerto concerto;//wifi的数据处理
    private Socket socket;
    SharedPreferences sp = null;
    private String Objectdistance;//目标距离
    private int num = 1; //文件出现次数
    FileOutputStream fileOutputStream = null; //文件输入流
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath()+"/CameraDemo"+"/data";  //文件保存的目录


    public LineFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_line_ranging,container,false);
       sp = PreferenceManager.getDefaultSharedPreferences(getActivity());//获取了SharePreferences对象
        Content(view);
       return view;
    }
    private void Content(View view){
        initView(view);
    }
    private void initView(View view){
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
       // line_ranging.setTextColor(getActivity().getResources().getColor(R.color.red));
    }
    /**
     * 获取wifi传递过来的数据
     */
    private void getWifiData(){
        if (netConnection.checkNetworkConnection(getActivity())) {
            connectThread = new ConnectThread(socket, myHandler);
            connectThread.start();
            Log.d("connectThread", "启动成功111111");
        } else {
            Toast.makeText(getActivity(), "请连接wifi", Toast.LENGTH_SHORT).show();
        }
    }
    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            Log.d("bundle", String.valueOf(bundle));
            Log.w("是否进行赋值","执行当当前语句");
            String data = bundle.getString("msg");
            Log.d("LineFragmentwifi_data", data);
            if(data.length()<24){
                Toast.makeText(getActivity(),"网络错误！请检查网络连接",Toast.LENGTH_SHORT).show();
            }
            concerto = new Concerto();
            String distance = concerto.Dataconversion(data.substring(18));
            angle = Float.parseFloat(concerto.Dataconversion(data.substring(0,5)));
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
            editor.putFloat("angle",angle);
            editor.putFloat("Verticaldistance",Verticaldistance);
            editor.putFloat("Horizontaldistance",Horizontaldistance);
            editor.putString("Objectdistance",Objectdistance);
            editor.commit();
            drawtriangle.setData(angle,Verticaldistance,Horizontaldistance);

        }
    };
    //单选按钮，判断是否包含仪器长度
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if(checkedId == Including_length.getId()){

        }else if(checkedId == nIncluding_length_length.getId()){

        }
    }
    //重置界面和数据
    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.reset:
                Intent intent = new Intent(getActivity(), Laser_rangingActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(),"重置成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.lock:
                getWifiData();
                break;
            case R.id.Save:
                showDialog();
        }
    }
    private void showDialog(){
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        TextView desc1 = view.findViewById(R.id.desc1);

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        desc1.setText(date);
        new AlertDialog.Builder(getActivity())
                .setTitle("系统提示")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText text = view.findViewById(R.id.name1);
                        String name = text.getText().toString();

                        SharedPreferences.Editor editor = sp.edit();
                        String Odistance = sp.getString("Objectdistance",Objectdistance);

                        Log.d("name",name);
                        String dname = name+".txt";
                        Log.d("name1",dname);
                        try {
                            //如果文件存在则删除文件
                            File file = new File(path, dname);
                            if(file.exists()){
                                fileOutputStream = new FileOutputStream(file,true);
                                num = sp.getInt("num"+name,1)+1;
                                Log.d("num", String.valueOf(num));
                                editor.putInt("num"+name,num);
                                editor.commit();
                                //file.delete();
                                String str = "\n" +
                                        "\t编  号："+num+"\n" +
                                        "\t"+Odistance+"米"+"\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();

                            }else {
                                fileOutputStream = new FileOutputStream(file);
                                editor.putInt("num"+name,1);
                                editor.commit();
                                String str = "\n" +
                                        "\t编  号："+num+"\n" +
                                        "\t"+Odistance+"米"+"\n" +
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
                }).setNegativeButton ("取消", null)
                .create()
                .show();
    }

}
