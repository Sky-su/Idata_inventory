package com.ives.idata_inventory.util;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtilZone {
        /**
         * AES_KEY 加密解密用的密钥Key，可以用26个字母和数字组成的16位，此处使用AES-128-CBC加密模式。
         * AES_VECTOR 向量， 普通aes加密解密需要为16位。
         */
        public static final String AES_KEY = "0123456789101112";
        public static final String AES_VECTOR = "abcdefghijklmnop";


        /**
         * 加密
         */
        public static String encrypt(String sSrc, String key, String vector) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
            // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(encrypted));// 此处使用BASE64做转码
        }



        public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String tmp;
        for (byte value : b) {
            tmp = (Integer.toHexString(value & 0XFF));
            if (tmp.length() == 1) {
                hs.append("0").append(tmp);
            } else {
                hs.append(tmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    /**
         * 解密
         */
        public static String decrypt(String sSrc, String key, String ivs)  {
            try {
                byte[] raw = key.getBytes("ASCII");
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES");
                IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                byte[] encrypted1 = Base64.getDecoder().decode(sSrc);// 先用base64解密
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }


        public static String encodeBytes(byte[] bytes) {
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
                strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
            }
            return strBuf.toString();
        }

        /**
         * 增强加密 密码为原始12位+4位随机数，随机数带入密文中,所以相同的内容每次加密后密文不一样
         */
        public static String encryptAd(String content, String key, String iv) {
            key = key.substring(0,12);
            int num = (int) (Math.random() * 9000 + 1000);
            String newKey= key + num;
            String tt4 = null;
            try {
                tt4 = encrypt(content, newKey, iv);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            tt4 = tt4.replace("=", "!");
            tt4 = tt4.replace('+', '[');
            tt4 = tt4.replace('/', ']');
            String result = tt4 + num;
            return result;
        }

        /**
         * 增强解密 明文[0,15] 位：密文为24 +4=28 位 明文[16,31] 位：密文为44 +4=48 位
         */
        public static String decryptAd(String data, String skey, String iv)  {
            skey = skey.substring(0,12);
            data = data.replace("!", "=");
            data = data.replace('[', '+');
            data = data.replace(']', '/');
            int len = data.length();

            String realkey = skey + data.substring(len - 4, len);
            String realcontent = data.substring(0, len - 4);
            String result = null;
            try {
                result = decrypt(realcontent, realkey, iv);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }
}
