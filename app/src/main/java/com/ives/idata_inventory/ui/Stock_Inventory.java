package com.ives.idata_inventory.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IScanListener;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.alibaba.fastjson.JSONObject;
import com.ives.idata_inventory.MainActivity;
import com.ives.idata_inventory.R;

import com.ives.idata_inventory.adapter.CommonDialog;

import com.ives.idata_inventory.adapter.Item_inventory_adapter;
import com.ives.idata_inventory.dao.GlobalUtil;
import com.ives.idata_inventory.entity.Stock;
import com.ives.idata_inventory.rf_mode.BackResult;
import com.ives.idata_inventory.rf_mode.GetRFIDThread;
import com.ives.idata_inventory.rf_mode.MyApp;
import com.ives.idata_inventory.rf_mode.OnKeyListener;
import com.example.iscandemo.iScanInterface;
import com.ives.idata_inventory.util.ButtonOnCilk;
import com.ives.idata_inventory.util.CustomToast;
import com.ives.idata_inventory.util.FileUtil;
import com.ives.idata_inventory.util.MyLOg;
import com.ives.idata_inventory.util.TextUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Stock_Inventory extends Fragment implements BackResult, OnKeyListener {


    private View mView;
    private Button buttonSet;
    private Button buttonExport;
    private ListView listView;
    private TextView total,finishInventory;
    private TextView areaTotal,areaFinishInventory;
    private TextView departmentText,areaText;
    private Item_inventory_adapter item_inventory;
    private List<Stock> inventory_data;
    private MainActivity mainActivity;

    private Map<Integer,String> title ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        GetRFIDThread.getInstance().setBackResult(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_take, container, false);
        //初始化接口对象
        miScanInterface = new iScanInterface(getContext());
        //注册数据回调接口
        miScanInterface.registerScan(scanResltListener);
        miScanInterface.open();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
        //urlIp = GlobalUtil.getURL(getContext());
    }
    /**
     * 实例化
     */
    boolean isStarted = false;
    private void initialize(View view){
        //显示
        total = view.findViewById(R.id.total);
        finishInventory = view.findViewById(R.id.finishinventory);
        departmentText = view.findViewById(R.id.departmentHit);
        //   unInventory = findViewById(R.id.unInventory);
        areaTotal = view.findViewById(R.id.areaTotal);
        areaFinishInventory = view.findViewById(R.id.areaInventory);
        // areaUnInventory = findViewById(R.id.areaUnInventory);
        buttonSet = view.findViewById(R.id.buttonInventorySet);
        buttonExport = view.findViewById(R.id.buttonInventoryExport);
        listView = view.findViewById(R.id.findInventoryListView);
        areaText = view.findViewById(R.id.AreaHit);
        departmentText.setText(String.format("%s %s",getString(R.string.departmentHit),getString(R.string.all)));
        areaText.setText(String.format("%s %s",getString(R.string.Area),getString(R.string.all)));
        listView.setDividerHeight(0);
        listView.setDivider(null);
        areaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ButtonOnCilk.isFastViewClick(areaText) && ButtonOnCilk.isFastViewClick(departmentText)){
                    mHandler.sendEmptyMessage(0x13);
                }
            }
        });
        departmentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ButtonOnCilk.isFastViewClick(areaText) && ButtonOnCilk.isFastViewClick(departmentText)){
                    mHandler.sendEmptyMessage(0x16);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                Stock item = (Stock) arg0.getItemAtPosition(arg2);
                if (!TextUtils.isEmpty(item.getStockId())){
                    FileUtil.tagId = item.getStockId();
                    mainActivity.setCurrentPage(4);
                }
                return true;
            }


        });
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ButtonOnCilk.isFastViewClick(buttonSet)){
                    showExitInventory();
                    //mHandler.sendEmptyMessage(0x17);
                }
            }
        });
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ButtonOnCilk.isFastViewClick(buttonExport)){
                    showExportInventory();
                }
            }
        });
    }
    private List<Map<String, String>> temporaryData;
    //扫描
    private static final String PROFILE1 = "Sacnner" ;
    IntentFilter filter ;
    @Override
    public void onResume() {
        super.onResume();
        if (inventory_data == null || inventory_data.size() == 0) {
            CustomToast.showLoad(getContext(), getString(R.string.load));
            mHandler.sendEmptyMessage(0x14);
        }else{
            total.setText(String.valueOf(inventory_data.size()));
            areaText.setText(String.format("%s %s",getString(R.string.Area),areaId.toString().substring(1,areaId.toString().length()-1)));
            departmentText.setText(String.format("%s %s",getString(R.string.departmentHit),department.toString().substring(1,department.toString().length()-1)));
            showInventoryData();
        }
    }





    //读取Excel数据
    private void sendData(){
        areaId = new HashSet<>();
        department = new HashSet<>();
        for (Map<String, String> map : temporaryData){
            Stock stock = new Stock();
            stock.setStockId(map.get(title.get(0)));
            stock.setErpId(map.get(title.get(1)));
            stock.setStockName(map.get(title.get(2)));
            stock.setBrand(map.get(title.get(3)));
            stock.setSpecification(map.get(title.get(4)));
            stock.setMF(map.get(title.get(5)));
            stock.setPreserver(map.get(title.get(6)));
            stock.setDepartment(map.get(title.get(7)));
            stock.setDescription(map.get(title.get(8)));
            areaId.add(stock.getMF());
            department.add(stock.getDepartment());
            inventory_data.add(stock);
        }
    }
    //读取临时表数据
    private void sendTempData(){
        for (Map<String, String> map : temporaryData){
            Stock stock = new Stock();
            stock.setStockId(map.get(title.get(0)));
            stock.setErpId(map.get(title.get(1)));
            stock.setStockName(map.get(title.get(2)));
            stock.setBrand(map.get(title.get(3)));
            stock.setSpecification(map.get(title.get(4)));
            stock.setMF(map.get(title.get(5)));
            stock.setPreserver(map.get(title.get(6)));
            stock.setDepartment(map.get(title.get(7)));
            stock.setDescription(map.get(title.get(8)));
            if(map.get(title.get(9)).equals("未盘")){
                stock.setInventoryStatus(0);
            }else{
                stock.setInventoryStatus(1);
            }
            stock.setDescription(title.get(10));
            stock.setInventoryTime(title.get(11));
            inventory_data.add(stock);
        }
    }

    private QMUIDialog.MultiCheckableDialogBuilder builder ;
    private Set<String> areaId = null;
    List<Stock> it = null;

    //区域选择
    public void areaSelected(final String [] items){
        builder = new QMUIDialog.MultiCheckableDialogBuilder(getContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            areaId.clear();
                            it.clear();
                            for (Stock stock :inventory_data){
                                if (stock.getInventoryStatus()==0){
                                    areaId.add(stock.getMF());
                                }
                            }
                            showInventoryData();
                            areaText.setText(String.format("%s %s",getString(R.string.Area),getString(R.string.all)));
                            item_inventory = new Item_inventory_adapter(it,getContext(),title);
                            listView.setAdapter(item_inventory);
                            dialog.dismiss();
                        }
                    }
                });
        builder.addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                areaId = new HashSet<>();
                it = new ArrayList<>();
                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                    areaId.add(items[builder.getCheckedItemIndexes()[i]]);
                }
                dialog.dismiss();
                areaText.setText(String.format("%s %s",getString(R.string.Area),areaId.toString().substring(1,areaId.toString().length()-1)));
               // updateInventory(it);
                showInventoryData();
            }
        });
        builder.setTitle(String.format("选择%s",getString(R.string.Area)));
        builder.show();
    }
    //部门
    private Set<String> department;
    public void departmentSelected(final String [] items){
        builder = new QMUIDialog.MultiCheckableDialogBuilder(getContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            department = new HashSet<>();
                            it = new ArrayList<>();
                            for (Stock stock : inventory_data){
                                if (stock.getInventoryStatus() == 0){
                                    department.add(stock.getDepartment());
                                }
                            }
                            showInventoryData();
                            //updateInventory(it);
                            departmentText.setText(String.format("%s %s",getString(R.string.Area),getString(R.string.all)));
                            dialog.dismiss();
                        }
                    }
                });
        builder.addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                department = new HashSet<>();
                it = new ArrayList<>();
                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                    department.add(items[builder.getCheckedItemIndexes()[i]]);
                }
                dialog.dismiss();
                departmentText.setText(String.format("%s %s",getString(R.string.departmentHit),department.toString().substring(1,department.toString().length()-1)));
                showInventoryData();
               // updateInventory(it);

            }
        });
        builder.setTitle(String.format("选择%s",getString(R.string.departmentHit)));
        builder.show();
    }

    private  boolean isNext = true;
    private int battery  ;
    //Error
    private String errorMessage = null;
    private String errorName = null;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            List<String> objects;
            switch (msg.what) {
                case 0x12:
                    MyLOg.e(errorName,errorMessage);
                    break;
                case 0x13:
                    objects = new ArrayList<String>();
                    for (Stock s:inventory_data) {
                        if (s.getInventoryStatus() == 0 && !objects.contains(s.getMF())){
                            objects.add(s.getMF());
                        }
                    }
                    objects.add(0,getString(R.string.all));
                    areaSelected(objects.toArray(new String[objects.size()]));
                    break;
                case 0x14:
                    if (inventory_data == null){
                        boolean isFile = false;
                        inventory_data = new ArrayList<Stock>();
                        //title = new String[]{"资产编号","ERP编号","资产名称","品牌","规格","存放位置","保管人","资产说明"};
                        title = new LinkedHashMap<Integer, String>();
                        if (FileUtil.tempFile(getContext())){
                            isFile = true;
                            temporaryData = FileUtil.readSerial(8,FileUtil.tempFileInName);
                        }else{
                            temporaryData = FileUtil.readSerial(8,FileUtil.appFolderImportName+"/inventory_import.xlsx");
                        }
                        if (temporaryData !=null){
                            //遍历map中的键
                            int i =0;
                            for (String key : temporaryData.get(0).keySet()) {
                                title.put(i,key) ;
                                i++;
                            }
                            if (!isFile){
                                sendData();
                            }else{
                                sendTempData();
                            }
                            if (inventory_data.size()>0){
                                areaId = new HashSet<>();
                                department = new HashSet<>();
                                for (Stock s:inventory_data) {
                                    if (s.getInventoryStatus() == 0){
                                        areaId.add(s.getMF());
                                        department.add(s.getDepartment());
                                    }
                                }
                                total.setText(String.valueOf(inventory_data.size()));
                                it = new ArrayList<>(inventory_data);
                                item_inventory =new Item_inventory_adapter(it,getContext(),title);
                                listView.setAdapter(item_inventory);
                                showInventoryData();
                                //Area
//                                    areaTotal.setText(String.valueOf(inventory_data.size()));
//                                    String un = String.valueOf(2986);
//                                    areaFinishInventory.setText(TextUtil.showTextStyle("14/"+un,0,2,getResources().getColor(R.color.colorREAD)));
//                                    finishInventory.setText(TextUtil.showTextStyle("14/"+un,0,2,getResources().getColor(R.color.colorREAD)));
////                                    areaText.setText(String.format("%s %s",getString(R.string.Area),areaId.toString()));
//                                    departmentText.setText(String.format("%s %s",getString(R.string.departmentHit),department.toString()));
//
                            }
                        }else{
                            isNext = false;
                            showTip(getString(R.string.choose_file),true);
                        }
                        CustomToast.showDismiss();
                    }
                    break;
                case 0x15:
                    addAllData(rfData);
                    rfData.clear();
                    break;
                case 0x16:
                    objects = new ArrayList<String>();
                    for (Stock s:inventory_data) {
                        if (s.getInventoryStatus() == 0 && !objects.contains(s.getDepartment())){
                            objects.add(s.getDepartment());
                        }
                    }
                    objects.add(0,getString(R.string.all));
                    departmentSelected(objects.toArray(new String[objects.size()]));
                    break;
                case 0x17:
                    CustomToast.battery(getContext(),String.valueOf(battery));
                    String[] zt = new String[]{title.get(0),title.get(1),title.get(2),title.get(3),title.get(4),title.get(5),title.get(6),title.get(7),title.get(8),"盘点结果","说明","盘点时间"};
                    String sheetName = "盘点详情表";
                    FileUtil.writeExcel(zt,FileUtil.tempFileInName,sheetName,inventory_data);
                    break;

                case 0x18:
                    if (!addData(scanData)){
                        CustomToast.showLoad(getContext(),"已盘或不存在的资产",3000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //提示
    public void showTip(String name, final boolean isClose) {
        t_dialog = new QMUIDialog.MessageDialogBuilder(getContext())
                .setMessage(name)
                .addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        t_dialog = null;
                        if (isClose){
                            inventory_data = null;
                            mainActivity.setCurrentPage(0);
                        }
                    }
                }).show();
    }
    /**退出盘点**/
    public void showExitInventory() {
        t_dialog = new QMUIDialog.MessageDialogBuilder(getContext())
                .setMessage(getString(R.string.exit_inventory))
                .addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        t_dialog = null;
                    }
                })
                .addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        t_dialog = null;
                        inventory_data = null;
                        mainActivity.setCurrentPage(1);
                    }
                }).show();
    }

    /**导出盘点提示**/
    private QMUIDialog t_dialog;
    public void showExportInventory(){
        t_dialog = new QMUIDialog.MessageDialogBuilder(getContext())
                .setMessage(getString(R.string.name_export))
                .addAction(getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        t_dialog = null;
                    }
                }).addAction(getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        Collections.sort(inventory_data, new Comparator<Stock>() {
                            @Override
                            public int compare(Stock o1, Stock o2) {
                                return Integer.compare(o2.getInventoryStatus(), o1.getInventoryStatus());
                            }
                        });
                        exportInventoryData(inventory_data);
                        dialog.dismiss();
                    }
                }).show();
        t_dialog.setCancelable(false);
    }
    /**
     *盘点结果导出
     * @param list 数据
     *资产编号	ERP编号	资产名称	品牌	规格	存放位置	保管人 部门	资产说明	盘点结果	盘点时间
     */
    private void exportInventoryData(List<Stock> list) {
        String[] zt = new String[]{title.get(0),title.get(1),title.get(2),title.get(3),title.get(4),title.get(5),title.get(6),title.get(7),title.get(8),"盘点结果","说明","盘点时间"};
        String sheetName = "盘点详情表";
        boolean is = FileUtil.writeExcel(zt,String.format("%s%s%s%s",FileUtil.appFolderOutName,"/",FileUtil.getTimes(),"inventoryOut.xls"),sheetName,list);
        if (is){
            FileUtil.deleteSingleFile(FileUtil.tempFileInName);
            mainActivity.setCurrentPage(0);
            CustomToast.showLoad(getContext(),getString(R.string.inventory_success) + String.format("%s%s%s%s",FileUtil.appFolderOutName,"/",FileUtil.getTimes(),"inventoryOut.xls"),3000);
        }else{
            CustomToast.showLoad(getContext(), String.format("%s%s%s",getString(R.string.inventory_success),String.format("%s%s%s",FileUtil.appFolderOutName,FileUtil.getTimes(),"inventoryOut.xls"),getString(R.string.fails)),5000);
        }
    }

    private void addAllData(Set<String> rfData) {
        List<Stock> countData = new ArrayList<Stock>();
        for (String count:rfData) {
            Iterator<Stock> areaSelected = inventory_data.iterator();
            while(areaSelected.hasNext()) {
                Stock st = areaSelected.next();
                if (st != null && count.equals(st.getStockId()) && st.getInventoryStatus() == 0){
                    countData.add(st);
                    areaSelected.remove();
                    break;
                }
            }
        }

        if (countData.size() > 0) {
            for (Stock p:countData) {
                if (areaId.contains(p.getMF()) && department.contains(p.getDepartment())){
                    inventory_data.remove(p);
                    p.setInventoryStatus(1);
                    p.setInventoryTime(FileUtil.getTimes());
                    inventory_data.add(p);
                    item_inventory.soundPool();
                }else if (p.getInventoryStatus()==0 && p.getStatus()==-1){
                    if (dialogSet == null){
                        dialogSet = new ArrayList<>();
                    }
                    dialogSet.add(p);
                    if (dialog==null || !dialog.isShowing()){
                        item_inventory.soundPool();
                        modeDialog(p);
                    }
                }
            }
            showInventoryData();
           // updateInventory(it);
        }
    }


    /**
     *
     * 与数据盘点
     * isType true 指定   false 一般
     *  Type   0:未盘 1：已盘
     **/
    private List<Stock> dialogSet ;

    private boolean addData(String rfData) {
        boolean flag = false;
        Iterator<Stock> areaSelected = it.iterator();
        Stock stock = null;
        while(areaSelected.hasNext()) {
            Stock st = areaSelected.next();
            if (st != null && rfData.equals(st.getStockId()) && st.getInventoryStatus() == 0){
                stock = st;
                areaSelected.remove();
                flag = true;
                break;
            }
        }
        if (stock != null) {
            inventory_data.remove(stock);
            stock.setInventoryStatus(1);
            if (stock.getNewMf().equals("")){
                stock.setNewMf("二维码");
            }else{
                stock.setNewMf(String.format("%s%s",stock.getMF(),"二维码"));
            }
            stock.setInventoryTime(FileUtil.getTimes());
            inventory_data.add(stock);
            showInventoryData();
            item_inventory.soundPool();
            //updateInventory(it);
        }else{
            for (Stock s:inventory_data) {
                if (s.getStockId().equals(rfData) && s.getInventoryStatus()==0 && s.getStatus()==-1){
                    item_inventory.soundPool();
                    flag = true;
                    if (dialogSet == null){
                        dialogSet = new ArrayList<>();
                    }
                    boolean success = false;
                    for (Stock item :dialogSet){
                        if (rfData.equals(item.getStockId())){
                            success = true;
                            break;
                        }
                    }
                    if (!success){
                        dialogSet.add(s);
                    }
                    if (dialog==null || !dialog.isShowing()){
                        modeDialog(s);
                    }
                    break;
                }
            }
        }
        return flag;
    }

    CommonDialog dialog = null;
    //mode dialog
    public void modeDialog(Stock date){
        dialog = new CommonDialog(getContext(),date,title, new CommonDialog.ClickCallBack() {
            @Override
            public void onConfirm() {
                inventory_data.remove(dialog.stock);
                dialog.stock.setStatus(1);
                dialog.stock.setInventoryStatus(1);
                dialog.stock.setInventoryTime(FileUtil.getTimes());
                dialog.stock.setNewMf(String.format("%s\n%s",departmentText.getText().toString(),areaText.getText().toString()));
                inventory_data.add(dialog.stock);
                showInventoryData();
                if (dialogSet.size()>1){
                    dialogSet.remove(0);
                    dialog.showDialog(dialogSet.get(0));
                    item_inventory.soundPool();
                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancel() {
                inventory_data.remove(dialog.stock);
                dialog.stock.setStatus(1);
                dialog.stock.setInventoryTime(FileUtil.getTimes());
                inventory_data.add(dialog.stock);
                if (dialogSet.size()>0){
                    dialogSet.remove(0);
                    if (dialogSet.size()>0){
                        dialog.showDialog(dialogSet.get(0));
                    }
                }else{
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        //dialog.setCancelable(false);
    }

    private void showInventoryData(){
        int i = 0;
        int j = 0;
        it.clear();
        for (Stock s:inventory_data) {
            if (s.getInventoryStatus()!=0){
                i++;
            }
            if (areaId.contains(s.getMF()) && department.contains(s.getDepartment())){
                if (s.getInventoryStatus() == 0){
                    it.add(s);
                }
                j++;
            }
        }
        item_inventory = new Item_inventory_adapter(it,getContext(),title);
        listView.setAdapter(item_inventory);
        String un = String.valueOf(inventory_data.size()-i);
        finishInventory.setText(TextUtil.showTextStyle(String.format("%s%s%s",i,"/",un),0,String.valueOf(i).length(),getResources().getColor(R.color.colorREAD)));
        //Area
        areaTotal.setText(String.valueOf(j));
        areaFinishInventory.setText(TextUtil.showTextStyle(String.format("%s%s%s",j-it.size(),"/",it.size()),0,String.valueOf(j-it.size()).length(),getResources().getColor(R.color.colorREAD)));

    }

//    private void updateInventory(List<Stock> op){
//        item_inventory = new Item_inventory_adapter(op,getContext(),title);
//        listView.setAdapter(item_inventory);
//    }
    private Set<String> rfData = new HashSet<>();

    @Override
    public void postResult(String[] tagData) {

        String str = tagData[1]; //拿到EPC
        if (str != null) {
            //MyApp.getMyApp().playSound();
            Log.d("tag", str);
            rfData.add(FileUtil.hexToAscii(str).substring(2));
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
                    startOrStopRFID();
                Log.d("onKeyUp", String.valueOf(1));
                mHandler.sendEmptyMessage(0x15);
                break;
            case KeyEvent.KEYCODE_BACK :
                if (ButtonOnCilk.isFastClick()){
                   showExitInventory();
                }
                break;
            default:
                break;
        }
//        if (keyCode == KeyEvent.KEYCODE_F8) {
//            Log.d("onKeyUp", String.valueOf(1));
//            startOrStopRFID();
//            handler.sendEmptyMessage(0x15);
//        }
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_F8 :
                if (isReady == 0) {
                    isReady = 1;
                    Log.d("onKeyDown", String.valueOf(2));
                    startOrStopRFID();
                }
                break;
            case KeyEvent.KEYCODE_BUTTON_1:
                miScanInterface.scan_start();
                break;
            default:
                break;
        }
//        if (keyCode == KeyEvent.KEYCODE_F8) {
//            Log.d("onKeyDown", String.valueOf(2));
//            startOrStopRFID();
//        }
    }

    //定义接口操作对象
    private iScanInterface miScanInterface ;


    private String scanData = "";
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
                scanData = data;
                mHandler.sendEmptyMessage(0x18);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        //应用关闭后注销回调
        miScanInterface.unregisterScan(scanResltListener);
        //inventory_data = null;
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
