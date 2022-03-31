package com.ives.idata_inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ives.idata_inventory.dao.GlobalUtil;
import com.ives.idata_inventory.rf_mode.GetRFIDThread;
import com.ives.idata_inventory.rf_mode.MUtil;
import com.ives.idata_inventory.rf_mode.MyApp;
import com.ives.idata_inventory.rf_mode.OnKeyDownListener;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.ives.idata_inventory.ui.MainFragment;
import com.ives.idata_inventory.ui.PdfViewFragment;
import com.ives.idata_inventory.ui.SeachTagFragment;
import com.ives.idata_inventory.ui.SettingFragment;
import com.ives.idata_inventory.ui.Stock_Inventory;
import com.ives.idata_inventory.ui.SwitchLanguageFragment;
import com.ives.idata_inventory.util.AesUtilZone;
import com.ives.idata_inventory.util.CustomToast;
import com.ives.idata_inventory.util.FileUtil;
import com.ives.idata_inventory.util.MLog;
import com.ives.idata_inventory.util.SoundPoolHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.Timer;
import java.util.TimerTask;

import realid.rfidlib.EmshConstant;

import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_ERROR;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_FULL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_GENERAL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_QUICK;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY;

public class MainActivity extends BaseActivity {

    //RFID
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private EmshStatusBroadcastReceiver mEmshStatusReceiver;
    private Stock_Inventory stock_inventory;
    private SettingFragment settingFragment;

    private FragmentManager manager;
    private boolean ifRequesetPermission = true;
    private Object currentFragment ;
    private GetRFIDThread rfidThread = GetRFIDThread.getInstance();//RFID标签信息获取线程

    //private LoginFragment loginFragment;
    private MainFragment mainFragment;
    private SeachTagFragment seachTagFragment;
    private SwitchLanguageFragment switchLanguageFragment;
    private PdfViewFragment pdfFragment;



    public SoundPoolHelper soundPoolHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        if (soundPoolHelper==null){
            soundPoolHelper = new SoundPoolHelper(4, SoundPoolHelper.TYPE_MUSIC)
                    .setRingtoneType(SoundPoolHelper.RING_TYPE_MUSIC)
                    .load(this, "happy1", R.raw.duka3);
        }
    }
    //UI实例化
    private void init(){
        MyApp.getMyApp().getIdataLib().changeConfig(true); //初始化开启把枪和串口配置
        MLog.e("poweron = " + MyApp.getMyApp().getIdataLib().powerOn());
        manager = getSupportFragmentManager();
        setCurrentPage(1);
        rfidThread.start();
        monitorEmsh();
    }

    public FragmentTransaction transaction;
    //设置当前显示的页面
    public void setCurrentPage(int tabPage) {
        transaction = manager.beginTransaction();
        switch (tabPage) {
            case 0: //登录界面
               // currentFragment = loginFragment = (loginFragment == null ? new LoginFragment() : loginFragment);
                break;
            case 1: //主界面
                currentFragment = mainFragment = (mainFragment == null ? new MainFragment() : mainFragment);
                break;
            case 2: //盘点界面
                currentFragment = stock_inventory = (stock_inventory == null ? new Stock_Inventory() : stock_inventory);
                break;
            case 3: //设置界面
                currentFragment = settingFragment = (settingFragment == null ? new SettingFragment() : settingFragment);
                break;
            case 4: //寻物界面
                currentFragment = seachTagFragment = (seachTagFragment == null ? new SeachTagFragment() : seachTagFragment);
                break;
            case 5: //语言界面
                currentFragment = switchLanguageFragment = (switchLanguageFragment == null ? new SwitchLanguageFragment() : switchLanguageFragment);
                break;
            case 6: //界面
                currentFragment = pdfFragment = (pdfFragment == null ? new PdfViewFragment() : pdfFragment);
                break;
            default:
                break;
        }
        transaction.replace(R.id.showData, (Fragment) currentFragment);
        transaction.commit();
    }
    private void clearFilter() {
        boolean status = MyApp.getMyApp().getIdataLib().filterSet
                (MyApp.UHF[0], 0, 0, "", false);
        if (status) {
            // MUtil.show(R.string.clean_success);
            //重置功率为30，区域频率为美国
            MyApp.getMyApp().getIdataLib().powerSet(30);
            MyApp.getMyApp().getIdataLib().frequencyModeSet(4);
        } else {
            //MUtil.show(R.string.failed);
        }
    }

    //APP 权限
    private int REQUEST_PERMISSION_CODE = 1000;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};
    private android.app.AlertDialog alertDialog;
    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
                FileUtil.checkFile(this);
                ifRequesetPermission = false;
                init();
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 42) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileUtil.checkFile(this);
                Log.i("onPermissionsResult:", "权限" + permissions[0] + "申请成功");
            } else {
                Log.i("onPermissionsResult:", "用户拒绝了权限申请");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.permission))
                        .setMessage(getString(R.string.permissionMessage))
                        .setPositiveButton(getString(R.string.Allow), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                    alertDialog =null;
                                }
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        });
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }

    }


    public boolean isNetworkAvailable(){
        return QMUIDisplayHelper.hasInternet(this);
    }

    QMUIDialog.EditTextDialogBuilder builder;
    private void Judge_serial(final Context connect,String data){
        if (GlobalUtil.getAuthorization(this).equals("")) {
            //Log.e("解密后 = {}", decrypt);

            builder = new QMUIDialog.EditTextDialogBuilder(connect);
            builder.setTitle(getString(R.string.activation_code))
                    .setInputType(InputType.TYPE_CLASS_TEXT)
                    .addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            CharSequence text = builder.getEditText().getText();
                            if (text != null && text.length() > 0) {
                                String decrypt = AesUtilZone.decrypt(text.toString(), AesUtilZone.AES_KEY, AesUtilZone.AES_VECTOR);
                                if (data.equals(decrypt)){
                                    GlobalUtil.setAuthorization(connect,"成功");
                                    dialog.dismiss();
                                }else{
                                    CustomToast.showLoad(connect,getString(R.string.fails),2000);
                                }
                            } else {
                                CustomToast.showLoad(connect,getString(R.string.fails),2000);
                            }
                        }
                    }).show();
            builder.setCancelable(false);
        }
    }
    //定时监听把枪状态
    private void monitorEmsh() {
        mEmshStatusReceiver = new EmshStatusBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EmshConstant.Action.INTENT_EMSH_BROADCAST);
        registerReceiver(mEmshStatusReceiver, intentFilter);
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(EmshConstant.Action.INTENT_EMSH_REQUEST);
                intent.putExtra(EmshConstant.IntentExtra.EXTRA_COMMAND, EmshConstant.Command.CMD_REFRESH_EMSH_STATUS);
                sendBroadcast(intent);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    //下电，回收停止线程，退出应用
    private void recyleResoure() {
        //这里强制停止盘点，无论是否使用
        MyApp.getMyApp().getIdataLib().stopInventory();
        if (mEmshStatusReceiver != null) {
            unregisterReceiver(mEmshStatusReceiver);
            mEmshStatusReceiver = null;
        }
        if (mTimer != null || mTimerTask != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }
        rfidThread.destoryThread();
        Log.e("yyyy","powoff = " + MyApp.getMyApp().getIdataLib().powerOff());
        MyApp.getMyApp().getIdataLib().changeConfig(false);
        System.exit(0);
    }



    //手柄按钮控制RFID线程读取与停止
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentFragment instanceof OnKeyDownListener)
            ((OnKeyDownListener) currentFragment).onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (currentFragment instanceof OnKeyListener)
            ((OnKeyListener) currentFragment).onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (!ifRequesetPermission) recyleResoure();

    }


    private int oldStatue = -1;

    public class EmshStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (EmshConstant.Action.INTENT_EMSH_BROADCAST.equalsIgnoreCase(intent.getAction())) {

                int sessionStatus = intent.getIntExtra("SessionStatus", 0);
                int batteryPowerMode = intent.getIntExtra("BatteryPowerMode", -1);
                //  MLog.e("sessionStatus = " + sessionStatus + "  batteryPowerMode  = " + batteryPowerMode);
                if ((sessionStatus & EmshConstant.EmshSessionStatus.EMSH_STATUS_POWER_STATUS) != 0) {
                    // 把枪电池当前状态
                    if (batteryPowerMode == oldStatue) { //相同状态不处理
                        MUtil.cancelWaringDialog();
                        return;
                    }
                    oldStatue = batteryPowerMode;
                    switch (batteryPowerMode) {
                        case EMSH_PWR_MODE_STANDBY:
                            MLog.e("standby status");
                            MyApp.getMyApp().getIdataLib().powerOn();
                            break;
                        case EMSH_PWR_MODE_DSG_UHF:
                            MLog.e("DSG_UHF status");
                            MUtil.show(R.string.poweron_success);
                            clearFilter();
                            break;
                        case EMSH_PWR_MODE_CHG_GENERAL:
                        case EMSH_PWR_MODE_CHG_QUICK:
                            MLog.e("charging status");
                            MUtil.show(R.string.charing);
                            break;
                        case EMSH_PWR_MODE_CHG_FULL:
                            MLog.e("charging full status");
                            MUtil.show(R.string.charing_full);
                            break;
                    }
                } else {
                    oldStatue = EMSH_PWR_MODE_BATTERY_ERROR;
                    MLog.e("unknown status");
                    MUtil.warningDialog(MainActivity.this);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!ifRequesetPermission){
            recyleResoure();
        }
    }

    /**
     * 返回键值捕获
     */
    @Override
    public void onBackPressed() {
        // showExitInventory();
    }

    private QMUIDialog qmUiTipDialog;

    /**退出盘点**/
    public void showExitInventory(){
        qmUiTipDialog = new QMUIDialog.MessageDialogBuilder(this)
                .setMessage(getString(R.string.exit_inventory))
                .addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        qmUiTipDialog = null;
                    }
                })
                .addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        qmUiTipDialog = null;
                        if (!ifRequesetPermission){
                            recyleResoure();
                        }

                    }
                }).show();
    }


}