package com.aliware.tianchi;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Fan Huaran
 * created on 2019/9/26
 * @description
 */
public class WorkRequest implements Comparable<WorkRequest>{

    private final int port;

    private final Double  latency;

    public WorkRequest(int port) {
        this.port = port;
        this.latency = null;
    }

    public WorkRequest(int port, Double  latency) {
        this.port = port;
        this.latency = latency  + ThreadLocalRandom.current().nextDouble();
    }

    public int getPort() {
        return port;
    }

    public Double  getLatency() {
        return latency;
    }

    @Override
    public int compareTo(WorkRequest o) {
        return latency.compareTo(o.latency);
    }
}
