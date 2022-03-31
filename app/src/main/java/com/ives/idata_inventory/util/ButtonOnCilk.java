package com.ives.idata_inventory.util;

import android.view.View;

public class ButtonOnCilk {

        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private static final int MIN_CLICK_DELAY_TIME = 2000;
        private static long lastClickTime;
        private static View lastView;

        public static boolean isFastClick() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                flag = true;
            }
            lastClickTime = curClickTime;
            return flag;
        }

    public static boolean isFastViewClick(View view) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME || lastView != view) {
            lastView = view;
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static boolean isIDClick(int i) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= i) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
