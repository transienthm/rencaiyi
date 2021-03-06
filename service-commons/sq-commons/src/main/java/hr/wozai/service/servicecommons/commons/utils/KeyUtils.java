// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Author:  Zhe Chen Created: 2015-08-20
 */
public class KeyUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyUtils.class);

  /**
   * The input requires original ${publicKeyFilePath} generated by:
   *
   * @param keyFilePath
   * @return
   */
  public static RSAPrivateKey loadRsaPrivateKey(String keyFilePath) throws Exception {

    byte[] keyBytes = loadKeyFileContent(keyFilePath);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

    return rsaPrivateKey;
  }

  /**
   * The input requires original ${privateKeyFile} generated by: $ssh-keygen -t rsa
   *
   * @param keyFilePath
   * @return
   */
  public static RSAPublicKey loadRsaPublicKey(String keyFilePath) throws Exception {

    byte[] keyBytes = loadKeyFileContent(keyFilePath);
    X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

    return rsaPublicKey;
  }

  /**
   * Load file fully to a byte array
   *
   * @param keyFilePath
   * @return
   * @throws Exception
   */
  public static byte[] loadKeyFileContent(String keyFilePath) throws Exception {

    ApplicationContext appContext = new ClassPathXmlApplicationContext();
    Resource keyResource = appContext.getResource(keyFilePath);
    InputStream is = keyResource.getInputStream();
    DataInputStream dis = new DataInputStream(is);
    byte[] keyBytes = new byte[(int) keyResource.contentLength()];
    dis.readFully(keyBytes);
    dis.close();

    return keyBytes;
  }

}

