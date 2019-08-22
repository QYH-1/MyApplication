package com.HK.dzbly.ui.fragment;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/15$
 * 描述：
 * 修订历史：
 */
public class TreeIllustrationFragment extends Fragment {
    private GLSurfaceView glView;
    private Handler drawlineHandler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sanwei,container,false);

        //Inint(view);

        return view;
    }
//    private void Inint(View view){
//        glView = (GLSurfaceView)view.findViewById(R.id.glView);
//        Threedimensional_coordinates myRender = new Threedimensional_coordinates(drawlineHandler);
//        glView.setRenderer(myRender);
//    }
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }

}
