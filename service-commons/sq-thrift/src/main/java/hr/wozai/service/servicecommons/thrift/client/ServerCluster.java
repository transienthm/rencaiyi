// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import hr.wozai.service.servicecommons.thrift.utils.SqThriftUtils;

/**
 * 服务端集群
 * @author liangyafei
 * @version 1.0
 * @created 15-8-29 下午1:36
 */
public class ServerCluster {

    /**
     * 服务端ip 地址列表
     *
     * 针对每个rpc服务，对此列表的更新都是串行的，所有不会存在并发问题
     */
    private List<ServerConnection> serverConns = new ArrayList<>();

    private ClientSocketPoolConfig poolConfig;

    private int timeOut;

    private Map<String, ObjectPool> ipPort2Pool = new HashMap<>();

    private Map<String, ServerConnection> ipPort2Connection = new HashMap<>();

    /**
     * 单个Server的连接池
     */
    public class ServerConnection {

        private GenericObjectPool.Config connPoolConf;
        private ObjectPool objectPool;
        private String ip;
        private String port;

        private boolean available;

        public ServerConnection(String ip, String port) {
            super();
            this.ip = ip;
            this.port = port;
            ipPort2Connection.put(SqThriftUtils.getIpPorts(ip, port), this);
            available = true;
        }

        public GenericObjectPool.Config getConnPoolConf() {
            return connPoolConf;
        }

        public void setConnPoolConf(GenericObjectPool.Config connPoolConf) {
            this.connPoolConf = connPoolConf;
        }

        public ObjectPool getObjectPool() {
            return objectPool;
        }

        public void setObjectPool(ObjectPool objectPool) {
            this.objectPool = objectPool;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ServerConnection))
                return false;

            ServerConnection that = (ServerConnection) o;

            if (ip != null ? !ip.equals(that.ip) : that.ip != null)
                return false;
            if (port != null ? !port.equals(that.port) : that.port != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = ip != null ? ip.hashCode() : 0;
            result = 31 * result + (port != null ? port.hashCode() : 0);
            return result;
        }
    }

    public ServerCluster(List<String> servers, ClientSocketPoolConfig poolConfig, int timeOut) {
        for (String server : servers) {
            String [] ipPort = server.split(":");
            ServerConnection serverConn = new ServerConnection(ipPort[0], ipPort[1]);
            serverConn.setObjectPool(createPool(ipPort[0], Integer.valueOf(ipPort[1]), timeOut, poolConfig));
            serverConn.setConnPoolConf(poolConfig);
            serverConns.add(serverConn);
        }
        this.poolConfig = poolConfig;
        this.timeOut = timeOut;
    }

    /**
     * zk服务地址列表变更后，更新内存服务地址列表
     * 新增服务地址
     *
     * @param servers
     */
    public void addServers(List<String> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return;
        }
        for (String server : servers) {
            String [] ipPort = server.split(":");
            ServerConnection serverConn = new ServerConnection(ipPort[0], ipPort[1]);
            serverConn.setObjectPool(createPool(ipPort[0], Integer.valueOf(ipPort[1]), timeOut, poolConfig));
            serverConn.setConnPoolConf(poolConfig);
            serverConn.setAvailable(true);
            serverConns.add(serverConn);
        }
    }

    /**
     * zk服务地址列表变更后，更新内存服务地址列表
     * 删除服务地址
     *
     * @param servers
     */
    public void delServers(List<String> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return;
        }
        for (String server : servers) {
            //TODO 暂时没有实现HA，如果zk server挂掉或者网络断开，则会导致rpc服务全部不可用，后期实现服务优雅降级 以及 服务地址列表持久化
            //清理网络连接池
            destoryPool(ipPort2Pool.get(server));
            //清理服务地址列表
            ipPort2Connection.get(server).setAvailable(false);
            serverConns.remove(ipPort2Connection.get(server));
            ipPort2Connection.remove(server);
        }
    }

    protected ObjectPool createPool(String host, int port, int timeOut, GenericObjectPool.Config poolConfig) {
        GenericObjectPool genericObjectPool = new GenericObjectPool(
                new PoolableSocketFactory(host, port,timeOut), poolConfig);
        if(0 == poolConfig.minIdle) {
            genericObjectPool.setMinEvictableIdleTimeMillis(poolConfig.timeBetweenEvictionRunsMillis);
        }
        ipPort2Pool.put(SqThriftUtils.getIpPorts(host, String.valueOf(port)), genericObjectPool);
        return genericObjectPool;
    }

    public List<ServerConnection> getServerConns() {
        return serverConns;
    }

    protected void destoryPool(ObjectPool pool) {
        if (pool != null) {
            try {
                pool.close();
            } catch (Exception e) {
            }
        }
    }
}
