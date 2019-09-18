package com.HK.dzbly.collector;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/*  活动管理类 实现随时退出程序*/
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /* 退出程序，杀死进程*/
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());//杀死进程
    }
}
