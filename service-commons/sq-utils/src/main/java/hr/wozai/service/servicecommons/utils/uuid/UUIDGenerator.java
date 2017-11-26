// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.uuid;

import java.util.UUID;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public class UUIDGenerator {
  public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
          "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
          "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
          "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
          "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
          "W", "X", "Y", "Z" };


  public static String generateShortUuid() {
    StringBuffer shortBuffer = new StringBuffer();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    for (int i = 0; i < 8; i++) {
      String str = uuid.substring(i * 4, i * 4 + 4);
      int x = Integer.parseInt(str, 16);
      shortBuffer.append(chars[x % 0x3E]);
    }
    return shortBuffer.toString();

  }

  public static String generateDocumentKey(String documentName) {
    String keyPrefix = documentName.substring(0, Math.min(20, documentName.length()));
    return keyPrefix + "_" + UUID.randomUUID();
  }

  public static String generateRandomKey() {
    return UUID.randomUUID().toString();
  }

  public static String generateCanonicalRandomKey() {
    String uuid = UUID.randomUUID().toString();
    return uuid.replace("-", "").toUpperCase();
  }

  public static void main(String[] args) {
    String name = "新文件";
    System.out.println(generateDocumentKey(name));

    System.out.println(generateShortUuid());
  }

}
