// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.client;

import java.net.SocketTimeoutException;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * socket 连接池工厂类
 * @author liangyafei
 * @version 1.0
 * @created 15-8-29 下午1:45
 */
public class PoolableSocketFactory  implements PoolableObjectFactory {

    private String host;
    private int port;
    private int timeOut;

    public PoolableSocketFactory(String host, int port, int timeOut) {
        this.host = host;
        this.port = port;
        this.timeOut = timeOut;
    }


    /**
     * 创建Tsocket对象
     */
    @Override
    public Object makeObject() throws Exception {
        // 三次重试
        int count = 3;
        TTransportException exception = null;
        while (count-- > 0) {
            TTransport transport = null;
            boolean connectSuccess = false;
            try {
                transport = new TSocket(host, port, 10000);
                transport.open();
                connectSuccess = true;
                ((TSocket)transport).setTimeout(timeOut);
                return transport;
            } catch (TTransportException te) {
                exception = te;
                if (!(te.getCause() instanceof SocketTimeoutException))
                    break;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (transport != null && connectSuccess == false) {
                    try {
                        transport.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public boolean validateObject(Object arg0) {
        //TODO
        return true;
    }

    @Override
    public void destroyObject(Object tTransport) throws Exception {
        if (tTransport instanceof TSocket) {
            TSocket socket = (TSocket) tTransport;
            if (socket.isOpen()) {
                socket.close();
            }
        } else if(tTransport instanceof TNonblockingSocket) {
            TNonblockingSocket socket = (TNonblockingSocket) tTransport;
            if (socket.isOpen()) {
                socket.close();
            }
        }
    }

    @Override
    public void passivateObject(Object arg0) throws Exception {
        // DO NOTHING
    }

    @Override
    public void activateObject(Object arg0) throws Exception {
        // DO NOTHING
    }

}
