// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.facebook.swift.codec.ThriftCodecManager;
import hr.wozai.service.servicecommons.thrift.annotation.ThriftInvocationHandler;
import hr.wozai.service.servicecommons.thrift.utils.SqThriftUtils;
import hr.wozai.service.servicecommons.thrift.utils.ZkUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * thrift client, spring proxy class.
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-8-29 下午1:08
 */
public class ThriftClientProxy implements FactoryBean<Object>, ApplicationContextAware, InitializingBean {

  private static Logger LOGGER = LoggerFactory.getLogger(ThriftClientProxy.class);

  /**
   * 服务interface
   */
  private Class<?> serviceInterface;

  /**
   * 网络连接池配置，可以不配置
   */
  private ClientSocketPoolConfig socketPoolConfig;

  /**
   * 服务超时时间，默认3s
   */
  private int timeout = 3000;

  /**
   * 服务ip地址列表
   */
  private String serverIps;

  /**
   * 服务端监听端口
   */
  private String port;

  /**
   * 服务模式， ZK（自动发现） or DIRECT（直连）
   */
  private String mode = "ZK";

  /**
   * service key
   */
  private String remoteServiceKey;

  private ZooKeeper zkClient;

  /**
   * 服务代理对象
   */
  private Object serviceProxy;

  private final ThriftCodecManager codecManager;

  private ServerCluster cluster;


  private final LoadingCache<ThriftInvocationHandler.TypeAndName, ThriftInvocationHandler.ThriftClientMetadata>
      clientMetadataCache =
      CacheBuilder
          .newBuilder()
          .build(new CacheLoader<ThriftInvocationHandler.TypeAndName, ThriftInvocationHandler.ThriftClientMetadata>() {

            @Override
            public ThriftInvocationHandler.ThriftClientMetadata load(ThriftInvocationHandler.TypeAndName typeAndName)
                throws Exception {
              return new ThriftInvocationHandler.ThriftClientMetadata(typeAndName.getType(), typeAndName.getName(),
                                                                      codecManager);
            }
          });

  public ThriftClientProxy() {
    this.codecManager = checkNotNull(new ThriftCodecManager(), "codecManager is null");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (StringUtils.isBlank(remoteServiceKey) && StringUtils.isBlank(serverIps)) {
      LOGGER.error("init remote service " + serviceInterface.getSimpleName()
                   + " failed, remoteServiceKey and serverIps are null.");
      throw new RuntimeException("init remote service " + serviceInterface.getSimpleName()
                                 + " failed, remoteServiceKey and serverIps are null.");
    }
    List<String> servers = new ArrayList<>();
    if ("DIRECT".equals(mode)) {
      List<String> ips = Arrays.asList(serverIps.split(","));
      for (String ip : ips) {
        servers.add(SqThriftUtils.getIpPorts(ip, port));
      }
    } else if ("ZK".equals(mode)) {
      zkClient = ZkUtils.getInstance();
      servers = zkClient.getChildren(remoteServiceKey, (watchedEvent) -> processWatchedEvent(watchedEvent));
      LOGGER.info("afterPropertiesSet(): serverIps={}, zkPath={}", servers, remoteServiceKey);
      if (servers == null) {
        servers = new ArrayList<>();
      }
    }
    cluster = new ServerCluster(servers, getMtThriftPoolConfig(), timeout);

    ThriftMethodInterceptor clientInterceptor = new ThriftMethodInterceptor(this, cluster);
    ThriftInvocationHandler.ThriftClientMetadata
        clientMetadata =
        clientMetadataCache.getUnchecked(new ThriftInvocationHandler.TypeAndName(
            serviceInterface, serviceInterface.getName()));
    clientInterceptor.setClientMetadata(clientMetadata);

    //生成代理类
    ProxyFactory proxyFactory = new ProxyFactory(serviceInterface, clientInterceptor);
    serviceProxy = proxyFactory.getProxy();
  }

  /**
   * 处理zk 节点事件
   * <p/>
   * zk的事件处理是串行的，所以此处不会出现并发问题
   *
   * @param watchedEvent
   */
  private void processWatchedEvent(WatchedEvent watchedEvent) {
    List<ServerCluster.ServerConnection> connections = cluster.getServerConns();
    List<String> currentIpPorts = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(connections)) {
      for (ServerCluster.ServerConnection connection : connections) {
        currentIpPorts.add(SqThriftUtils.getIpPorts(connection.getIp(), connection.getPort()));
      }
    }

    try {
      if (watchedEvent.getType().equals(Watcher.Event.EventType.NodeChildrenChanged)) {
        List<String> latestIpPorts =
            zkClient.getChildren(watchedEvent.getPath(), (event) -> processWatchedEvent(event));
        LOGGER.info("processWatchedEvent(): serverIps={}, zkPath={}", latestIpPorts, watchedEvent.getPath());
        //不可用的ip port
        List<String> unavailable = new ArrayList<>(currentIpPorts);
        unavailable.removeAll(latestIpPorts);
        //新加入的ip port
        List<String> added = new ArrayList<>(latestIpPorts);
        added.removeAll(currentIpPorts);
        cluster.delServers(unavailable);
        cluster.addServers(added);
      }
    } catch (KeeperException e) {
      LOGGER.error("process children watched event keeper exception occur.", e);
    } catch (InterruptedException e) {
      LOGGER.error("process children watched event interrupted exception occur.", e);
    }
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

  }

  @Override
  public Object getObject() throws Exception {
    return serviceProxy;
  }

  @Override
  public Class<?> getObjectType() {
    return null;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }


  /**
   * 默认的连接池配置
   *
   * @return
   */
  public ClientSocketPoolConfig getMtThriftPoolConfig() {
    if (socketPoolConfig == null) {
      socketPoolConfig = new ClientSocketPoolConfig();
      socketPoolConfig.setMaxActive(100);
      socketPoolConfig.setMaxIdle(20);
      socketPoolConfig.setMinIdle(5);
      socketPoolConfig.setMaxWait(3000);
      socketPoolConfig.setTestOnBorrow(false);
      //socketPoolConfig.setTimeBetweenEvictionRunsMillis();
    }
    socketPoolConfig.setTestOnBorrow(false);// 强制关闭test
    socketPoolConfig.setTestOnReturn(false);
    return socketPoolConfig;
  }

  public void setServiceInterface(Class<?> serviceInterface) {
    this.serviceInterface = serviceInterface;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public void setServerIps(String serverIps) {
    this.serverIps = serverIps;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setSocketPoolConfig(ClientSocketPoolConfig socketPoolConfig) {
    this.socketPoolConfig = socketPoolConfig;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setRemoteServiceKey(String remoteServiceKey) {
    this.remoteServiceKey = remoteServiceKey;
  }
}
