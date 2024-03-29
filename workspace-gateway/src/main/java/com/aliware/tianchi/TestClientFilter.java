package com.aliware.tianchi;

import com.aliware.tianchi.metric.InvokerMetric;
import com.aliware.tianchi.metric.RelativeLeapWindowInvokerMetricImpl;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

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

    private static final int DEFAULT_TIME_OUT = 400;

    private InvokerMetric metric = RelativeLeapWindowInvokerMetricImpl.getInstance();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 记录响应起始时间
            long start = System.currentTimeMillis();
            // 指标统计起始埋点
//            metric.invokeStart(invoker);
            // 起始时间放入上下文缓存当中
            RpcContext.getContext().setAttachment("invoke_start", String.valueOf(start));
            // 设置超时时间
//            RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(DEFAULT_TIME_OUT));
//            setTimeout(invoker.getUrl(), invocation, String.valueOf(DEFAULT_TIME_OUT));
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

//        if (result.getException() == null) {
        // 记录结束时间
        long end = System.currentTimeMillis();
        // 从上下文中拿出起始时间
        long start = Long.valueOf(RpcContext.getContext().getAttachment("invoke_start"));
        costTime = end - start;
//        }
//
//        // 埋点
//        metric.invokeEnd(invoker, costTime);
        UserLoadBalance.recordLatency(invoker.getUrl().getPort(), costTime);

        return result;
    }

    private void setTimeout(URL url, Invocation invocation, String timeout) {
        String key = invocation.getMethodName() + "." + Constants.TIMEOUT_KEY;
        putParameter(url, key, timeout);
    }

    public static void putParameter(URL url, String key, String value) {
        try {
            if (StringUtils.isEquals(url.getParameter(key), value)) {
                return;
            }

            Field field = URL.class.getDeclaredField("parameters");
            field.setAccessible(true);

            updateUnmodifiedMap((Map) field.get(url), key, value);
            Method method = URL.class.getDeclaredMethod("buildString", boolean.class, boolean.class, String[].class);
            method.setAccessible(true);
            method.invoke(url, false, true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateUnmodifiedMap(Map unmodifiedMap, String key, String value) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        for (Class cl : classes) {
            if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(unmodifiedMap);
                Map map = (Map) obj;
                map.put(key, value);
            }
        }
    }
}
