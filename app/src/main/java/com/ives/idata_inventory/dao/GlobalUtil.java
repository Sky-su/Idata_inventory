package com.ives.idata_inventory.dao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GlobalUtil {

    // 1：在线 2：离线
    public static int inventoryStore = 0;


    public static int workId = 0;
    //用户
    public static String userName = null;
    public static int userType = -1;
    public static int userId = -1;

    public static void delLogin(){
         userName = null;
         userType = -1;
         userId = -1;
    }
    //登录
    public static String login ="/api/v1/rbac/user/login";

    //获取未完成盘点单
    public static String unfinishedInventory = "/api/v1/inventory/zcmgpandian/pandianlist";
    //根据单号获取详情
    public static String inventoryData = "/api/v1/inventory/zcmgpandianliebiao/edit/";
    //在线导入
    public static String onlineFinished = "/api/v1/inventory/item/zaixianimport";
    /**
     * 网络地址
     * @param context
     * @return
     */
    public static String getURL(Context context) {
        //String result = "http://172.19.10.57";
        //String result = "http://192.168.0.119";
        String result = "http://192.168.0.132:7022";
        SharedPreferences sharedPreferences= context.getSharedPreferences("networkurl", MODE_PRIVATE);
        String url = sharedPreferences.getString("ipaddress","");
        if (!"".equals(url)){
            result = url;
        }
        return result ;
    }
    public static void SetURL(Context context, String data) {
        SharedPreferences sharedPref = context.getSharedPreferences("networkurl",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("ipaddress",data);
        prefEditor.commit();
    }
    public static void setUpdateTime(Context context, String data) {
        SharedPreferences sharedPref = context.getSharedPreferences("updateTime",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("time",data);
        prefEditor.commit();
    }
    public static String getUpdate(Context context) {
        String result = "15";
        SharedPreferences sharedPreferences= context.getSharedPreferences("updateTime", MODE_PRIVATE);
        String url = sharedPreferences.getString("time","");
        if (!"".equals(url)){
            result = url;
        }
        return result ;
    }
    /**
     * 设备号
     * @param context
     * @return
     */
    public static String getDevice(Context context) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("device", MODE_PRIVATE);
        String url = sharedPreferences.getString("id","");

        //测试
        if (!"".equals(url)){
            return url;
        }
        return "" ;

        //return url ;
    }


    /**
     * 授权
     * @param context
     * @param data
     */
    public static void setAuthorization(Context context,String data) {
        SharedPreferences sharedPref = context.getSharedPreferences("Authorization",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("AuthorizationState",data);
        //prefEditor.putString("AuthorizationTime",data);
        prefEditor.commit();
    }
    public static String getAuthorization(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Authorization", MODE_PRIVATE);
        return sp.getString("AuthorizationState", "") ;
    }
    public static void SetDevice(Context context, String data) {
        SharedPreferences sharedPref = context.getSharedPreferences("device",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("id",data);
        prefEditor.commit();
    }

    /**
     * 密码
     * @param context
     * @return
     */
    public static String getPWD(Context context) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("PWD", MODE_PRIVATE);
        String url = sharedPreferences.getString("id","");
        if (!"".equals(url)){
            return url;
        }
        return "654321" ;
    }


    public static void setPWD(Context context, String data) {
        SharedPreferences sharedPref = context.getSharedPreferences("PWD",MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("id",data);
        prefEditor.commit();
    }
    //扫描枪ID
    public static String ScanId = "";
    /**
     * 读Internal中文件的方法
     *
     * @param filePathName 文件路径及文件名
     * @return 读出的字符串
     * @throws IOException
     */
    public static String readInternal(String filePathName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        // 打开文件输入流
        FileInputStream fileInputStream = new FileInputStream(filePathName);
        byte[] buffer = new byte[1024];
        int len = fileInputStream.read(buffer);
        // 读取文件内容
        while (len > 0) {
            stringBuffer.append(new String(buffer, 0, len));
            // 继续将数据放到buffer中
            len = fileInputStream.read(buffer);
        }
        // 关闭输入流
        fileInputStream.close();
        //Log.d("jjjj",stringBuffer.toString());
        return stringBuffer.toString();
    }

    //ASCII转化
    public static String hexToAscii(String str) {
        StringBuilder sb = new StringBuilder();
        if (!str.isEmpty()){
            for (int i = 0; i < str.length(); i += 2) {
                sb.append((char) Integer.parseInt(str.substring(i, i + 2), 16));
            }
        }
        return sb.toString();
    }
    public static String getTagASCII(String data) {
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            sb.append(Integer.toHexString((int) c));
        }
        return sb.toString();
    }
    /**
     * 后@
     * @param str
     * @param strLength
     * @return
     */
    public static String addFFForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
           // sb.append("0").append(str);// 左补0
            sb.append(str).append("@");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    /**
     * 获取当前时间
     * 可根据需要自行截取数据显示 // yyyy-MM-dd HH:mm
     * @return
     */
    public static String getTimes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static List convertStringToList(String str, String mark){
        String[] strArray = str.split(mark);
        List list= Arrays.asList(strArray);
        return list;
    }
    /**
     *
     * 斑马扫描Scan
     */
    public void setScan(boolean is, Context context){
        Intent dwIntent = new Intent();
        dwIntent.setAction("com.symbol.datawedge.api.ACTION");
//  Enable
        if (is) {
            dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
        }
//  or Disable
        if (!is) {
            dwIntent.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "DISABLE_PLUGIN");
        }
        context.sendBroadcast(dwIntent);
    }
}
