package com.HK.dzbly.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;
import com.HK.dzbly.ui.activity.LpszActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/25$
 * 描述：普通测量fragment
 * 修订历史：
 */
public class ordinary_measurement_fragment extends Fragment {
    private TextView explain;//说明
    private TextView save;//保存
    private TextView point_lase;//点激光
    private TextView line_laser;//线激光
    private TextView Compass_settings;//罗盘设置
    private int type;//对在说明中的内容进行编号 0为测量方法，1为测量出的结果
    FileOutputStream fileOutputStream = null; //文件输入流
    SharedPreferences sp = null;  //存储对象
    private DBhelper dBhelper;
    private int num = 1; //文件出现次数
    float val; //方位角
    float eada; //仰角
    float rana; //横滚角
    String result;//产状信息

    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath() + "/CameraDemo" + "/data";  //文件保存的目录

    public ordinary_measurement_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ordinary_measurement, container, false);
        //所有的方法
        methods(view);
        return view;
    }

    private void methods(View view) {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());//获取了SharePreferences对象
        //获取控件
        inint(view);
        //设置说明
        setExplain(view);
        //设置罗盘
        setCompass_settings(view);
        setSave(view);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //获取控件
    private void inint(View view) {
        explain = view.findViewById(R.id.explain);
        point_lase = view.findViewById(R.id.point_lase);
        line_laser = view.findViewById(R.id.line_laser);
        save = view.findViewById(R.id.tsave);
        Compass_settings = view.findViewById(R.id.Compass_settings);
    }

    //给定说明中的显示
    private void setExplain(View view) {
        type = 0;
        switch (type) {
            case 0:
                explain.setPadding(15, 60, 0, 0);//设置边距
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

    /**
     * 罗盘设置
     */
    private void setCompass_settings(View view) {
        Compass_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LpszActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * 保存数据
     */
    private void setSave(View view) {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
    }

    private void showDialog() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        TextView desc1 = view.findViewById(R.id.desc1);

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        desc1.setText(date);
        new AlertDialog.Builder(getActivity())
                .setTitle("系统提示")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText text = view.findViewById(R.id.name1);
                        String name = text.getText().toString();

                        SharedPreferences.Editor editor = sp.edit();
                        val = sp.getFloat("val", 0);//获取指南针数据
                        eada = sp.getFloat("eada", 0);//获取仰角数据
                        rana = sp.getFloat("rana", 0);//获取横滚角数据
                        result = sp.getString("result", "");//获取产状信息数据
                        //将数据存储到数据库中
                        dBhelper = new DBhelper(getContext(),dBhelper.db_name,null,1);
                        ContentValues cv = new ContentValues();
                        cv.put("Dname",1);
                        cv.put("Dval",1);
                        cv.put("Drollangle",1);
                        cv.put("Delevation",1);
                        cv.put("type","dzbl");
                        cv.put("Dresult",1);
                        dBhelper.Insert(getContext(),dBhelper.DZBLY_TABLE,cv);

                        Log.d("name", name);
                        String dname = name + ".txt";
                        Log.d("name1", dname);
                        try {
                            //如果文件存在则删除文件
                            File file = new File(path, dname);
                            if(file.exists()){
                                fileOutputStream = new FileOutputStream(file,true);
                                num = sp.getInt("num"+name,1)+1;
                                Log.d("num", String.valueOf(num));
                                editor.putInt("num"+name,num);
                                editor.commit();
                                //file.delete();
                                String str = "\n" +
                                        "\t编  号："+num+"\n" +
                                        "\t仰  角："+eada+"  \n" +
                                        "\t横滚角："+rana+" \t\n" +
                                        "\t方位角："+val+" \t\n" +
                                        "\t产状信息："+result+" \t\n" +
                                        "\t测量时间："+date+"\t\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();

                            }else {
                                fileOutputStream = new FileOutputStream(file);
                                editor.putInt("num"+name,1);
                                editor.commit();
                                String str = "\n" +
                                        "\t编  号："+num+"<br>\n" +
                                        "\t仰  角：   <br>\n" +
                                        "\t横滚角：\t<br>\n" +
                                        "\t方位角：\t<br>\n" +
                                        "\t产状信息：\t<br>\n" +
                                        "\t测量时间：\t<br>\n" +
                                        "\t\n";
                                fileOutputStream.write(str.getBytes());
                                fileOutputStream.close();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("取消", null)
                .create()
                .show();
    }

}
