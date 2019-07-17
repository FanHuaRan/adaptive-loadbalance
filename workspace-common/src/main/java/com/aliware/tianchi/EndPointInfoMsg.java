package com.aliware.tianchi;

import org.apache.dubbo.config.ProtocolConfig;

/**
 * @author Fan Huaran
 * created on 2019/7/10
 * @description
 */
public class EndPointInfoMsg {
    private String host;

    private Integer port;

    private Long avgCostTime;

    private InstanceInfo instanceInfo;

    private ProtocolConfig protocolConfig;

    public EndPointInfoMsg() {
    }

    public EndPointInfoMsg(String host, Integer port, Long avgCostTime, InstanceInfo instanceInfo, ProtocolConfig protocolConfig) {
        this.host = host;
        this.port = port;
        this.avgCostTime = avgCostTime;
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

    public Long getAvgCostTime() {
        return avgCostTime;
    }

    public void setAvgCostTime(Long avgCostTime) {
        this.avgCostTime = avgCostTime;
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
                ", avgCostTime=" + avgCostTime +
                ", instanceInfo=" + instanceInfo +
                ", protocolConfig=" + protocolConfig +
                '}';
    }
}
