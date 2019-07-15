package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.listener.ListenerInvokerWrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fan Huaran
 * created on 2019/7/15
 * @description
 */
public class DynamicInvokerWeight {
    private Map<Tuple<String, Integer>, Integer> weights = new ConcurrentHashMap();

    private Map<String, String> hostIpMap = new ConcurrentHashMap<>();

    private DynamicInvokerWeight(){

    }

    public static DynamicInvokerWeight getInstance(){
        return Inner.DYNAMIC_INVOKER_WEIGHT;
    }

    public Integer getWeight(Invoker invoker){
        ListenerInvokerWrapper wrapper = (ListenerInvokerWrapper) invoker;
        URL url = wrapper.getUrl();
        String ip = hostIpMap.get(url.getHost());
        if (ip == null){
            try {
                ip = InetAddress.getByName(url.getHost()).getHostAddress();
            } catch (UnknownHostException e) {
                // ignore
            }
            hostIpMap.put(url.getHost(),ip);
        }
        Integer port = url.getPort();
        Tuple<String, Integer> tuple = new Tuple<>(ip, port);

        return weights.get(tuple);
    }

    public void setWeight(String ip, Integer port, Integer weight){
        Tuple<String, Integer> tuple = new Tuple<>(ip, port);
        weights.put(tuple, weight);
    }

    private static final class Inner {
        private static final DynamicInvokerWeight DYNAMIC_INVOKER_WEIGHT = new DynamicInvokerWeight();

    }

}
