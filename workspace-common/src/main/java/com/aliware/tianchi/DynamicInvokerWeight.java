package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.listener.ListenerInvokerWrapper;
import org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fan Huaran
 * created on 2019/7/15
 * @description
 */
public class DynamicInvokerWeight {
    private Map<Tuple<String, Integer>, Integer> weights = new ConcurrentHashMap();

//    private Map<String, String> hostIpMap = new ConcurrentHashMap<>();

    private DynamicInvokerWeight(){

    }

    public static DynamicInvokerWeight getInstance(){
        return Inner.DYNAMIC_INVOKER_WEIGHT;
    }

    public Integer getWeight(Invoker invoker){
        URL url = invoker.getUrl();

        String host = url.getHost();
        Integer port = url.getPort();
        Tuple<String, Integer> tuple = new Tuple<>(host, port);

        return weights.get(tuple);
    }

    public void setWeight(String host, Integer port, Integer weight){
        Tuple<String, Integer> tuple = new Tuple<>(host, port);
        weights.put(tuple, weight);
    }

    private static final class Inner {
        private static final DynamicInvokerWeight DYNAMIC_INVOKER_WEIGHT = new DynamicInvokerWeight();

    }

}
