package com.HK.dzbly.ui.fragment;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    private TreeIllustrationFragment fragment;
    private TextView reset;
    private GLSurfaceView glView;
    private Handler drawlineHandler;
    public Two_poointFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.sanwei,container,false);

        Init(view);
        //Tp_TreeIllustrationFragment(view);
        //Tp(view);
       // TwoOptions(view);
        return view;
    }
    private void Init(View view){
       // reset = view.findViewById(R.id.reset);
        fragment = new TreeIllustrationFragment();
        //fragment2 = new TwoOptionsFragment();
        glView = (GLSurfaceView) view.findViewById(R.id.glView);
    }
//    private void Tp_TreeIllustrationFragment(View view){
//        FragmentManager fragmentManager = getChildFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.sanwei,fragment).commit();
//    }
//    private void TwoOptions(View view){
//            FragmentManager fragmentManager = getChildFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.Options,fragment2).commit();
//    }
//    private void Tp(View view){
//        Threedimensional_coordinates myRender = new Threedimensional_coordinates(drawlineHandler);
//        glView.setRenderer(myRender);
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        glView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        glView.onPause();
//    }
}
