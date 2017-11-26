// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.client;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-10-4 下午9:36
 */
public class ConnectionResetException extends RuntimeException {

    public ConnectionResetException(String msg) {
        super(msg);
    }
}
