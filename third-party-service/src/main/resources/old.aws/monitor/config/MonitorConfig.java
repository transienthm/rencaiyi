package hr.wozai.service.servicecommons.utils.monitor.config;

import hr.wozai.service.servicecommons.utils.monitor.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * monitor config
 * Author: lepujiu
 */
public class MonitorConfig {
    public static final String DFT_CONFIG_FILE_NAME = "monitor.properties";

    public static MonitorConfig loadFromFile(String filename) {
        if (Utils.isBlank(filename)) {
            filename = DFT_CONFIG_FILE_NAME;
        }
        Properties props = new Properties();
        InputStream is = MonitorConfig.class.getClassLoader()
                .getResourceAsStream(filename);
        if (is == null) {
            is = MonitorConfig.class.getClassLoader().getResourceAsStream("monitor.properties");
        }
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("MonitorConfig failed to read " + filename, e);
        }
        MonitorConfig config = new MonitorConfig(props);
        return config;
    }

    private boolean disabled;
    private long awsPutInterval;

    public MonitorConfig(Properties props) {
        disabled = Boolean.parseBoolean(props.getProperty("disabled", "false"));
        if (disabled) {
            return;
        }
        awsPutInterval = Long.parseLong(props.getProperty("aws.put.interval", "10"));
    }

    public boolean isDisabled() {
        return disabled;
    }

    public long getAwsPutInterval() {
        return awsPutInterval;
    }

    public void setAwsPutInterval(long awsPutInterval) {
        this.awsPutInterval = awsPutInterval;
    }

    @Override
	public String toString() {
		return "MonitorConfig [disabled=" + disabled + ", awsPutInterval=" + awsPutInterval;
	}


}
