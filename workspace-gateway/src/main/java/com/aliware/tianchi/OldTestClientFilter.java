package com.aliware.tianchi;

import com.aliware.tianchi.metric.InvokerMetric;
import com.aliware.tianchi.metric.LeapWindowInvokerMetricImpl;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Deprecated
@Activate(group = Constants.CONSUMER)
public class OldTestClientFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OldTestClientFilter.class);

    private InvokerMetric metric = LeapWindowInvokerMetricImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 记录响应起始时间
            long start = System.currentTimeMillis();
            // 指标统计起始埋点
            metric.invokeStart(invoker);
            // 执行远程调用，注意调用是异步执行，想要获得结果可以通过下面这种方法
            Result result = invoker.invoke(invocation);

            // 添加一个回调
            CompletableFuture<Integer> completableFuture = RpcContext.getContext().getCompletableFuture();
            completableFuture.whenComplete((actual, t) -> {
                // 记录结束时间
                long end = System.currentTimeMillis();
                // LOGGER.info("<filter>cost:" + (end - start) + ",result:" + actual);
                // 埋点
                metric.invokeEnd(invoker, end - start);
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
