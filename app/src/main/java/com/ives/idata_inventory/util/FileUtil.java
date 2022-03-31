package com.ives.idata_inventory.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;


import com.ives.idata_inventory.entity.Stock;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Theory
 */
public class FileUtil {


    /**
     * APP文件夹地址
     */
    public static String appFolderImportName = Environment.getExternalStorageDirectory().getPath ()+"/IVESApp/import";
    public static String appFolderOutName = Environment.getExternalStorageDirectory().getPath ()+"/IVESApp/export";


    //盘点文件路径
    public static String tempFileInName = Environment.getExternalStorageDirectory ().getPath ()+"/IVESApp/temp.xls";
    public static String inventoryFileOutName = Environment.getExternalStorageDirectory ().getPath ()+"/IVESApp/export/"+getTimes()+"inventoryOut.xls";


    //生成盘点文件夹
    static File receiveFile = null;
    static File exportFile = null;
    public static void checkFile(Context context){
        receiveFile = new File(appFolderImportName);
        exportFile = new File(appFolderOutName);
        if (!receiveFile.exists()) {
            receiveFile.mkdirs();
        }
        if (!exportFile.exists()) {
            exportFile.mkdirs();
        }
        copyFilesFromAssets(context,appFolderImportName);
    }

    //判断临时表
    public static boolean tempFile(Context context){
        receiveFile = new File(tempFileInName);
        return receiveFile.exists();

    }
    public static boolean delTempFile(Context context){
        receiveFile = new File(tempFileInName);
        return receiveFile.exists();

    }


    public static String tagId = "";
    //ASCII转化
    public static String hexToAscii(String str) {
        StringBuilder sb = new StringBuilder();
        if (!str.isEmpty()){
            for (int i = 0; i < str.length(); i += 2) {
                int c = Integer.parseInt(str.substring(i, i + 2), 16);
                if (c>32&&c<123){
                    sb.append((char) c);
                }else{
                    return "'"+str+"'";
                }
            }
        }
        return sb.toString();
    }


    public static String getTagID(String data) {
        // str = "@" + tagId;
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            sb.append(Integer.toHexString((int) c));
        }
        return sb.toString();
    }
    /**
     * 获取当前时间
     * 可根据需要自行截取数据显示 // yyyy-MM-dd HH:mm
     * @return
     */

    public static String getTimes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH点mm分", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(Context context,String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    //
                    if (file.getName().contains("RFID厂家编码")){
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }



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
        return stringBuffer.toString();
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.d("失败","删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e("失败","删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }

    public static void copyFilesFromAssets(Context context, String filePath) {
        File jhPath=new File(filePath +"/help.pdf");
        //查看该文件是否存在
        if(jhPath.exists()){
            Log.e("test", "该文件已存在");
        }else{
            //不存在先创建文件夹
//            File path=new File(filePath);
//            Log.e("test", "pathStr="+path);
//            if (path.mkdir()){
//               // MyLOg.e("filePath", "创建成功");
//            }else{
//               // MyLOg.e("filePath", "创建失败");
//            };
            //创建失败
            try {
                //得到资源
                AssetManager am= context.getAssets();
                //得到该文件的输入流
                InputStream is=am.open("help.pdf");
                Log.e("test", is+"");
                //用输出流写到特定路径下
                FileOutputStream fos=new FileOutputStream(jhPath);
              //  Log.e("test", "fos="+fos);
               // Log.e("test", "jhPath="+jhPath);
                //创建byte数组  用于1KB写一次
                byte[] buffer=new byte[1024];
                int count = 0;
                while((count = is.read(buffer))>0){
                    fos.write(buffer,0,count);
                }
                //最后关闭流
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(Context context,String oldPath,String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
           // MainActivity.handler.sendEmptyMessage(COPY_FALSE);
        }
    }

    /**
     * 复制文件
     * @param oldFilePath
     * @param newFilePath
     * @return
     * @throws IOException
     */
    public static boolean fileCopy(String oldFilePath,String newFilePath) throws IOException {


        //如果原文件不存在

        if(fileExists(oldFilePath) == false){

            return false;

        }


        //获得原文件流

        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

        byte[] data = new byte[1024];


        //输出流

        FileOutputStream outputStream =new FileOutputStream(new File(newFilePath));


        //开始处理流

        while (inputStream.read(data) != -1) {

            outputStream.write(data);

        }

        inputStream.close();

        outputStream.close();

        return true;

    }

    public static boolean fileExists(String filePath) {

        File file = new File(filePath);

        return file.exists();

    }


    /**
     * 创建表格
     * @param title
     * @param filePath
     * @param objList
     */
    public static boolean writeExcel(String[] title,String filePath,String sheetName ,List<Stock> objList){
        boolean exportFile = false;
        //新建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //新建sheet
        HSSFSheet sheet = workbook.createSheet();
        //创建第一行
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        //创建第一行id,name,sex
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);

        }
        //添加数据
        for (int i = 1; i <= objList.size(); i++) {
            HSSFRow next_row = sheet.createRow(i);
            Stock entity =  objList.get(i-1);
            List<String> list = new ArrayList<>();
            list.add(entity.getStockId());
            list.add(entity.getErpId());
            list.add(entity.getStockName());
            list.add(entity.getBrand());
            list.add(entity.getSpecification());
            list.add(entity.getMF());
            list.add(entity.getPreserver());
            list.add(entity.getDepartment());
            list.add(entity.getDescription());
            switch(entity.getInventoryStatus()){
                case 0:
                    list.add("未盘");
                    break;
                case 1:
                    list.add("已盘");
                    break;
                case 2:
                    list.add("补打");
                    break;
                default:
                    list.add("");
                    break;
            }
            list.add(entity.getNewMf());
            if (entity.getInventoryTime() ==null || entity.getInventoryTime().equals("")){
                list.add(FileUtil.getTimes());

            }else{
                list.add(entity.getInventoryTime());
            }

            for (int j=0;j<list.size();j++){
                HSSFCell cell2 = next_row.createCell(j);
                cell2.setCellValue(list.get(j));
            }
//            HSSFCell cell2 = nextrow.createCell(0);
//
//            cell2.setCellValue("a" + i);
//
//            cell2 = nextrow.createCell(1);
//
//            cell2.setCellValue("user" + i);
//
//            cell2 = nextrow.createCell(2);
//
//            cell2.setCellValue("男");

        }
        //创建excel
        File file = new File(filePath);
        boolean isFile = false;
        try {
            isFile = file.createNewFile();
            //存入excel
            FileOutputStream stream = new FileOutputStream(file);
            workbook.write(stream);
            stream.close();
            exportFile = true;
            //workbook.setSheetName(0,sheetName);
        } catch (IOException e) {
            MyLOg.e("创建excel",e.getMessage());
            exportFile = false;
        }
       return exportFile;
    }

    /**
     * 读取excel   （xls和xlsx）
     * @return
     */
    public static List<Map<String, String>> readExcel(String columns[], String filePath) {
        // File file = new File(String.valueOf(readText.get(0)));
        // String filePath=file.getAbsolutePath();
        Sheet sheet = null;
        Row row = null;
        Row rowHeader = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(is);
            } else {
                wb = null;
            }
            if (wb != null) {
                // 用来存放表中数据
                list = new ArrayList<Map<String, String>>();
                // 获取第一个sheet
                sheet = wb.getSheetAt(0);
                // 获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();
                // 获取第一行
                rowHeader = sheet.getRow(0);
                row = sheet.getRow(0);
                // 获取最大列数
                int colnum = row.getPhysicalNumberOfCells();
                for (int i = 1; i < rownum; i++) {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < colnum ; j++) {
                            if(columns[j].equals(getCellFormatValue(rowHeader.getCell(j)))){
                                cellData = (String) getCellFormatValue(row
                                        .getCell(j));
                                map.put(columns[j], cellData);
                                /*DecimalFormat df = new DecimalFormat("#");
                                System.out.println(df.format(cellData));*/
                                Log.e("yy","cellData="+cellData);
                                Log.e("yy","map="+map);
                            }
                        }
                    } else {
                        break;
                    }
                    list.add(map);
                }
            }
        } catch (IOException e) {
            MyLOg.e("读取excel",e.getMessage());
        }
        return list;
    }

    /**
     *
     * @param columns
     * @param filePath
     * @return
     */
    public static List<Map<String, String>> readSerial(int columns, String filePath) {
        // File file = new File(String.valueOf(readText.get(0)));
        // String filePath=file.getAbsolutePath();
        Sheet sheet = null;
        Row row = null;
        Row rowHeader = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(is);
            } else {
                wb = null;
            }
            if (wb != null) {
                // 用来存放表中数据
                list = new ArrayList<Map<String, String>>();
                // 获取第一个sheet
                sheet = wb.getSheetAt(0);
                // 获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();
                // 获取第一行
                rowHeader = sheet.getRow(0);
                row = sheet.getRow(0);
                // 获取最大列数
                int colnum = row.getPhysicalNumberOfCells();
                for (int i = 1; i < rownum; i++) {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < colnum ; j++) {
                                cellData = (String) getCellFormatValue(row.getCell(j));
                                map.put(String.valueOf(getCellFormatValue(rowHeader.getCell(j))), cellData);
                                /*DecimalFormat df = new DecimalFormat("#");
                                System.out.println(df.format(cellData));*/
//                                Log.e("yy","cellData="+cellData);
//                                Log.e("yy","map="+map);
                        }
                    } else {
                        break;
                    }
                    list.add(map);
                }
            }
        } catch (IOException e) {
            MyLOg.e("读取excel",e.getMessage());
        }
        return list;
    }

    /**	获取单个单元格数据
     * @param cell
     * @return
     * @author lizixiang ,2018-05-08
     */
    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            // 判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        // 数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }
    /**
     * @author Sky
     * @date 2020/9/10
     * @desc
     * @uses "限制office"
     *         String[] mimeTypes = {FileUtil.MimeType.DOC, FileUtil.MimeType.DOCX, FileUtil.MimeType.PDF, FileUtil.MimeType.PPT, FileUtil.MimeType.PPTX, FileUtil.MimeType.XLS, FileUtil.MimeType.XLSX};
     *       //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
     *       intent.addCategory(Intent.CATEGORY_OPENABLE);
     */

    public class MimeType {
        public static final String DOC = "application/msword";
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String XLS = "application/vnd.ms-excel application/x-excel";
        public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        public static final String PDF = "application/pdf";
    }
}
