// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.utils.codec;

import java.lang.reflect.Field;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Hex;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-9-3 下午12:13
 */
public class EncryptUtils {

    /**
     * 判断是否为基本类型，java.lang包下面的类都可以认为是基本类型
     *
     * @param field
     * @return
     */
    /*private static boolean isPrimitiveType(Field field) {

        Class type = field.getType();
        if (type.isPrimitive()) {
            return true;
        }
        if (type.getPackage().equals("java.lang")) {
            return true;
        }
        return false;
    }*/

    /**
     * 支持以下任意一种算法
     *
     * <pre>
     * PBEWithMD5AndDES
     * PBEWithMD5AndTripleDES
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC2_40
     * </pre>
     */
    public static final String ALGORITHM = "PBEWITHMD5andDES";


    private static final String KEY_ALGORITHM = "DESede";


    /**
     * 转换密钥
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key keyGenerator(String key) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return secretKey;
    }

    /**
     * 对称加密
     *
     * @param data 数据
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String symmetricEncrypt(String data, String privateKey) throws Exception {
        Key key = keyGenerator(privateKey);
        PBEParameterSpec paramSpec = new PBEParameterSpec(EncryptConstants.DEFAULT_ENCRYPT_SALT.getBytes(), 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        byte[] buff = cipher.doFinal(data.getBytes());
        return Hex.encodeHexString(buff).toUpperCase();
    }

    /**
     * 对称解密
     *
     * @param data 数据
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String symmetricDecrypt(String data, String privateKey) throws Exception {
        Key key = keyGenerator(privateKey);
        PBEParameterSpec paramSpec = new PBEParameterSpec(EncryptConstants.DEFAULT_ENCRYPT_SALT.getBytes(), 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] buff = cipher.doFinal(Hex.decodeHex(data.toCharArray()));
        return new String(buff);
    }

    /**
     * 对称加密
     *
     * @param data 数据
     * @return
     * @throws Exception
     */
    public static String symmetricEncrypt(String data) throws Exception {
        return symmetricEncrypt(data, EncryptConstants.DEFAULT_PRIVATE_KEY);
    }

    /**
     * 对称解密
     *
     * @param data 数据
     * @return
     * @throws Exception
     */
    public static String symmetricDecrypt(String data) throws Exception {
        return symmetricDecrypt(data, EncryptConstants.DEFAULT_PRIVATE_KEY);
    }

    public static void main(String[] args) throws Exception {
        String inputStr = "liangyafei";
        System.out.println("原文: " + inputStr);

        String pwd = "lyf";


        String encryptData = symmetricEncrypt(inputStr, pwd);
        System.out.println("加密后: " + encryptData);

        String decryptData = symmetricDecrypt(encryptData, pwd);
        System.out.println("解密后: " + decryptData);

    }
}