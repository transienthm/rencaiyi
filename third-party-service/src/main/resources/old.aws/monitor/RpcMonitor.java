package hr.wozai.service.servicecommons.utils.monitor;

import hr.wozai.service.servicecommons.utils.monitor.config.MonitorConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by pujiule on 11/20/15.
 */
public class RpcMonitor {
    private ConcurrentHashMap<String, Item> map = new ConcurrentHashMap<String, Item>();
    private ConcurrentHashMap<String, Integer> map4SlowThreshold = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<String, Integer> linger_map = new ConcurrentHashMap<String, Integer>();

    static boolean enable = true;
    private final MonitorConfig config;
    private ScheduledExecutorService ses;
    private AwsClient awsClient;

    private static volatile RpcMonitor instance = null;

    public static RpcMonitor getInstance() {
        if (instance == null) {
            synchronized (RpcMonitor.class) {
                if (instance == null) {
                    instance = new RpcMonitor();
                }
            }
        }
        return instance;
    }

    private static final Properties properties;

    static {
        properties = new Properties();
        InputStream inputStream = AwsClient.class.getClassLoader().getResourceAsStream("monitor.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RpcMonitor() {
        config = MonitorConfig.loadFromFile("monitor.properties");
        if (config.isDisabled()) {
            enable = false;
        }
        awsClient = new AwsClient(properties);
        start();
    }


    public Map<String, Item> getAndReset() {
        Map<String, Item> cloned = map;
        synchronized (RpcMonitor.class) {
            map = new ConcurrentHashMap<String, Item>();
            for (Map.Entry<String, Item> entry : cloned.entrySet()) {
                Item item = new Item();
                map.put(entry.getKey(), item);
            }
        }
        return cloned;
    }

    public Map<String, Item> get() {
        return Collections.unmodifiableMap(map);
    }

    public void setSlowThreshold(String key, int slow) {
        map4SlowThreshold.put(key, slow);
    }

    private Item makeSureExist(String key) {
        Item item = map.get(key);
        if (null == item) {
            synchronized (RpcMonitor.class) {
                item = map.get(key);//一定要赋值item，不然可能出现map数据重置造成item取不到
                if (null == item) {
                    item = new Item();
                    if (map4SlowThreshold.containsKey(key)) {
                        item.slowThreshold = map4SlowThreshold.get(key);
                    }
                    map.put(key, item);
                }
            }
        }
        return item;
    }

    public void setCount(String key, Integer value) {
        if (!enable) {
            return;
        }
        Item item = makeSureExist(key);
        item.count.set(value.intValue());
    }

    public void setLingerCount(String key, Integer value) {
        if (!enable) {
            return;
        }
        synchronized (RpcMonitor.class) {
            linger_map.put(key, value);
        }
    }

    public Map<String, Integer> getLingerMap() {
        return linger_map;
    }

    /**
     * @param key   操作标示
     * @param mills 单次操作响应时间
     */
    public void add(String key, long mills) {
        if (!enable) {
            return;
        }
        Item item = makeSureExist(key);
        item.sumtime.addAndGet(mills);
        if (item.maxTime.get() < mills) {
            item.maxTime.set(mills);
        }
        if (item.minTime.get() > mills || item.getCount() == 0) {
            item.minTime.set(mills);
        }
        item.count.incrementAndGet();
        if (item.slowThreshold > 0 && mills > item.slowThreshold) {
            item.slowCount.incrementAndGet();
        }
    }

    public void add(String key) {
        if (!enable) {
            return;
        }
        makeSureExist(key).count.incrementAndGet();
    }

    @Deprecated
    public void addRaw(String key) {
        if (!enable) {
            return;
        }
        Item item = makeSureExist(key);
        item.count.incrementAndGet();
    }

    public void start() {
        if (config.isDisabled()) {
            return;
        }

        System.out.println("JMonitorAgent start.config:[" + config + "]");

        ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Map<String, Item> m = getAndReset();
                awsClient.putDataToAws(m);
            }

        }, 5, config.getAwsPutInterval(), TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        RpcMonitor rpcMonitor = new RpcMonitor();
        rpcMonitor.start();
        rpcMonitor.add("com.shanqian.crm.service.appengine.test", 111);
        rpcMonitor.add("lepujiu", 222);
    }
}
