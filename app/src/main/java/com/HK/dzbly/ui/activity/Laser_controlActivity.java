package com.HK.dzbly.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;
import com.HK.dzbly.utils.wifi.ControlData;
import com.HK.dzbly.utils.wifi.Laser_control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/10/30
 * 描述：
 * 修订历史：
 */
public class Laser_controlActivity extends BaseActivity {
    private Context mContext;
    private GridView grid_photo;
    private SimpleAdapter sim_adapter; //使用适配器
    private int position; //记录点击的适配器中的位置
    private List<Map<String, Object>> data_list = new ArrayList<>(); //用于存储适配器的数据源
    private String clickText; //用户点击的数据项
    private String clinkData; //用户点击的数据项
    private String[] openName = {"打开左侧点激光", "打开左侧线激光", "打开右侧点激光", "打开右侧线激光", "打开下侧点激光", "打开下侧线激光", "打开上侧点激光", "打开上侧线激光"};
    private String[] closeName = {"关闭左侧点激光", "关闭左侧线激光", "关闭右侧点激光", "关闭右侧线激光", "关闭下侧点激光", "关闭下侧线激光", "关闭上侧点激光", "关闭上侧线激光"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.laser_control);

        grid_photo = (GridView) findViewById(R.id.grid_photo);

        for (int i = 0; i < 8; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("openText", openName[i]);
            data_list.add(map);
        }
        changeView(data_list);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = new Bundle();
            bundle = msg.getData();
            clinkData = bundle.getString("clickText");
            Log.d("clinkData", clinkData);
            changeView(changeData(clinkData));
        }
    };

    /**
     * 使用适配器动态显示数据，并返回用户点击的数据选项
     *
     * @param data_list
     */
    private void changeView(List<Map<String, Object>> data_list) {
        //新建适配器
        String[] from = {"openText"};
        int[] to = {R.id.txt_icon};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_grid_icon, from, to);
        grid_photo.setAdapter(sim_adapter);
        grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) Laser_controlActivity.this.sim_adapter.getItem(position);
                clickText = (String) map.get("openText");
//                Log.d("position", String.valueOf(position));
                Log.d("-----clinkData-----", clickText);
                ControlData controlData = new ControlData();
                //利用子线程向硬件发送控制指令
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("laser_control","laser_control");
                        Laser_control laser_control = new Laser_control();
                        laser_control.laserControl(controlData.setData(clickText));
                    }
                }).start();
              //  Toast.makeText(getApplicationContext(), "单击的是" + clickText, Toast.LENGTH_LONG).show();
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("clickText", clickText);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 改变适配器的数据源
     *
     * @param clinkData
     * @return
     */
    private List<Map<String, Object>> changeData(String clinkData) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < openName.length; i++) {
            Map<String, Object> map = new HashMap<>();
            if (clinkData.equals(openName[i])) {
                map.put("openText",closeName[i]);
            }else {
                map.put("openText", openName[i]);
            }
            data.add(map);
        }
        return data;
    }
}
