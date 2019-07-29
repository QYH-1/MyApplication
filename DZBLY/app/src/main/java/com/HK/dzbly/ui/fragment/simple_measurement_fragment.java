package com.HK.dzbly.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.activity.LpszActivity;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：
 * 修订历史：
 */
public class simple_measurement_fragment extends Fragment {
    private TextView mexplain;//说明
    private TextView re_measurement;//重新测量
    private TextView locking_occurrence;//锁定左侧产状物
    private TextView msave;//保存
    private int type;//说明文本显示类型
    private TextView Compass_settings;//罗盘设置
    public simple_measurement_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_measurement,container,false);
        //所有的方法
        methods(view);
        return view;
    }

    private void methods(View view) {
        //获取控件
        inint(view);
        //设置说明
        setExplain(view);
        //设置罗盘
        setCompass_settings(view);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    //获取控件
    private void inint(View view){
        mexplain =view.findViewById(R.id.mexplain);
        re_measurement =view.findViewById(R.id.re_measurement);
        locking_occurrence =view.findViewById(R.id.locking_occurrence);
        msave =view.findViewById(R.id.msave);
        Compass_settings = view.findViewById(R.id.Compass_settings);
    }
    //给定说明中的显示
    private void setExplain(View view) {
        type=0;
        switch (type){
            case 0:
                mexplain.setPadding(15,50,0,0);//设置边距
                //explain.setGravity(Gravity.CENTER);//居中显示
                String text = "<p> 测量方法：通过两次视产状测量获得真倾角与倾角<br>\n" +
                        "\t\t1）右侧测量：设备右偏后调整姿态，使左侧激光线与左侧产状线重合，右侧点\n" +
                        "\t\t激光也在右侧视产状线上，然后锁定测量结果；<br>\n" +
                        "\t\t2）左侧测量：设备左偏后调整姿态，使右侧激光线与右侧产状线重合，左侧点\n" +
                        "\t\t激光也在左侧视产状线上，然后锁定测量结果；<br>\n" +
                        "\t</p>";
                mexplain.setText(Html.fromHtml(text));
                break;
            case 1:
                break;
            default:
                break;
        }

    }
    /**
     * 罗盘设置
     */
    private void setCompass_settings(View view){
        Compass_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LpszActivity.class);
                startActivity(intent);

            }
        });
    }
}
