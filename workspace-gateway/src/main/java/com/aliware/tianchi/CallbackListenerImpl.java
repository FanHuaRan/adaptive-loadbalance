package com.aliware.tianchi;

import com.google.gson.Gson;
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

    @Override
    public void receiveServerMsg(String msg) {
        Gson gson = new Gson();
        EndPointInfoMsg endPointInfoMsg = gson.fromJson(msg, EndPointInfoMsg.class);
        RpcContext.getContext().getUrl().getParameters()
        // todo 构建加权随机策略
        // System.out.println("receive msg from server :" + msg);
    }

}
