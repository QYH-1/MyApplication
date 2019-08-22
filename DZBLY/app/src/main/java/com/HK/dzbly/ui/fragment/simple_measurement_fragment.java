package com.HK.dzbly.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.ui.activity.LpszActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    FileOutputStream fileOutputStream = null; //文件输入流
    SharedPreferences sp =null;  //存储对象
    private int num = 1; //文件出现次数
    float val ; //方位角
    float eada ; //仰角
    float rana ; //横滚角
    String result ;//产状信息

    File root = Environment.getExternalStorageDirectory();
    String path = root.getAbsolutePath()+"/CameraDemo"+"/data";  //文件保存的目录
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
        setMsave(view);//保存数据
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
                intent.setClass(getActivity(), LpszActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }
    /**
     * 保存数据
     */
    private void setMsave(View view){
        msave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
    private void showDialog(){
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout,null,false);
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
                        val = sp.getFloat("val",0);//获取指南针数据
                        eada = sp.getFloat("eada",0);//获取仰角数据
                        rana = sp.getFloat("rana",0);//获取横滚角数据
                        result = sp.getString("result","");//获取产状信息数据


                        Log.d("name",name);
                        String dname = name+".txt";
                        Log.d("name1",dname);
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
                }).setNegativeButton ("取消", null)
                .create()
                .show();
    }
}
