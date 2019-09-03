package com.HK.dzbly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/29$
 * 描述：显示数据列表中具体的数据内容
 * 修订历史：
 */
public class DataFragment extends Fragment {
    private  View view;
    private TextView datasshow;
    private String dataName;
    private String dataTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.datashow,container,false);

        inInt(view);
        getData();
        setView(view);
        return view;
    }
    private void inInt(View view){
        datasshow = view.findViewById(R.id.datasshow);
    }
    private void setView(View view){
        datasshow.setText(dataName+dataTime);
    }
    private void getData(){
        Bundle bundle =this.getArguments();//得到从Activity传来的数据
        if (bundle != null){
            dataName = bundle.getString("dataName");
            dataTime = bundle.getString("dataTime");
        }
    }
}
