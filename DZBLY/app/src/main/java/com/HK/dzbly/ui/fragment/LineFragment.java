package com.HK.dzbly.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.activity.Laser_rangingActivity;
import com.HK.dzbly.utils.drawing.Drawtriangle;
import com.HK.dzbly.utils.wifi.Concerto;
import com.HK.dzbly.utils.wifi.ConnectThread;
import com.HK.dzbly.utils.wifi.NetConnection;

import java.net.Socket;

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
    private RadioGroup Initial_length;
    private ConnectThread connectThread;//wifi连接
    private NetConnection netConnection;//wifi连接检查
    private Concerto concerto;//wifi的数据处理
    private Socket socket;
    SharedPreferences sp = null;


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
        //getWifiData1();
        //DataTransfer();

    }
    private void initView(View view){
        line_ranging = getActivity().findViewById(R.id.line_ranging);
        Initial_length = view.findViewById(R.id.Initial_length);
        nIncluding_length_length = view.findViewById(R.id.nIncluding_length_length);
        Including_length = view.findViewById(R.id.Including_length);
        reset = view.findViewById(R.id.reset);
        lock = view.findViewById(R.id.lock);
        //单选按钮，判断是否包含仪器长度
        nIncluding_length_length.setChecked(true);
        Initial_length.setOnCheckedChangeListener(this);
        reset.setOnClickListener(this);
        lock.setOnClickListener(this);
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
       // Verticaldistance = Float.valueOf(40);
       // Horizontaldistance = Float.valueOf(40);
       // angle =45;
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
            Log.d("LineFragment_angle", String.valueOf(angle));
            Log.d("LineVerticaldistance", String.valueOf(Verticaldistance));
            Log.d("LineHorizontaldistance", String.valueOf(Horizontaldistance));

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("angle",angle);
            editor.putFloat("Verticaldistance",Verticaldistance);
            editor.putFloat("Horizontaldistance",Horizontaldistance);
            editor.commit();

            drawtriangle.setData(angle,Verticaldistance,Horizontaldistance);
        }
    };
    /**
     * 获取wifi传递过来的数据
     */
//    private void getWifiData1(){
//        Verticaldistance = sp.getFloat("Verticaldistance",0.0f);
//        Horizontaldistance = sp.getFloat("Horizontaldistance",0.0f);
//        angle =sp.getFloat("angle",0.0f);
//
//        Log.d("LineFragment_angle1", String.valueOf(angle));
//        Log.d("LineVerticaldistance1", String.valueOf(Verticaldistance));
//        Log.d("LineHorizontaldistance1", String.valueOf(Horizontaldistance));
//    }
    /**
     * 向画图类传递画图参数
     */
//    private void DataTransfer() {
//        try {
//            drawtriangle.setData(angle,Verticaldistance,Horizontaldistance);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
     *通过wifi向硬件传递数据
     */
    private void setData(){

    }
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
        }
    }

}
