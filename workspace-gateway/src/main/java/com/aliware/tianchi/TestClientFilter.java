package com.aliware.tianchi;

import com.aliware.tianchi.metric.InvokerMetric;
import com.aliware.tianchi.metric.LeapWindowInvokerMetricImpl;
import com.aliware.tianchi.metric.RelativeLeapWindowInvokerMetricImpl;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

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

    private InvokerMetric metric = RelativeLeapWindowInvokerMetricImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 记录响应起始时间
            long start = System.currentTimeMillis();
            // 指标统计起始埋点
            metric.invokeStart(invoker);
            // 起始时间放入上下文缓存当中
            RpcContext.getContext().setAttachment("invoke_start", String.valueOf(start));
            // 执行远程调用，注意调用是异步执行
            Result result = invoker.invoke(invocation);

            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        long costTime = -1;

        if (result.getException() == null){
            // 记录结束时间
            long end = System.currentTimeMillis();
            // 从上下文中拿出起始时间
            long start = Long.valueOf(RpcContext.getContext().getAttachment("invoke_start"));
            costTime = end -start;
        }

        // 埋点
        metric.invokeEnd(invoker, costTime);

        return result;
    }
}
