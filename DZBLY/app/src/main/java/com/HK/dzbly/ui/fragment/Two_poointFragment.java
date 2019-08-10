package com.HK.dzbly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/6$
 * 描述：
 * 修订历史：
 */
public class Two_poointFragment extends Fragment {
    private View view;

    public Two_poointFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twopoint_ranging,container,false);

        return view;
    }
}
