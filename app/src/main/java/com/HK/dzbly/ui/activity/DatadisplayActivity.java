package com.HK.dzbly.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.fragment.DataFragment;
import com.HK.dzbly.ui.fragment.DatashowFragment;
import com.HK.dzbly.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/28$
 * 描述：查看数据，向用户展示数据
 * 修订历史：
 */
public class DatadisplayActivity extends FragmentActivity implements DatashowFragment.FragmentInteraction {
    private Spinner dspinner, tspinner;
    private Button kz;
    private List<String> data = new ArrayList<String>(); //用来存储测量数据类别
    private List<String> tdata = new ArrayList<String>(); //用来存储时间数据类别
    private DatashowFragment df;//获取fragment对象
    private VideoFragment videoFragment;
    private DataFragment dataFragment;
    private String ditem = null;
    private String titem = null;
    private String type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.datadisplay);

        getDataSource();
        inInt();
        setSpinner();
        setKz();
    }

    private void inInt() {
        dspinner = findViewById(R.id.Data_type);
        tspinner = findViewById(R.id.time);
        kz = findViewById(R.id.kz);
    }

    private void setKz() {
        kz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                df = new DatashowFragment();
                Bundle bundle = new Bundle();
                Log.i("ditem----------", ditem);
                Log.i("titem------", titem);
                bundle.putString("ditem", ditem);
                bundle.putString("titem", titem);
                df.setArguments(bundle);//数据传递到fragment中
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.dataShow, df);
                fragmentTransaction.commit();
                kz.setText("查看");
            }
        });
    }

    private void setSpinner() {
        //列表定义一个数组适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_datatype, data);
        //将适配器配置到下拉列表上
        dspinner.setAdapter(adapter);
        //给下拉菜单设置监听事件
        dspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.red));
                tv.setTextSize(18.0f);
                //tv.setGravity(Gravity.CENTER_HORIZONTAL);
                ditem = (String) dspinner.getItemAtPosition(i);
                Log.d("ditem", ditem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //列表定义一个数组适配器
        ArrayAdapter<String> tadapter = new ArrayAdapter<String>(this, R.layout.item_datatype, tdata);
        //将适配器配置到下拉列表上
        tspinner.setAdapter(tadapter);
        //给下拉菜单设置监听事件
        tspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.red));
                tv.setTextSize(18.0f);
                //tv.setGravity(Gravity.CENTER_HORIZONTAL);
                titem = (String) tspinner.getItemAtPosition(i);
                Log.d("titem", titem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * 添加下拉菜单列表
     */
    public void getDataSource() {
        data.add("直线测距");
        data.add("两点测距");
        data.add("断面测距");
        data.add("连续测距");
        data.add("累加测距");
        data.add("累减测距");
        data.add("地质编录");
        data.add("照片");
        data.add("视频");
        data.add("全部");
        tdata.add("一天");
        tdata.add("半个月");
        tdata.add("一个月");
        tdata.add("一个季度");
        tdata.add("半年");
        tdata.add("一年");
        tdata.add("全部");
    }

    //实现接口，实现回调
    @Override
    public void process(Map<String, Object> str) {
        Map<String, Object> rdata = null;
        if (str != null) {
            rdata = str;
            Log.i("rdata", String.valueOf(rdata));
        }
        String dataName = String.valueOf(rdata.get("name"));
        String dataTime = String.valueOf(rdata.get("time"));

        dataFragment = new DataFragment();
        videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("dataName", dataName);
        bundle.putString("dataTime", dataTime);
        bundle.putString("ditem", ditem);
        String data = dataName.substring(dataName.indexOf(".") + 1);
        Log.i("----data======", data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (data.equals("mp4")) {
            videoFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.dataShow, videoFragment);
        } else {
            dataFragment.setArguments(bundle);//数据传递到fragment中
            fragmentTransaction.replace(R.id.dataShow, dataFragment);
        }
        fragmentTransaction.commit();
        kz.setText("返回");
    }
}
