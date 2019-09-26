package com.aliware.tianchi;

import com.aliware.tianchi.model.EndPointInfoMsg;
import com.aliware.tianchi.model.InstanceInfo;
import com.aliware.tianchi.model.PerformanceIndicator;
import com.aliware.tianchi.core.RealTimeDynamicInvokerWeight;
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

    private RealTimeDynamicInvokerWeight realTimeDynamicInvokerWeight = RealTimeDynamicInvokerWeight.getInstance();

    @Override
    public void receiveServerMsg(String msg) {
        System.out.println("receive msg from server :" + msg);
        Gson gson = new Gson();
        EndPointInfoMsg endPointInfoMsg = gson.fromJson(msg, EndPointInfoMsg.class);

        RpcContext rpcContext = RpcContext.getContext();
        InetSocketAddress inetSocketAddress = rpcContext.getRemoteAddress();

        String host = inetSocketAddress.getHostName();
        Integer port = inetSocketAddress.getPort();
        Integer threads = endPointInfoMsg.getProtocolConfig().getThreads();
        Integer cpuCore = endPointInfoMsg.getInstanceInfo().getCpuCore();

//        realTimeDynamicInvokerWeight.setThreadCount(host, port, threads);
//        realTimeDynamicInvokerWeight.setCpuCore(host, port, cpuCore);

        // 被压init
        for (int i = 0; i < threads; i++){
            UserLoadBalance.addWorkRequest(port);
        }

    }

}
