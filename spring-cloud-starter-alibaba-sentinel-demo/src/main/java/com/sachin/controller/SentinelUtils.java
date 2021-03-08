package com.sachin.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class SentinelUtils {



    // 原本的业务方法.
    @SentinelResource(blockHandler = "blockHandlerForGetUser")
    public User getUserById(String id) {
        throw new RuntimeException("getUserById command failed");
    }

    // blockHandler 函数，原方法调用被限流/降级/系统保护的时候调用
    public User blockHandlerForGetUser(String id, BlockException ex) {
        return new User("admin");
    }
}
