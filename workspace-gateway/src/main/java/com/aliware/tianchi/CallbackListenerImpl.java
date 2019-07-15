package com.aliware.tianchi;

import com.google.gson.Gson;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 *
 */
public class CallbackListenerImpl implements CallbackListener {
    private DynamicInvokerWeight dynamicInvokerWeight = DynamicInvokerWeight.getInstance();
    @Override
    public void receiveServerMsg(String msg) {
         System.out.println("receive msg from server :" + msg);
        Gson gson = new Gson();
        EndPointInfoMsg endPointInfoMsg = gson.fromJson(msg, EndPointInfoMsg.class);

        String host = endPointInfoMsg.getHost();
        Integer port = endPointInfoMsg.getPort();
        // TODO weight的科学计算
        Integer weight = endPointInfoMsg.getProtocolConfig().getThreads();

        dynamicInvokerWeight.setWeight(host, port, weight);
    }

}
