// Copyright (C) 2015 Meituan
// All rights reserved
package hr.wozai.service.servicecommons.thrift.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-11-22 上午9:09
 */
public class ZkUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(ZkUtils.class);

    /**
     * 声明成volatile，防止并发情况下由于cpu重排序导致返回null单例
     */
    private static volatile ZooKeeper zkClient;

    private static String ZOOKEEPER_SERVER;

    /**
     * session timeout 是和服务端协商的超时时间，会存储在服务端
     * 与集群超过5000ms失去心跳后，被认为与服务器断开
     */
    private static Integer SESSION_TIMEOUT = 5000;

    private static CountDownLatch latch = new CountDownLatch(1);

    /**
     * 初始化
     *
     * 加载 zookeeper.properties 文件
     *
     * 启动 zookeeper client
     */
    private static void init() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("zookeeper.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
            ZOOKEEPER_SERVER = (String)properties.get("thrift.zookeeper.servers");
            SESSION_TIMEOUT = StringUtils.isBlank((String)properties.get("thrift.zookeeper.server.session.timeout")) ? SESSION_TIMEOUT : Integer.valueOf(
                    (String)properties.get("thrift.zookeeper.server.session.timeout"));
        } catch (IOException e) {
            LOGGER.error("load zookeeper properties failed");
            throw new RuntimeException("load zookeeper properties failed", e);
        }

        try {
            zkClient = new ZooKeeper(ZOOKEEPER_SERVER, SESSION_TIMEOUT, (watchedEvent) -> processConnectedEvent(watchedEvent));
            latch.await();
            LOGGER.info("init zookeeper successful");
        } catch (Exception e) {
            LOGGER.error("init zookeeper client failed");
            throw new RuntimeException("init zookeeper client failed", e);
        }
    }

    /**
     * 处理zk连接成功的事件
     *
     * @param event
     */
    private static void processConnectedEvent(WatchedEvent event) {
        if (event.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
            latch.countDown();
        }
    }

    /**
     * 获取单例zk client
     *
     * @return
     */
    public static ZooKeeper getInstance() {
        if (zkClient == null) {
            synchronized (ZkUtils.class) {
                if (zkClient == null) {
                    init();
                }
            }
        }
        return zkClient;
    }
}
