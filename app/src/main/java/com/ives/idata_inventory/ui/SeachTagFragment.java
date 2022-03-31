package com.ives.idata_inventory.ui;

import android.os.Bundle;
import android.os.IScanListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.iscandemo.iScanInterface;
import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;
import com.ives.idata_inventory.dao.GlobalUtil;
import com.ives.idata_inventory.rf_mode.BackResult;
import com.ives.idata_inventory.rf_mode.GetRFIDThread;
import com.ives.idata_inventory.rf_mode.MyApp;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.ives.idata_inventory.util.ButtonOnCilk;
import com.ives.idata_inventory.util.FileUtil;
import com.ives.idata_inventory.util.FindBar;


public class SeachTagFragment extends Fragment implements BackResult, OnKeyListener {

    public SeachTagFragment() {
        // Required empty public constructor
    }




    Button buttonCancel = null;
    EditText editText = null;
    FindBar findBar = null;
    private View findBarView;
    private String tag;

    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        GetRFIDThread.getInstance().setBackResult(this);
        GetRFIDThread.getInstance().setSearchTag(true);
    }

    //定义接口操作对象
    private iScanInterface miScanInterface ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findBarView = inflater.inflate(R.layout.fragment_seach_tag, container, false);
        //tagId =  getArguments().getString("id");;
        editText = findBarView.findViewById(R.id.editText);

        if (GlobalUtil.workId ==2){
            //初始化接口对象
            miScanInterface = new iScanInterface(getContext());
            //注册数据回调接口
            miScanInterface.registerScan(scanResltListener);
            miScanInterface.open();
        }else{
            editText.setEnabled(false);
            editText.setText(FileUtil.tagId);
        }

        tag = FileUtil.getTagID(String.format("%s%s","[@",FileUtil.tagId));
        findBar = findBarView.findViewById(R.id.findBar);
        buttonCancel = findBarView.findViewById(R.id.button);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ButtonOnCilk.isFastViewClick(buttonCancel)){
                    if (GlobalUtil.workId == 2){
                        mainActivity.setCurrentPage(1);
                    }else{
                        mainActivity.setCurrentPage(2);
                    }
                }
            }
        });
        return findBarView;
    }
    //定义数据回调接口
    private IScanListener scanResltListener = new IScanListener(){
        /**
         * @param data 条码数据
         * @param type 条码类型
         * @param decodeTime 解码耗时
         * @param keyDownTime 按键时间
         * @param imagePath 图片路径，注意：默认图片输出为关闭状态；
         *                                  如果需要条码图片需要打开图片输出选项。
         */
        @Override
        public void onScanResults(String data, final  int type, long decodeTime, long keyDownTime, String imagePath) {
//            Log.d(TAG, "onScanResults: data="+data);
//            Log.d(TAG, "onScanResults: type="+type);
//            Log.d(TAG, "onScanResults: decodeTime="+decodeTime);
//            Log.d(TAG, "onScanResults: keyDownTime="+keyDownTime);
//            Log.d(TAG, "onScanResults: imagePath="+imagePath);

//            final String result="data:"+data+"\r\n"
//                    +"type:"+type+"\n"
//                    +"decodeTime:"+decodeTime+"\n"
//                    +"keyDownTime:"+keyDownTime+"\n"
//                    +"imagePath:"+imagePath+"\n";
            if (type !=0){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.setText(data);
                        tag = FileUtil.getTagID(String.format("%s%s","[@",data));
                    }
                });
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (GlobalUtil.workId==2){
            //应用关闭后注销回调
            miScanInterface.unregisterScan(scanResltListener);
            GlobalUtil.workId= 0;
        }
        //inventory_data = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearFilter();
        GetRFIDThread.getInstance().setSearchTag(false);
        editText.setText("");

    }

    private void filter() {
            boolean status = MyApp.getMyApp().getIdataLib().filterSet
                    (MyApp.UHF[0], 32, tag.length()*4, tag, false);
            if (status) {
                //定频915MHZ,定功率25
                MyApp.getMyApp().getIdataLib().powerSet(25);
                MyApp.getMyApp().getIdataLib().frequencyModeSet(3);
                //MUtil.show(R.string.success);
            } else {
               // MUtil.show(R.string.failed);
            }

    }

    private void clearFilter() {
        boolean status = MyApp.getMyApp().getIdataLib().filterSet
                (MyApp.UHF[0], 0, 0, tag, false);
        if (status) {
           // MUtil.show(R.string.clean_success);
            //重置功率为30，区域频率为美国
            MyApp.getMyApp().getIdataLib().powerSet(30);
            MyApp.getMyApp().getIdataLib().frequencyModeSet(4);
        } else {
            //MUtil.show(R.string.failed);
        }
    }

    private short maxValue = -29, minValue = -70; //RSSI的最大值和最小值


    @Override
    public void postResult(String[] tagData) {
        if (tagData != null) {
            String epc = tagData[1];
            final String rssiStr = tagData[2]; //为负数的字符串
            int floatBit = Integer.parseInt(rssiStr.substring(rssiStr.length() - 1, rssiStr.length())); // 小数位的值
            int intgerBit = Integer.parseInt(rssiStr.substring(0, rssiStr.length() - 2)); // 整数位的值
            if (floatBit > 5) { //四舍五入操作
                --intgerBit;
            } else {
                ++intgerBit;
            }
            int rssi = intgerBit;
            // int length = maxValue - minValue; //取差值
            if (rssi >= maxValue) {
                rssi = maxValue;
            } else if (rssi <= minValue) {
                rssi = minValue;
            }
            rssi -= (minValue - 1);
            final int finalRssi = rssi;
            if (!TextUtils.isEmpty(editText.getText().toString())){
                mainActivity.soundPoolHelper.playLooking("happy1",false,(finalRssi/100f));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findBar.set(finalRssi / 50f);
                    }
                });
            }

        }
    }

    @Override
    public void postInventoryRate(long rate) {

    }

    int isReady = 0;
    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_F8 :
                isReady = 0;
                filter();
                startOrStopRFID();
                break;
            case KeyEvent.KEYCODE_BACK :
                if (ButtonOnCilk.isFastClick()){
                    if (GlobalUtil.workId==2){
                        //应用关闭后注销回调
                        mainActivity.setCurrentPage(1);
                    }else{
                        mainActivity.setCurrentPage(2);

                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_F8 :
                if (isReady == 0) {
                    isReady = 1;
                    startOrStopRFID();
                }
                break;
            default:
                break;
        }
    }

    //开启或停止RFID模块
    public void startOrStopRFID() {
        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
        if (flag) {
            MyApp.getMyApp().getIdataLib().startInventoryTag();
        } else {
            MyApp.getMyApp().getIdataLib().stopInventory();
        }
        GetRFIDThread.getInstance().setIfPostMsg(flag);
    }
}