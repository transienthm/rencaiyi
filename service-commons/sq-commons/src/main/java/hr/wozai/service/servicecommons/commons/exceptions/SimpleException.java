// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.commons.exceptions;

/**
 * 简单封装RuntimeException
 * @author liangyafei
 * @version 1.0
 * @created 15-9-12 下午4:30
 */
public class SimpleException extends RuntimeException{

    public SimpleException() {
        super();
    }

    public SimpleException(String msg) {
        super(msg);
    }

    public SimpleException(String msg, Exception ex) {
        super(msg, ex);
    }
}
