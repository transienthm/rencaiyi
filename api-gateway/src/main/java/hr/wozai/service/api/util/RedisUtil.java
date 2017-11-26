package hr.wozai.service.api.util;

import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/31
 */
public class RedisUtil {
  private static JedisPool pool;
  private static long count = 0L;

  private static final Properties properties;

  static {
    properties = new Properties();
    InputStream inputStream = RedisUtil.class.getClassLoader().getResourceAsStream("redis/redis.properties");
    try {
      properties.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void getPool() {
    JedisPoolConfig config = new JedisPoolConfig();
    //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
    String host = properties.getProperty("redis.hostName");
    String port = properties.getProperty("redis.port");
    String password = properties.getProperty("redis.password", "");
    String maxTotal = properties.getProperty("redis.maxTotal", "20");
    String maxIdle = properties.getProperty("redis.maxIdle", "10");
    String maxWaitMillis = properties.getProperty("redis.maxWaitMillis", "10000");
    config.setMaxTotal(Integer.valueOf(maxTotal));
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
    config.setMaxIdle(Integer.valueOf(maxIdle));
    //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
    config.setMaxWaitMillis(Long.valueOf(maxWaitMillis));
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    if (StringUtils.isNullOrEmpty(password)) {
      pool = new JedisPool(config, host, Integer.valueOf(port), 100000);
    } else {
      pool = new JedisPool(config, host, Integer.valueOf(port), 100000, password);
    }
  }

  /**
   * 在多线程环境同步初始化
   */
  private static synchronized void poolInit() {
    if (pool == null) {
      getPool();
    }
  }

  /**
   * 同步获取Jedis实例
   *
   * @return Jedis
   */
  public synchronized static Jedis getJedis() {
    if (pool == null) {
      poolInit();
    }
    Jedis jedis = null;
    count = count + 1;
    System.out.println("第几次获取:" + count);
    try {
      jedis = pool.getResource();
      System.out.println();
    } catch (Exception e) {
      System.out.println("Get jedis error : " + e.getMessage());
    }
    return jedis;
  }

  public static boolean sismember(String key, String value) {
    boolean result;
    Jedis jedis = null;
    try {
      jedis = getJedis();
      result = jedis.sismember(key, value);
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return result;
  }
}
