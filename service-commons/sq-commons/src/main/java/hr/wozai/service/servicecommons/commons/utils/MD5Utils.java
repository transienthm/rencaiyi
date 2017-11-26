// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-24
 */
public class MD5Utils {

  private static MessageDigest md5Digest = null;

  static {
    try {
      md5Digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("MD5 not supported", e);
    }
  }

  /**
   * Get 32-hex md5 digest
   *
   * @param key
   * @return
   */
  public static String md5DigestToString(String key) {

    if (key == null) {
      return null;
    }

    MessageDigest md5;
    try {
      md5 = (MessageDigest) md5Digest.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("clone of MD5 not supported", e);
    }
    md5.update(key.getBytes());

    return byte2Hex(md5.digest());
  }

  public static String md5DigestToString(InputStream inputStream) throws Exception {

    if (inputStream == null) {
      return null;
    }

    MessageDigest md5 = (MessageDigest) md5Digest.clone();
    int cnt;
    byte[] array = new byte[1024];
    while ((cnt = inputStream.read(array, 0, 1024)) != -1) {
      md5.update(array, 0, cnt);
    }

    return byte2Hex(md5.digest());
  }

  /**
   * Get 16-byte md5 digest
   *
   * @param key
   * @return
   */
  public static byte[] md5DigestToBytes(String key) {

    if (key == null) {
      return null;
    }

    MessageDigest md5;
    try {
      md5 = (MessageDigest) md5Digest.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("clone of MD5 not supported", e);
    }
    md5.update(key.getBytes());

    return md5.digest();
  }

  /**
   * Get 16-byte md5 digest
   *
   * @param inputStream
   * @return
   * @throws Exception
   */
  public static byte[] md5DigestToBytes(InputStream inputStream) throws Exception {

    if (inputStream == null) {
      return null;
    }

    MessageDigest md5 = (MessageDigest) md5Digest.clone();
    int cnt;
    byte[] array = new byte[1024];
    while ((cnt = inputStream.read(array, 0, 1024)) != -1) {
      md5.update(array, 0, cnt);
    }

    return md5.digest();
  }

  public static boolean isValidMd5DigestString(String md5Digest) {

    // TODO: HOWTO
    if (null == md5Digest
        || md5Digest.length() <= 0) {
      return false;
    }

    return true;
  }

  private static byte[] hex2Byte(byte[] b) {

    if ((b.length % 2) != 0)
      throw new IllegalArgumentException("长度不是偶数");
    byte[] b2 = new byte[b.length / 2];
    for (int n = 0; n < b.length; n += 2) {
      String item = new String(b, n, 2);
      b2[n / 2] = (byte) Integer.parseInt(item, 16);
    }

    return b2;
  }

  /**
   * 转换为16进制字符串，每个byte生成2个字符
   *
   * @param b
   * @return
   */
  private static String byte2Hex(byte[] b) {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = (Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1) {
        hs = hs + "0" + stmp;
      } else {
        hs = hs + stmp;
      }
    }
    return hs.toUpperCase();
  }

  /**
   * 转换为16进制字符串，每个byte生成一个字符（会丢失信息）
   *
   * @param b
   * @return
   */
  private static String byte2SingleHex(byte[] b) {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = (Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1) {
        hs = hs + "0" + stmp;
      } else {
        hs = hs + stmp;
      }
    }
    return hs.toUpperCase();
  }

}
