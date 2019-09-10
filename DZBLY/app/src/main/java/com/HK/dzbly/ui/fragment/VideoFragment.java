package com.HK.dzbly.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;

import java.io.File;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/9/6$
 * 描述：
 * 修订历史：
 */
public class VideoFragment extends Fragment {
    private View view;
    private VideoView video;
    private String dataName;//名称
    private String ditem;
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/video";  //文件保存的目录
    String videoPath = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.videofragment, container, false);
        getData();
        setView(view);
        return view;
    }

    private void setView(View view) {
        video = view.findViewById(R.id.video);
        //为 VideoView 视图设置媒体控制器，设置了之后就会自动由进度条、前进、后退等操作
        video.setMediaController(new MediaController(getContext()));
        //以文件路径的方式设置 VideoView 播放的视频源
        video.setVideoPath(videoPath);
        //视频准备完成时回调
    }

    /**
     * 得到从activity传递过来的另一个fragment中的数据，并在数据库中查找
     */
    private void getData() {
        Bundle bundle = this.getArguments();//得到从Activity传来的数据
        if (bundle != null) {
            dataName = bundle.getString("dataName");
            ditem = bundle.getString("ditem");
            Log.i("--ditem--", ditem);
        }
        videoPath = path + "/" + dataName;
    }

}
