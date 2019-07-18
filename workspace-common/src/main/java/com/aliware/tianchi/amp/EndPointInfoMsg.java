package com.aliware.tianchi.amp;

import com.aliware.tianchi.amp.InstanceInfo;
import org.apache.dubbo.config.ProtocolConfig;

/**
 * @author Fan Huaran
 * created on 2019/7/10
 * @description
 */
public class EndPointInfoMsg {
    private String host;

    private Integer port;

    private PerformanceIndicator performanceIndicator;

    private InstanceInfo instanceInfo;

    private ProtocolConfig protocolConfig;

    public EndPointInfoMsg() {
    }

    public EndPointInfoMsg(String host, Integer port, PerformanceIndicator performanceIndicator, InstanceInfo instanceInfo, ProtocolConfig protocolConfig) {
        this.host = host;
        this.port = port;
        this.performanceIndicator = performanceIndicator;
        this.instanceInfo = instanceInfo;
        this.protocolConfig = protocolConfig;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public PerformanceIndicator getPerformanceIndicator() {
        return performanceIndicator;
    }

    public void setPerformanceIndicator(PerformanceIndicator performanceIndicator) {
        this.performanceIndicator = performanceIndicator;
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    @Override
    public String toString() {
        return "EndPointInfoMsg{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", performanceIndicator=" + performanceIndicator +
                ", instanceInfo=" + instanceInfo +
                ", protocolConfig=" + protocolConfig +
                '}';
    }
}
