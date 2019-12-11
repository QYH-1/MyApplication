package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.HK.dzbly.R;
import com.HK.dzbly.utils.LocationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/30$
 * 描述：
 * 修订历史：
 */
public class testActivity extends Activity {

    private Button test1;
    private TextView textView;
    public Context context;

    private String result;
    private TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        //获取GPS权限
        ActivityCompat.requestPermissions(testActivity.this, new String[]
                {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 122);

        info = (TextView) findViewById(R.id.tv);
        test1 = findViewById(R.id.test1);
        textView = findViewById(R.id.TextView);

        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils utils = new LocationUtils(getApplicationContext());
                Location l = utils.getLocation();
                double lat = l.getLatitude();
                double lon = l.getLongitude();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String strUrlPath = "https://cloud.cqhky.com:7777/api/cpj?lat=" + lat + "&lon=" + lon + "";
                        result = getServiceInfo(strUrlPath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(result);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * 发送Get请求到服务器
     *
     * @param strUrlPath:接口地址（带参数）
     * @return
     */
    public String getServiceInfo(String strUrlPath) {
        String strResult = "";
        try {
            URL url = new URL(strUrlPath);
            Log.d("url", String.valueOf(url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            strResult = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("strResult", strResult);
        return strResult;
    }

}
