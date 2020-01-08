package com.HK.dzbly.utils.wifi;

import android.app.Service;
import android.content.Context;
import android.os.Environment;

import com.HK.dzbly.R;

import java.io.File;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2020/1/2
 * 描述：
 * 修订历史：
 */
public class DZBLYData {
    private static DZBLYData dZBLYData = null;

    public static DZBLYData getInstance(Context context){
        if (dZBLYData == null) {
            dZBLYData = new DZBLYData(context);
        }
        return dZBLYData;
    }
    private DZBLYData(Context context){
    }
}
