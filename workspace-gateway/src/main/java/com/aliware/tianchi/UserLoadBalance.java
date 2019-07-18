package com.aliware.tianchi;

import com.aliware.tianchi.core.RealTimeDynamicInvokerWeight;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {
    private RealTimeDynamicInvokerWeight dynamicInvokerWeight = RealTimeDynamicInvokerWeight.getInstance();

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
//        System.out.println("invokers:" + invokers +" url:" + url +
//                " invoker.class:" + invokers.get(0).getClass() + "invoker.interface:" + invokers.get(0));

        // Number of invokers
        int length = invokers.size();
        // Every invoker has the same weight?
        boolean sameWeight = true;
        // the weight of every invokers
        Integer[] weights = new Integer[length];
        // the first invoker's weight
        Integer firstWeight = dynamicInvokerWeight.getWeight(invokers.get(0));
        if (firstWeight == null) {
            // If weight is null, return evenly.
            return invokers.get(ThreadLocalRandom.current().nextInt(length));
        }

        weights[0] = firstWeight;
        // The sum of weights
        int totalWeight = firstWeight;
        for (int i = 1; i < length; i++) {
            Integer weight = dynamicInvokerWeight.getWeight(invokers.get(i));
            if (weight == null) {
                // If weight is null, return evenly.
                return invokers.get(ThreadLocalRandom.current().nextInt(length));
            }

            // save for later use
            weights[i] = weight;
            // Sum
            totalWeight += weight;
            if (sameWeight && weight != firstWeight) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                offset -= weights[i];
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }
}
