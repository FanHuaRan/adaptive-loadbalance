package com.aliware.tianchi;

/**
 * @author Fan Huaran
 * created on 2019/7/10
 * @description
 */
public class EndPointInfoMsg {
    private String host;

    private Integer port;

    private InstanceInfo instanceInfo;

    public EndPointInfoMsg() {
    }

    public EndPointInfoMsg(String host, Integer port, InstanceInfo instanceInfo) {
        this.host = host;
        this.port = port;
        this.instanceInfo = instanceInfo;
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

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    @Override
    public String toString() {
        return "EndpointInfoMsg{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", instanceInfo=" + instanceInfo +
                '}';
    }
}
