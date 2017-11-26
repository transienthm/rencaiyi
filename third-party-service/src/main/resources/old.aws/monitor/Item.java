package hr.wozai.service.servicecommons.utils.monitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pujiule on 11/24/15.
 */
public class Item {
    public Item() {
    }

    AtomicLong sumtime = new AtomicLong();
    AtomicLong maxTime = new AtomicLong();
    AtomicLong minTime = new AtomicLong();
    AtomicInteger count = new AtomicInteger();
    AtomicInteger slowCount = new AtomicInteger();
    int slowThreshold;

    public long getSumtime() {
        return sumtime.get();
    }

    public long getMaxTime() {
        return maxTime.get();
    }

    public long getMinTime() {
        return minTime.get();
    }

    public int getCount() {
        return count.get();
    }

    public int getSlowCount() {
        return slowCount.get();
    }

    public int getSlowThreshold() {
        return slowThreshold;
    }
}
