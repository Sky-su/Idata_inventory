package com.ives.idata_inventory.adapter;



import com.ives.idata_inventory.entity.SerialEntity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SeriaSaxXml {

    public static List<SerialEntity> sax2xml(String data) throws Exception {
        InputStream is =  new ByteArrayInputStream(data.getBytes());
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //初始化Sax解析器
        SAXParser sp = spf.newSAXParser();
        //新建解析处理器
        MyHandler handler = new MyHandler();
        //将解析交给处理器
        sp.parse(is, handler);
        //返回List
        return handler.getList();
    }

    public static class MyHandler extends DefaultHandler {

        private List<SerialEntity> list = null;
        private SerialEntity empGeneral;
        //用于存储读取的临时变量
       // private String tempString;

        private String node;
        //用于存储读取的临时变量
        private StringBuilder sb;
        private boolean flag = false;

        /**
         * 解析到文档开始调用，一般做初始化操作
         *
         * @throws SAXException
         */
        @Override
        public void startDocument() throws SAXException {
            list = new ArrayList<>();
            super.startDocument();
        }

        /**
         * 解析到文档末尾调用，一般做回收操作
         *
         * @throws SAXException
         */
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        /**
         * 每读到一个元素就调用该方法
         *
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            flag = true;
            if ("asset".equals(qName)) {
                //读到empGeneral标签
                empGeneral = new SerialEntity();
            }
            node = qName;
            sb = new StringBuilder();


        }
        /**
         * 读到元素的结尾调用
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if ("asset".equals(qName)) {
                list.add(empGeneral);
            }
            String tempString = sb.toString();
            switch (node) {
                case "serialIdentifier":
                    empGeneral.setSerialIdentifier(tempString.trim());
                    break;
                case "typeName":
                    empGeneral.setTypeName(tempString.trim());
                    break;
                default :
                    break;
            }
            super.endElement(uri, localName, qName);
        }

        /**
         * 读到属性内容调用
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
           // tempString = new String(ch, start, length);
            super.characters(ch, start, length);
            if (!flag){
                return;
            }
            sb.append(new String(ch,start,length));
        }


        /**
         * 获取该List
         *
         * @return
         */
        public List<SerialEntity> getList() {
            return list;
        }
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
        //Log.d("jjjj",stringBuffer.toString());
        return stringBuffer.toString();
    }


    /**
     * 获取当前时间
     * 可根据需要自行截取数据显示 // yyyy-MM-dd HH:mm
     * @return
     */
    public static String getTimes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd日HH时mm");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}

