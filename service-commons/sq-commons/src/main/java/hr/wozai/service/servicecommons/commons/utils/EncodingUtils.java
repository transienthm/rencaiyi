// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-07
 */
public class EncodingUtils {

  /**
   * Provide ~1000 bytes, including the specials (e.g. Chinese Simplified)
   *
   * @param content
   * @return
   */
  public static String detectCharset(byte[] content) {

    if (null == content
        || content.length == 0) {
      return null;
    }

    UniversalDetector detector = new UniversalDetector(null);
    detector.handleData(content, 0, content.length);
    detector.dataEnd();
    String charset = detector.getDetectedCharset();

    return charset;
  }

  public static void main(String[] args) {

    byte [] content = "神经病".getBytes();
    System.out.println(detectCharset(content));

  }

}
