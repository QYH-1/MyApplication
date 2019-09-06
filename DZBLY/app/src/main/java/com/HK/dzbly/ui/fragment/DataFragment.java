package com.HK.dzbly.ui.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;

import java.io.File;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/29$
 * 描述：显示数据列表中具体的数据内容
 * 修订历史：
 */
public class DataFragment extends Fragment {
    private View view;
    private TextView datasshow, dataname, name;//显示数据的控件
    private String dataName;//名称
    private String dataTime;//时间
    private String result;//结果（距离、产状）
    private String val; //方位角
    private String rollAngle;//横滚角
    private String elevation; //俯仰角
    private String type; //数据类型
    private String displayDataName;//显示的数据
    private String displayName;//显示的数据
    private String display;//显示的数据
    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/video";  //文件保存的目录
    String path1 = root.getAbsolutePath() + "/CameraDemo" + "/capture";  //文件保存的目录
    String imagePath = null;
    String videoPath = null;
    private VideoView video;
    private ImageView img;
    private String ditem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.datashow, container, false);

        inInt(view);
        getData();
        setView(view);
        return view;
    }

    private void inInt(View view) {
        datasshow = view.findViewById(R.id.datasshow);
        dataname = view.findViewById(R.id.dataname);
        name = view.findViewById(R.id.name);
        video = view.findViewById(R.id.video);
        img = view.findViewById(R.id.img);
    }

    private void setView(View view) {
        if (type.equals("jpg")) {
            //通过getVideoThumbnail方法取得视频中的第一帧图片，该图片是一个bitmap对象
            Log.i("imagePath", imagePath);
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            //将图片显示到ImageView中
            img.setImageBitmap(bm);

        } else {
            if (type.equals("line")) {
                displayDataName = "直线测距：";
                displayName = "\t名  称：" + dataName + "";
                display = "\t时  间：" + dataTime + "\n" +
                        "\t距  离：" + result + "\n" +
                        "\t\n";

            } else if (type.equals("twoPoint")) {
                displayDataName = "两点测距：";
                displayName = "\t名  称：" + dataName + "";
                display = "\t时  间：" + dataTime + "\n" +
                        "\t距  离：" + result + "\n" +
                        "\t\n";
            } else if (type.equals("dZbl")) {
                displayDataName = "产状测量：";
                displayName = "\t名        称：" + dataName + "";
                display = "\t时        间：" + dataTime + "\n" +
                        "\t方  位  角：" + val + "\n" +
                        "\t俯  仰  角：" + elevation + "\n" +
                        "\t横  滚  角：" + rollAngle + "\n" +
                        "\t产状信息：" + result + "\n" +
                        "\t\n";
            }
            dataname.setText(displayDataName);
            name.setText(displayName);
            datasshow.setText(display);
        }

    }

    /**
     * 得到从activity传递过来的另一个fragment中的数据，并在数据库中查找
     */
    private void getData() {
        Bundle bundle = this.getArguments();//得到从Activity传来的数据
        if (bundle != null) {
            dataName = bundle.getString("dataName");
            dataTime = bundle.getString("dataTime");
            ditem = bundle.getString("ditem");
            Log.i("--ditem--", ditem);
        }
        if (ditem.equals("照片")) {
            type = "jpg";
        } else if (ditem.equals("视频")) {
            type = "video";
        } else {
            type = "";
        }

        //获取数据库对象
        DBhelper dBhelper = new DBhelper(getContext(), "cqhk.db");
        if (type.equals("")) {
            String selection1 = " CreatedTime = '" + dataTime + "' and name= '" + dataName + "'";
            Cursor cursor = dBhelper.Query(getContext(), "DZBLY", null, selection1, null, null, null, "CreatedTime desc");
            //获取选中的行的数据
            while (cursor.moveToNext()) {
                val = cursor.getString(3);
                rollAngle = cursor.getString(4);
                elevation = cursor.getString(5);
                type = cursor.getString(6);
                result = cursor.getString(7);
            }
        } else if (type.equals("jpg")) {
            imagePath = path1 + "/" + dataName;
            Log.i("imagePath", imagePath);
        } else if (type.equals("video")) {
            videoPath = path + "/" + dataName;
            Log.i("videoPath", videoPath);
        }
    }
}
