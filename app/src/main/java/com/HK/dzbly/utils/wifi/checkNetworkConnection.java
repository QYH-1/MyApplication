package com.HK.dzbly.utils.wifi;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/11/5
 * 描述： 判断是否连上指定wifi
 * 修订历史：
 */
public class checkNetworkConnection {
    private WifiManager mWifiManager;

    /**
     * 是否已连接指定wifi
     */
    public boolean isConnected(String ssid, WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return false;
        }
        switch (wifiInfo.getSupplicantState()) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
            case COMPLETED:
                return wifiInfo.getSSID().replace("\"", "").equals(ssid);
            default:
                return false;
        }
    }
    /**
     * 打开WiFi
     *
     * @return
     */
    public boolean openWifi() {
        boolean opened = true;
        if (!mWifiManager.isWifiEnabled()) {
            opened = mWifiManager.setWifiEnabled(true);
        }
        return opened;
    }
}
