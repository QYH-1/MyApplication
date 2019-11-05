package com.HK.dzbly.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.android.volley.VolleyLog.TAG;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：进入时的欢迎界面，识别手机imei码，如何是对应的码才能进入应用
 * 修订历史：
 */
public class WelcomeActivity extends BaseActivity {
    Context mcontext;
    private static final String TODO = null;
    private TextView prompt; //提示信息文本框
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.welcome);

        prompt = findViewById(R.id.prompt);
        //动态获取权限
        int checkPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1); //后面的1为请求码

            Log.d(TAG, "onpause(),未授权,去授权");
            //展示信息
            //showInformation();
            return;
        }
        String imei = getIMEI(mcontext);
        Log.d("imei",imei);
        if (imei.equals("868375038978679")){
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    WelcomeActivity.this.finish();
                }
            };
            timer.schedule(timerTask, 3000 * 1);
        }else {
           // Toast.makeText(WelcomeActivity.this, "对不起，该手机不支持使用该应用！", Toast.LENGTH_SHORT).show();
            prompt.setText("对不起，该手机不支持使用该应用！");
        }

    }

    /**
     * 获取手机IMEI号
     */
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return TODO;
        }
        String imei = telephonyManager.getDeviceId();

        return imei;
    }
    /**
     * 获取手机MSISDN号
     */
    public String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return TODO;
        }
        String imsi = telephonyManager.getSubscriberId();
        return imsi;
    }

    /**
     * 获取手机ICCID号
     */
    public String getICCID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return TODO;
        }
        String iccid = telephonyManager.getSimSerialNumber();
        return iccid;
    }

    /**
     * 获取到信息并展示
     */
//    public void showInformation() {
//        //获取IMEI地址
//        String imei = getIMEI(mcontext);
//        if (imei != null) {
//            Log.d("imei", imei);
//        }
//        //获取IMSI
//        String imsi = getIMSI(mcontext);
//        if (imsi != null) {
//            Log.d("imsi", imsi);
//        }
//        //获取ICCID
//        String iccid = getICCID(mcontext);
//        if (iccid != null) {
//            Log.d("iccid", iccid);
//        }
//
//    }

//    protected void onPause() {
//        super.onPause();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int checkPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1); //后面的1为请求码
//
//                Log.d(TAG, "onpause(),未授权,去授权");
//                //展示信息
//                //showInformation();
//                return;
//
//            }
//            //展示信息
//            //showInformation();
//            Log.d(TAG, "onpause()已授权...");
//
//        } else {
//            //展示信息
//            //showInformation();
//            Log.d(TAG, "onpause()版本<=6.0");
//        }
//    }
}
