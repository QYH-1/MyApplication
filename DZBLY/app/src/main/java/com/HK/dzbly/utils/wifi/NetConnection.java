package com.HK.dzbly.utils.wifi;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/21$
 * 描述：检查网络连接
 * 修订历史：
 */
public class NetConnection {
    public static boolean checkNetworkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isAvailable())
            return true;
        else
            return false;
    }
}
