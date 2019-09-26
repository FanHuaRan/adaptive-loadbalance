package com.aliware.tianchi;

import io.netty.util.concurrent.Promise;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Fan Huaran
 * created on 2019/9/26
 * @description 被压实现
 */
public class UserLoadBalance implements LoadBalance {

    private static final ConcurrentLinkedQueue<WorkRequest> WORK_REQUEST_CONCURRENT_LINKED_QUEUE = new ConcurrentLinkedQueue<>();

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokers.size() == 0){
            return invokers.get(0);
        }

        WorkRequest workRequest;
        while ( (workRequest = WORK_REQUEST_CONCURRENT_LINKED_QUEUE.poll()) == null){
            // 忙等
        }

        int port = workRequest.getPort();
        for (Invoker invoker : invokers){
            if (invoker.getUrl().getPort() == port){
                return invoker;
            }
        }

        return null;
    }

    public static void addWorkRequest(int port){
        WorkRequest workRequest = new WorkRequest(port);
       WORK_REQUEST_CONCURRENT_LINKED_QUEUE.offer(workRequest);
    }

}
