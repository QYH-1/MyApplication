package com.HK.dzbly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.utils.drawing.Drawtriangle;

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
    private Float Verticaldistance; //接收wifi传递的垂距
    private Float Horizontaldistance;//接收wifi传递的平距
    private RadioButton nIncluding_length_length; //不包含仪器长度
    private RadioButton Including_length; //包含仪器长度
    private TextView reset;//重置
    private TextView lock;//锁定
    private RadioGroup Initial_length;

    public LineFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_line_ranging,container,false);
        Content(view);
       return view;
    }
    private void Content(View view){
        initView(view);
        getWifiData();
        DataTransfer();

    }
    private void initView(View view){
        line_ranging = getActivity().findViewById(R.id.line_ranging);
        drawtriangle = view.findViewById(R.id.drawtriangle);
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

        Verticaldistance = Float.valueOf(40);
        Horizontaldistance = Float.valueOf(40);
        angle =45;
    }
    /**
     * 向画图类传递画图参数
     */
    private void DataTransfer() {
        try {
            drawtriangle.setData(angle,Verticaldistance,Horizontaldistance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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

                Toast.makeText(getActivity(),"重置成功",Toast.LENGTH_SHORT).show();
            break;
            case R.id.lock:

                break;
        }
    }

}
