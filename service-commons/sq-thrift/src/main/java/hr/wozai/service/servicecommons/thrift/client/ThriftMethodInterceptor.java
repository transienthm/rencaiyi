// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.client;

import hr.wozai.service.servicecommons.thrift.annotation.ThriftInvocationHandler;
import hr.wozai.service.servicecommons.thrift.annotation.ThriftMethodHandler;
import hr.wozai.service.servicecommons.thrift.model.Entry;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 方法调用过滤器
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-8-29 下午2:06
 */
public class ThriftMethodInterceptor implements MethodInterceptor {

  private static Logger LOGGER = LoggerFactory.getLogger(ThriftMethodInterceptor.class);


  private ThriftClientProxy clientProxy;

  private ServerCluster cluster;

  private String localIpAddress;

  private static AtomicInteger counter = new AtomicInteger(0);

  private final AtomicInteger sequenceId = new AtomicInteger(1);

  private ThriftInvocationHandler.ThriftClientMetadata clientMetadata = null;

  private Map<Method, ThriftMethodHandler> methods;

  public ThriftMethodInterceptor(ThriftClientProxy clientProxy, ServerCluster cluster) {
    this.clientProxy = clientProxy;
    this.cluster = cluster;
    this.localIpAddress = getLocalIpAddress();
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    // 实现负载均衡 发起网络调用
    int retry = 7;
    Entry<Object, Throwable> rpcResult = null;
    ServerCluster.ServerConnection serverConnection = null;
    boolean isLocalAvailable = true;
    while (retry-- > 0) {
       try {
         serverConnection = selectServerWithSameIp(isLocalAvailable);
         TTransport socket = getTsocket(serverConnection);
         rpcResult = rpcInvoke(serverConnection, (TSocket) socket, invocation);
       } catch (ConnectionResetException e) {
         LOGGER.error("ConnectionResetException:" + e);
         LOGGER.error("connect to server [{}] fail, retry time:{}", serverConnection.getIp(), retry);
         /*if (serverConnection.getIp().equals(localIpAddress)) {
           isLocalAvailable = false;
         } else {
           isLocalAvailable = true;
         }*/
         break;
       } catch (RuntimeException e) {
         // socket初始化不成功
         LOGGER.error("RuntimeException:" + e);
         LOGGER.error("get socket with [{}] fail, retry time:{}", serverConnection.getIp(), retry);
         if (serverConnection.getIp().equals(localIpAddress)) {
           isLocalAvailable = false;
         } else {
           isLocalAvailable = true;
         }
         continue;
       }
      break;
    }

    Throwable toThrow = null;
    if (rpcResult != null) {
      toThrow = rpcResult.getT2();
      if (rpcResult.getT1() != null || toThrow == null) {
        return rpcResult.getT1();
      } else {
        throw toThrow;
      }
    } else {
      throw new TException("thrift rpc unknown Exception");
    }
  }

  /**
   * 发起rpc调用
   */
  private Entry<Object, Throwable> rpcInvoke(ServerCluster.ServerConnection serverConnection, TSocket socket,
                                             MethodInvocation invocation)
      throws Throwable {
    String serverIpPort = serverConnection.getIp() + ":" + serverConnection.getPort();
    String methodName = invocation.getMethod().getName();
    Throwable toThrow = null;
    Object o = null;
    try {
      TProtocol protocol = new TBinaryProtocol(socket);
      o = methods.get(invocation.getMethod()).invoke(protocol, protocol, sequenceId.getAndIncrement(), invocation.getArguments());
      return new Entry<Object, Throwable>(o, null);
    } catch (Exception e) {
      if (e.getCause() instanceof SocketException || e instanceof TTransportException) {
        returnBrokenConnection(serverConnection, socket);
        socket = null;
        throw new ConnectionResetException("connection reset");
      }

      if (socket != null) {
        if (e instanceof TApplicationException) {
          int type = ((TApplicationException) e).getType();
          if (type == TApplicationException.MISSING_RESULT || type == TApplicationException.INTERNAL_ERROR
              || type == TApplicationException.PROTOCOL_ERROR || type == TApplicationException.UNKNOWN_METHOD
              || type == 10001) {
            returnConnection(serverConnection, socket);
            socket = null;
          }
        }
        if (e instanceof TBase) {
          returnConnection(serverConnection, socket);
          socket = null;
        }
        if (socket != null) {
          returnBrokenConnection(serverConnection, socket);
          socket = null;
        }
      }
      if (e instanceof TApplicationException && ((TApplicationException) e).getType() == TApplicationException.MISSING_RESULT) {
        // server return null
      } else {
        if (e.getCause() != null && e.getCause() instanceof TTransportException
            && e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
          // 接口响应超时
          toThrow = new TException("thrift remote(" + serverIpPort + ") invoke(" + methodName + ") timeout", e);
        } else {
          if (e != null && ((e instanceof TBase) || (e instanceof TProtocolException))) {
            toThrow = e;
          } else if (e != null && (e instanceof TApplicationException)) {
            toThrow =
                new TException(
                    "thrift remote(" + serverIpPort + ") invoke(" + methodName + ") Exception:" + e.getMessage(),
                    e);
          } else {
            toThrow = new TException("thrift remote(" + serverIpPort + ") invoke(" + methodName + ") Exception", e);
          }
        }
      }
    } finally {
      if (socket != null) {
        returnConnection(serverConnection, socket);
      }
    }
    return new Entry<Object, Throwable>(o, toThrow);
  }

  void returnConnection(ServerCluster.ServerConnection serverConn, TTransport socket) {
    try {
      if (socket == null || serverConn == null || serverConn.getObjectPool() == null) {
        return;
      }
      serverConn.getObjectPool().returnObject(socket);
    } catch (Exception e) {
      throw new RuntimeException("error returnBrokenConnection()", e);
    }
  }

  void returnBrokenConnection(ServerCluster.ServerConnection serverConn, TTransport socket) {
    try {
      if (socket == null || serverConn == null || serverConn.getObjectPool() == null) {
        return;
      }
      serverConn.getObjectPool().invalidateObject(socket);
    } catch (Exception e) {
      throw new RuntimeException("error returnBrokenConnection()", e);
    }
  }

  /**
   * 获取socket连接
   *
   * @return
   */
  private TTransport getTsocket(ServerCluster.ServerConnection connection) throws Exception {
    if (connection == null) {
      LOGGER.error("can not find available server.");
      throw new RuntimeException("can not find available server.");
    }
    TTransport socket = (TTransport) connection.getObjectPool().borrowObject();
    return socket;
  }

  /**
   * 优先选取相同IP地址的服务
   * @return
   */
  private ServerCluster.ServerConnection selectServerWithSameIp(boolean isLocalAvailable) {
    List<ServerCluster.ServerConnection> connections = cluster.getServerConns();
    if (CollectionUtils.isEmpty(connections)) {
      return null;
    }

    // 如果本地不可用
    if (!isLocalAvailable) {
      return selectServer();
    } else { // 如果本地可用,优先选取本地
      for (ServerCluster.ServerConnection serverConnection : connections) {
        if (serverConnection.getIp().equals(localIpAddress)) {
          return serverConnection;
        }
      }
      return selectServer();
    }
  }

  private String getLocalIpAddress() {
    InetAddress ia = null;
    String localIp = "";
    try {
      ia=ia.getLocalHost();
      localIp=ia.getHostAddress();
    } catch (Exception e) {
      LOGGER.error("getLocalIpAddress-error():{}", e);
    }
    return localIp;
  }

  /**
   * 实现负载均衡，round robin
   *
   * @return
   */
  private ServerCluster.ServerConnection selectServer() {
    List<ServerCluster.ServerConnection> connections = cluster.getServerConns();
    if (CollectionUtils.isEmpty(connections)) {
        return null;
    }
    ServerCluster.ServerConnection target = null;
    try {
        int seq = 0;
        while(true) {
        //使用自旋 + cas，性能好于同步锁
            seq = counter.get();
            int next = seq >= Integer.MAX_VALUE ? 0 : seq + 1;
            if (counter.compareAndSet(seq, next)) {
                 break;
            }
      }

      target = connections.get(seq % connections.size());
      if (!target.isAvailable()) {
        return selectServer();
      }
    } catch (Exception e) {
      // LOGGER
      LOGGER.info("selectServer(): connections=" + connections.toString());
      LOGGER.info("selectServer(): connectionsSize=" + connections.size());
      LOGGER.info("selectServer(): counter=" + counter.intValue());
    }
    return target;
  }

  public void setClientMetadata(
      ThriftInvocationHandler.ThriftClientMetadata clientMetadata) {

    this.clientMetadata = clientMetadata;
    this.methods = clientMetadata.getMethodHandlers();
  }
}
