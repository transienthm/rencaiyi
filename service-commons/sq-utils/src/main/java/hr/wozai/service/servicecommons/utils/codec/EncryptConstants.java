// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.utils.codec;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 加密常量
 * @author liangyafei
 * @version 1.0
 * @created 15-9-3 下午12:10
 */
public class EncryptConstants {
    /**
     * 为uuid加密的私钥
     */
    public static String DEFAULT_ENCRYPT_SALT;

    /**
     * 为uuid加密的私钥
     */
    public static String DEFAULT_PRIVATE_KEY;

    static {
        try {
            Properties p = new Properties();
            InputStream in = EncryptConstants.class.getClassLoader()
                    .getResourceAsStream("keypair/keyset.properties");
            p.load(in);
            DEFAULT_ENCRYPT_SALT = p.getProperty("serializer.encrypt.salt", "#$%^&*()");
            DEFAULT_PRIVATE_KEY = p.getProperty("serializer.private.key", "4]H%6p-ma}IgHB");
            in.close();
        } catch (Exception e) {

        }
    }
}
