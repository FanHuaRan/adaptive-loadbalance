package com.aliware.tianchi.core;

import org.apache.dubbo.rpc.Invoker;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description 动态权重源
 */
public interface DynamicInvokerWeight {
    /**
     * 获取invoker对应权重
     *
     * @param invoker
     * @return
     */
    Integer getWeight(Invoker invoker);
}
