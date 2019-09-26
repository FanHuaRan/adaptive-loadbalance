package com.aliware.tianchi;

import com.aliware.tianchi.core.RealTimeDynamicInvokerWeight;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
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
    private static final ConcurrentSkipListSet<WorkRequest> WORK_REQUESTS = new ConcurrentSkipListSet<>();

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokers.size() == 0) {
            return invokers.get(0);
        }

        WorkRequest workRequest;
        while ((workRequest = WORK_REQUESTS.pollFirst()) == null) {
            // 忙等
        }

        int port = workRequest.getPort();
        for (Invoker invoker : invokers) {
            if (invoker.getUrl().getPort() == port) {
                return invoker;
            }
        }

        return null;
    }

    public static void recordLatency(int port, long latency) {
        if (latency != -1) {
            if (latency < 3) {
                for (int i = 0; i < 1; i++) {
                    WORK_REQUESTS.pollLast();
                    WORK_REQUESTS.add(new WorkRequest(port, (double) latency));
                }
            }
            WORK_REQUESTS.add(new WorkRequest(port, 50D));
        }
    }
}
