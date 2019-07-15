package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fan Huaran
 * created on 2019/7/15
 * @description
 */
public class DynamicInvokerWeight {
    private final Map<Tuple<String, Integer>, Integer> writeWeights = new ConcurrentHashMap();

    private volatile Map<Tuple<String, Integer>, Integer> readWeights = new HashMap<>();

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

        return writeWeights.get(tuple);
    }

    public void setWeight(String host, Integer port, Integer weight){
        Tuple<String, Integer> tuple = new Tuple<>(host, port);
        writeWeights.put(tuple, weight);

        this.readWeights = new HashMap<>(writeWeights);
    }

    private static final class Inner {
        private static final DynamicInvokerWeight DYNAMIC_INVOKER_WEIGHT = new DynamicInvokerWeight();

    }

}
