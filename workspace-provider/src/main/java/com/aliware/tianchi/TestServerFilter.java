package com.aliware.tianchi;

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
 *
 * 服务端过滤器
 * 可选接口
 * 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {
    private final ProviderCostAvgTimeRecorder providerCostAvgTimeRecorder = HardCodeProviderCostAvgTimeRecorderImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try{
            Date start = new Date();
            Result result = invoker.invoke(invocation);
            Date end = new Date();

            providerCostAvgTimeRecorder.recordCostTime(start, end.getTime() - start.getTime());

            return result;
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }

}
