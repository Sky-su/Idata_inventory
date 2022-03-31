package com.ives.idata_inventory.rf_mode;

import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ives.idata_inventory.R;

import realid.rfidlib.MyLib;


/**
 * author CYD
 * date 2018/11/19
 * email chengyd@idatachina.com
 */
public class MyApp extends Application {

    public static byte[] UHF = {0x01, 0x02, 0x03};
    private MyLib idataLib;
    public static MyApp myApp;
    private SoundPool soundPool;
    private int soundID;

    public static boolean ifOpenSound = false; //是否启动盘点声音
    //  public static AlertDialog showAtd = null;//应用是否处于弹框状态

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;

        idataLib = new MyLib(this);
        //   MLog.ifShown = false;// 默认true开启日志调试，false关闭
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundID = soundPool.load(this, R.raw.beep, 1);
    }

    public static MyApp getMyApp() {
        return myApp;
    }
    public MyLib getIdataLib() {

        return idataLib;
    }


    //播放滴滴滴的声音
    public void playSound() {
        soundPool.play(soundID, 1, 1, 0, 1, 1);
    }


}
