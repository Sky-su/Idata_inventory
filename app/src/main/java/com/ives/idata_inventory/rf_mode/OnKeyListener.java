package com.ives.idata_inventory.rf_mode;

import android.view.KeyEvent;

/**
 * Author CYD
 * Date 2019/2/20
 * Email chengyd@idatachina.com
 */
public interface OnKeyListener extends OnKeyDownListener {

    void onKeyUp(int keyCode, KeyEvent event);
}
