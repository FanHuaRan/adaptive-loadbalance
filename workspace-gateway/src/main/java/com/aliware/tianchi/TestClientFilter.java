package com.aliware.tianchi;

import com.aliware.tianchi.amp.HardCodeMetricImpl;
import com.aliware.tianchi.amp.Metric;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClientFilter.class);

    private Metric metric = HardCodeMetricImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            long start = System.currentTimeMillis();
            metric.invokeStart(invoker);

            Result result = invoker.invoke(invocation);

            CompletableFuture<Integer> completableFuture = RpcContext.getContext().getCompletableFuture();
            completableFuture.whenComplete((actual, t) -> {
                long end = System.currentTimeMillis();
//                LOGGER.info("<filter>cost:" + (end - start) + ",result:" + actual);
                metric.invokeEnd(invoker, new Date(start), end - start);
            });

            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }
}
