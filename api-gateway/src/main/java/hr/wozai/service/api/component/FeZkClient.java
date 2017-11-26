// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.component;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-07-20
 */
@Component("zkClient")
public class FeZkClient implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeZkClient.class);

  @Value("${thrift.zookeeper.servers}")
  private String zkServers;

  @Value("${thrift.zookeeper.server.session.timeout}")
  private Integer zkTimeout;

  @Value("${fe.js.app.path}")
  private String appJsPath;

  @Value("${fe.css.app.path}")
  private String appCssPath;

  @Value("${fe.js.auth.path}")
  private String authJsPath;

  @Value("${fe.css.auth.path}")
  private String authCssPath;

  @Value("${fe.js.charts.path}")
  private String chartsJsPath;

  @Value("${fe.css.charts.path}")
  private String chartsCssPath;

  @Value("${fe.js.onboarding.path}")
  private String onboardingJsPath;

  @Value("${fe.css.onboarding.path}")
  private String onboardingCssPath;

  @Value("${fe.js.admin.path}")
  private String adminJsPath;

  @Value("${fe.css.admin.path}")
  private String adminCssPath;

  private List<String> hosts;

  private ZooKeeper zkClient;

  private Watcher watcher = new Watcher() {
    public void process(WatchedEvent event) {
      LOGGER.info("process : " + event.getType());
    }
  };

  @Override
  public void afterPropertiesSet() throws Exception {
    hosts = Arrays.asList(zkServers.split(","));
    zkClient = new ZooKeeper(hosts.get(0), zkTimeout, watcher);

    String appJsVersion = getAppJsVersion();
    String authJsVersion = getAuthJsVersion();
    String onboardingJsVersion = getOnboardingJsVersion();
    String adminJsVersion = getAdminJsVersion();

    String appCssVersion = getAppCssVersion();
    String authCssVersion = getAuthCssVersion();
    String onboardingCssVersion = getOnboardingCssVersion();
    String adminCssVersion = getAdminCssVersion();

    LOGGER.info("afterPropertiesSet(): zkServers={}, zkTimeout={}, "
                    + "appJsPath={}, authJsPath={}, onboardingJsPath={},adminJsPath={}, "
                    + "appCssPath={}, authCssPath={}, onboardingCssPath={}, adminCssPath={}, "
                    + "appJsVersion={}, authJsVersion={}, onboardingJsVersion={}, adminJsVersion={}, "
                    + "appCssVersion={}, authCssVersion={}, onboardingCssVersion={}, adminCssVersion={}",
            zkServers, zkTimeout,
            appJsPath, authJsPath, onboardingJsPath, adminJsPath,
            appCssPath, authCssPath, onboardingCssPath, adminCssPath,
            appJsVersion, authJsVersion, onboardingJsVersion, adminJsVersion,
            appCssVersion, authCssVersion, onboardingCssVersion, adminCssVersion);

  }

  public String getAppJsVersion() {
    try {
      if (zkClient.exists(appJsPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(appJsPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAppJsVersion()-error", e);
      return null;
    }
  }

  public String getAppCssVersion() {
    try {
      if (zkClient.exists(appCssPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(appCssPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAppCssVersion()-error", e);
      return null;
    }
  }

  public String getAuthJsVersion() {
    try {
      if (zkClient.exists(authJsPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(authJsPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAuthJsVersion()-error", e);
      return null;
    }
  }

  public String getAuthCssVersion() {
    try {
      if (zkClient.exists(authCssPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(authCssPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAuthCssVersion()-error", e);
      return null;
    }
  }

  public String getOnboardingJsVersion() {
    try {
      if (zkClient.exists(onboardingJsPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(onboardingJsPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getOnboardingJsVersion()-error", e);
      return null;
    }
  }

  public String getOnboardingCssVersion() {
    try {
      if (zkClient.exists(onboardingCssPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(onboardingCssPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getOnboardingCssVersion()-error", e);
      return null;
    }
  }

  public String getChartsJsVersion() {
    try {
      if (zkClient.exists(chartsJsPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(chartsJsPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAdminJsVersion()-error", e);
      return null;
    }
  }

  public String getchartsCssVersion() {
    try {
      if (zkClient.exists(chartsCssPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(chartsCssPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getOnboardingCssVersion()-error", e);
      return null;
    }
  }

  public String getAdminJsVersion() {
    try {
      if (zkClient.exists(adminJsPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(adminJsPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAdminJsVersion()-error", e);
      return null;
    }
  }

  public String getAdminCssVersion() {
    try {
      if (zkClient.exists(adminCssPath, false) == null) {
        return null;
      } else {
        return new String(zkClient.getData(adminCssPath, false, null));
      }
    } catch (Exception e) {
      LOGGER.error("getAdminCssVersion()-error", e);
      return null;
    }
  }

}
