package com.HK.dzbly.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
 * 创建日期：2019/12/10
 * 描述： 获取当前经纬度
 * 修订历史：
 */
public class LocationUtils implements LocationListener {

    private Context context;
    private String provider;

    public LocationUtils(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);// 允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
        provider = locationManager.getBestProvider(criteria, true);

        boolean isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGps) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            }
            Log.d("location", String.valueOf(location));
            return location;
        } else {
            Toast.makeText(context, "开启GPS", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
