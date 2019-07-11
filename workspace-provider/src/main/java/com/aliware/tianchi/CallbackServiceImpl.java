package com.aliware.tianchi;

import com.google.gson.Gson;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.listener.CallbackListener;
import org.apache.dubbo.rpc.service.CallbackService;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daofeng.xjf
 * <p>
 * 服务端回调服务
 * 可选接口
 * 用户可以基于此服务，实现服务端向客户端动态推送的功能
 */
public class CallbackServiceImpl implements CallbackService {

    public CallbackServiceImpl() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!listeners.isEmpty()) {
                    // 有以下几个方法可以获取部分dubbo相关信息
                    // 1.org.apache.dubbo.config.context.ConfigManager.getInstance()  强烈推荐！
                    // 2.ExtensionLoader.getExtensionLoader(Protocol.class).getLoadedExtension("dubbo")
                    // 3.org.apache.dubbo.rpc.model.ApplicationModel
                    // 4.org.apache.dubbo.rpc.RpcContext 不在上下文中通过上下文获取？
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
//                            entry.getValue().receiveServerMsg(System.getProperty("quota") + " " + new Date().toString());
                            entry.getValue().receiveServerMsg(buildMessage());
                        } catch (Throwable t1) {
                            listeners.remove(entry.getKey());
                        }
                    }
                }
            }
        }, 0, 5000);
    }

    private Timer timer = new Timer();

    /**
     * key: listener type
     * value: callback listener
     */
    private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void addListener(String key, CallbackListener listener) {
        listeners.put(key, listener);
        listener.receiveServerMsg(new Date().toString()); // send notification for change
    }

    private String buildMessage(){
        RpcContext rpcContext = RpcContext.getServerContext();
        String host = rpcContext.getLocalHost();
        Integer port = rpcContext.getLocalPort();
        // TODO 获取服务端信息 或者客户端通过Invoker获取？
        EndPointInfoMsg endPointInfoMsg = new EndPointInfoMsg(host, port, InstanceInfoUtils.getInstanceInfo());

        Gson gson = new Gson();
        return gson.toJson(endPointInfoMsg);
    }
}
