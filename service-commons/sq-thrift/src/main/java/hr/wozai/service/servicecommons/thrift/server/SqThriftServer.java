// Copyright (C) 2015 Meituan
// All rights reserved
package hr.wozai.service.servicecommons.thrift.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import hr.wozai.service.servicecommons.thrift.utils.ZkUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.google.common.collect.ImmutableList;
import hr.wozai.service.servicecommons.thrift.utils.SqThriftUtils;

/**
 * Thrift Server 工厂类
 * @author liangyafei
 * @version 1.0
 * @created 15-8-20 上午12:13
 */
public class SqThriftServer<T> {


    private static final Logger LOGGER = LoggerFactory.getLogger(SqThriftServer.class);

    private static final Map<Integer, String> PORTS = new ConcurrentHashMap();

    private static final Map<Integer, ThriftServer> SERVERS = new ConcurrentHashMap();

    private static ZooKeeper zkClient;


    /**
     * 启动thrift server，并注册服务地址到zk
     *
     * @param service
     * @param port
     * @param serviceKey
     * @param <T>
     */
    public static <T> void start(T service, int port, String serviceKey) {
        if (service == null || port <= 0 || StringUtils.isBlank(serviceKey)) {
            LOGGER.error("service or port or serviceKey is null.");
            throw new RuntimeException("service or port or serviceKey is null.");
        }
        getInstance(service, port).start();

        if (StringUtils.isBlank(serviceKey)) {
            return;
        }
        //注册到zk
        try {
            zkClient = ZkUtils.getInstance();
            createZkNodeRecursive(serviceKey);
            addServerNode(serviceKey, port);
        } catch (KeeperException e) {
            LOGGER.error("start service " + service.getClass().getSimpleName() + " failed, keeper exception occur.");
            throw new RuntimeException("start service " + service.getClass().getSimpleName() + " failed, keeper exception occur.", e);
        } catch (InterruptedException e) {
            LOGGER.error("start service " + service.getClass().getSimpleName() + " failed, interrupted exception occur.");
            throw new RuntimeException("start service " + service.getClass().getSimpleName() + "failed, interrupted exception occur", e);
        }
    }


    /**
     * 创建zk节点
     */
    private static void createZkNodeRecursive(String path) throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists(path, false);
        if (stat != null) {
            return;
        }
        createParentIfNeed(path);
    }

    /**
     * 递归创建zk父节点
     * @param path
     */
    private static void createParentIfNeed(String path) throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists(path, false);
        if (stat != null) {
            return;
        }
        int parentPathIndex = path.lastIndexOf("/");
        if (parentPathIndex > 0) {
            String parentPath = path.substring(0, parentPathIndex);
            createParentIfNeed(parentPath);
            zkClient.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            zkClient.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * 创建server的临时zk node
     * @param serviceKey
     * @param port
     * @throws KeeperException
     * @throws InterruptedException
     */
    private static void addServerNode(String serviceKey, int port) throws KeeperException, InterruptedException {
        String ipv4 = SqThriftUtils.getLocalIpV4();
        String childNode = SqThriftUtils.getIpPorts(ipv4, String.valueOf(port));
        List<String> children = zkClient.getChildren(serviceKey, false);

        //理论上不会用到第二个判断条件
        if (CollectionUtils.isEmpty(children) || !children.contains(childNode)) {
            zkClient.create(serviceKey + "/" + childNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }
    }

    /**
     * 根据服务实现和端口号获取server实例
     * @param service
     * @param port
     * @param <T>
     * @return
     */
    private synchronized static <T> ThriftServer getInstance(T service, int port) {

        if (!PORTS.containsKey(port)) {
            PORTS.put(port, service.getClass().getSimpleName());
        } else if (PORTS.get(port).equals(service.getClass().getSimpleName())){
            return SERVERS.get(port);
        } else {
            throw new RuntimeException("port " + port + "has been occupied by service" + PORTS.get(port));
        }

        ThriftServiceProcessor processor = new ThriftServiceProcessor(
                new ThriftCodecManager(),
                ImmutableList.<ThriftEventHandler>of(),
                service
        );

        /**
         * 工作线程池
         */
        ExecutorService taskWorkerExecutor = new ThreadPoolExecutor(10, 50, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    private AtomicInteger threadCount = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "Thrift-Worker-" + threadCount.getAndDecrement());
                    }
                });

        ThriftServerDef serverDef = ThriftServerDef.newBuilder()
                .listen(port)
                .withProcessor(processor)
                .using(taskWorkerExecutor)
                .build();

        /**
         * 管理线程池&io线程池
         */
        ExecutorService bossExecutor = Executors.newCachedThreadPool();
        ExecutorService ioWorkerExecutor = Executors.newCachedThreadPool();

        /**
         * 创建netty server
         */
        NettyServerConfig serverConfig = NettyServerConfig.newBuilder()
                .setBossThreadExecutor(bossExecutor)
                .setWorkerThreadExecutor(ioWorkerExecutor)
                .build();

        ThriftServer server = new ThriftServer(serverConfig, serverDef);
        SERVERS.putIfAbsent(port, server);

        return server;
    }
}
