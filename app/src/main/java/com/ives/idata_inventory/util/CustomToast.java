package com.ives.idata_inventory.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.ives.idata_inventory.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class CustomToast {
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
           // mToast.cancel();
            tipDialog.dismiss();
        }
    };

    public static void showToast(Context mContext, String text, int duration) {
       // mHandler.removeCallbacks(r);
//        if (mToast != null){
//            mToast.setText(text);
//        }
//        else{
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            mToast.show();
        //}
//        LinearLayout layout = (LinearLayout) mToast.getView();
//        TextView tv = (TextView) layout.getChildAt(0);
//        tv.setTextSize(25);
//        mToast.setGravity(Gravity.CENTER, 0, 0);
//        mToast.setText(text);
//        mHandler.postDelayed(r, duration);


    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

    private static QMUITipDialog tipDialog = null;


    public static void showLoad(Context mContext, String text, int duration){
        mHandler.removeCallbacks(r);
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
        tipDialog= new QMUITipDialog.Builder(mContext)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                    .setTipWord(text)
                    .create();
        tipDialog.setCancelable(false);
        tipDialog.show();
        mHandler.postDelayed(r, duration);
    }


    public static void showLoad(Context mContext, String text){
        mHandler.removeCallbacks(r);
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
        tipDialog= new QMUITipDialog.Builder(mContext)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                .setTipWord(text)
                .create();
        tipDialog.setCancelable(false);
        tipDialog.show();
    }
    public static void showDismiss(){
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
    }

    public static void battery(Context mContext, String str){
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
        QMUIDialog tip =  new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle(mContext.getString(R.string.remind))
                .setMessage(String.format("%s%s",mContext.getString(R.string.battery),str))
                .addAction(mContext.getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
