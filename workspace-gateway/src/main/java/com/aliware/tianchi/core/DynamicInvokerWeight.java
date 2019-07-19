package com.aliware.tianchi.core;

import org.apache.dubbo.rpc.Invoker;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public interface DynamicInvokerWeight {
    Integer getWeight(Invoker invoker);
}
