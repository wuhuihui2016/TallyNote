package com.whh.tallynote.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class DesEncryptUtils {

    /**
     * Base64编码加密
     *
     * @param data 待加密字节数组
     * @return 加密后字符串
     */
    public static String encode(String data) throws CodecException {
        if (data == null) return null;
        return new String(org.bouncycastle.util.encoders.Base64.encode(encode(data.getBytes(), "IrisKing")));
    }

    /**
     * Base64编码解密
     *
     * @param data 待解密字符串
     * @return 解密后字节数组
     * @throws CodecException 异常
     */
    public static String decode(String data) throws CodecException {
        if (data == null) return null;
        try {
            return new String(decode(org.bouncycastle.util.encoders.Base64.decode(data.getBytes()), "IrisKing"));
        } catch (RuntimeException e) {
            throw new CodecException(e.getMessage(), e);
        }
    }

    /**
     * 密码算法示例
     *
     * @throws CodecException
     */
    public static void test() throws CodecException {
        LogUtils.e("whh1012加密", "a和法国和a 加密：" + DesEncryptUtils.encode("a和法国和a")); //37vxUZLZ16HVSImUhiUeSQ==
        LogUtils.e("whh1012加密", "12345678 加密：" + DesEncryptUtils.encode("12345678")); //CxthicxzTr0WDHtwBu+iGw==
        LogUtils.e("whh1012加密", "1234qwer 加密：" + DesEncryptUtils.encode("1234qwer")); //sYUOank/bWYWDHtwBu+iGw==
        LogUtils.e("whh1012解密", "YeWSjOazleWbveWSjGE= 解密：" + DesEncryptUtils.decode("sYUOank/bWYWDHtwBu+iGw==")); //1234qwer
    }

    /**
     * 加解密算法异常类
     *
     * @author Administrator
     * @version 2015-4-4 17:20:11
     */
    public static class CodecException extends Exception {
        private static final long serialVersionUID = 3966129708775022345L;

        public CodecException() {
            super();
        }

        public CodecException(String msg) {
            super(msg);
        }

        public CodecException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public CodecException(Throwable cause) {
            super(cause);
        }
    }


    private static final String alg = "DES";
    private static final String transformation = "DES/ECB/PKCS5Padding";

    /**
     * DES加密
     *
     * @param src 待加密数据
     * @param key 加密私钥，长度必须是8的倍数
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static byte[] encode(byte[] src, String key) throws CodecException {
        return encode(src, key, transformation);
    }

    /**
     * DES加密
     *
     * @param src            待加密数据
     * @param key            加密私钥，长度必须是8的倍数
     * @param transformation String
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static byte[] encode(byte[] src, String key, String transformation) throws CodecException {
        try {
            //DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(transformation);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    /**
     * DES解密
     *
     * @param src 待解密字符串
     * @param key 解密私钥，长度必须是8的倍数
     * @return 解密后的字节数组
     */
    public static byte[] decode(byte[] src, String key) throws CodecException {
        return decode(src, key, transformation);
    }

    /**
     * DES解密
     *
     * @param src            待解密字符串
     * @param key            解密私钥，长度必须是8的倍数
     * @param transformation String
     * @return 解密后的字节数组
     */
    public static byte[] decode(byte[] src, String key, String transformation) throws CodecException {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(transformation);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
            // 现在，获取数据并解密
            // 正式执行解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CodecException(e);
        }
    }

}
