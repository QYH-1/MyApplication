package com.HK.dzbly.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;

;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：普通测量fragment
 * 修订历史：
 */
public class ordinary_measurement_fragment extends Fragment {
    private TextView explain;//说明
    private RadioButton r_elevation;//仰角
    private RadioButton r_roll_angle;//仰角
    private TextView save;//保存
    private TextView point_lase;//点激光
    private TextView line_laser;//线激光
    private int type;//对在说明中的内容进行编号 0为测量方法，1为测量出的结果

    public ordinary_measurement_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ordinary_measurement,container,false);
        methods(view);
        return  view;
    }

    private void methods(View view) {
        inint(view);
        setExplain(view);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    //获取控件
    private void inint(View view){
        explain =view.findViewById(R.id.explain);
        r_elevation =view.findViewById(R.id.r_elevation);
        r_roll_angle =view.findViewById(R.id.r_roll_angle);
        point_lase =view.findViewById(R.id.point_lase);
        line_laser =view.findViewById(R.id.line_laser);
        save =view.findViewById(R.id.save);
    }
    //给定说明中的显示
    private void setExplain(View view){
        type=0;
        switch (type){
            case 0:
                explain.setPadding(15,60,0,0);//设置边距
                //explain.setGravity(Gravity.CENTER);//居中显示
                String text = "<p> 测量方法：<br>\n" +
                        "\t\t1）保持设备与待测产状平行（建议使用激光线辅助）；<br>\n" +
                        "\t\t2）调整设备姿态，视倾角为仰角，当仰角在±1°之间时，横滚角即为真倾角。\n" +
                        "\t</p>";
                explain.setText(Html.fromHtml(text));
                break;
            case 1:
                break;
            default:
                break;
        }

    }
}
