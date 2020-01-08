package com.HK.dzbly.utils.clikeButton;

import android.view.View;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2020/1/5
 * 描述：
 * 修订历史：
 */
public abstract class OnMultiClickListener implements View.OnClickListener {
    // 两次点击按钮之间的点击间隔不能少于2000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public abstract void onMultiClick(View view);

    @Override
    public void onClick(View view) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(view);
        }
    }
}
