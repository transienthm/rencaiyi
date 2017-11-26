// Copyright (C) 2015 Meituan
// All rights reserved
package hr.wozai.service.servicecommons.thrift.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-11-21 下午10:13
 */
public class SqThriftUtils {

    public static String getLocalIpV4() {
        Enumeration<NetworkInterface> networkInterface;
        try {
            networkInterface = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
        String ip = null;
        while (networkInterface.hasMoreElements()) {
            NetworkInterface ni = networkInterface.nextElement();
            Enumeration<InetAddress> inetAddress = ni.getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress ia = inetAddress.nextElement();
                if (ia instanceof Inet6Address) {
                    // ignore ipv6
                    continue;
                }
                String thisIp = ia.getHostAddress();
                if (!ia.isLoopbackAddress() && !thisIp.contains(":") && !"127.0.0.1".equals(thisIp)) {
                    ip = thisIp;
                }
            }
        }
        return ip;
    }

    public static String getIpPorts(String ip, String port) {
        StringBuilder sb = new StringBuilder();
        sb.append(ip).append(":").append(port);
        return sb.toString();
    }
}
