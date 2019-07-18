package com.aliware.tianchi;

import com.aliware.tianchi.amp.EndPointInfoMsg;
import com.aliware.tianchi.amp.InstanceInfo;
import com.aliware.tianchi.amp.PerformanceIndicator;
import com.aliware.tianchi.core.DynamicInvokerWeight;
import com.google.gson.Gson;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.listener.CallbackListener;

import java.net.InetSocketAddress;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {
    private DynamicInvokerWeight dynamicInvokerWeight = DynamicInvokerWeight.getInstance();

    @Override
    public void receiveServerMsg(String msg) {
        System.out.println("receive msg from server :" + msg);
        Gson gson = new Gson();
        EndPointInfoMsg endPointInfoMsg = gson.fromJson(msg, EndPointInfoMsg.class);
        RpcContext rpcContext = RpcContext.getContext();
        InetSocketAddress inetSocketAddress = rpcContext.getRemoteAddress();

        String host = inetSocketAddress.getHostName();
        Integer port = inetSocketAddress.getPort();
        // TODO weight的科学计算
        // Integer weight = endPointInfoMsg.getProtocolConfig().getThreads() * endPointInfoMsg.getInstanceInfo().getCpuCore();
        Integer threads = endPointInfoMsg.getProtocolConfig().getThreads();
        PerformanceIndicator performanceIndicator = endPointInfoMsg.getPerformanceIndicator();
        Long avgTime = null;
        if (performanceIndicator != null) {
            avgTime = performanceIndicator.getAvgCostTime();
        }

        Integer cpuCore = null;
        InstanceInfo instanceInfo = endPointInfoMsg.getInstanceInfo();
        if (instanceInfo != null) {
            cpuCore = instanceInfo.getCpuCore();
        }

        // Integer weight =  endPointInfoMsg.getInstanceInfo().getCpuCore();
        Integer weight = 0;
        if (threads != null && avgTime != null && cpuCore != null) {
            weight = (int) (cpuCore * threads / avgTime);
        }
        dynamicInvokerWeight.setStatisticsWeight(host, port, weight);
    }

}
