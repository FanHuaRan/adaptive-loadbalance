package com.aliware.tianchi;

import com.aliware.tianchi.amp.HardCodeMetricImpl;
import com.aliware.tianchi.amp.Metric;
import com.aliware.tianchi.core.OfflineDynamicInvokerWeight;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.util.Date;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {

    private Metric metric = HardCodeMetricImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Date start = new Date();
            metric.invokeStart(invoker);

            Result result = invoker.invoke(invocation);

            Date end = new Date();
            metric.invokeEnd(invoker, start, end.getTime() - start.getTime());

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
