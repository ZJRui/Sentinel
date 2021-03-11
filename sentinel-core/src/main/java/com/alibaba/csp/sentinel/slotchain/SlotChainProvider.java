/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slotchain;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.DefaultSlotChainBuilder;
import com.alibaba.csp.sentinel.spi.SpiLoader;

/**
 * A provider for creating slot chains via resolved slot chain builder SPI.
 *
 * @author Eric Zhao
 * @since 0.2.0
 */
public final class SlotChainProvider {

    //注意这里声明为了volatile类型
    private static volatile SlotChainBuilder slotChainBuilder = null;

    /**
     * 加载和选择过程不是线程安全的，但这没关系，因为该方法应该只被调用
     * *通过{@code lookProcessChain}在{@link com.alibaba.csp.sentinel。CtSph}下锁。
     * *
     * The load and pick process is not thread-safe, but it's okay since the method should be only invoked
     * via {@code lookProcessChain} in {@link com.alibaba.csp.sentinel.CtSph} under lock.
     *
     * @return new created slot chain
     */
    public static ProcessorSlotChain newSlotChain() {
        if (slotChainBuilder != null) {
            return slotChainBuilder.build();
        }

        // Resolve the slot chain builder SPI. 这个地方使用SpiLoad去读取 meat-inf、service 目录下SlotChainBuilder接口对应的文件中的实现
        //在配置文件中指定了SlotChainBuilder接口的唯一实现类 com.alibaba.csp.sentinel.slots.DefaultSlotChainBuilder
        slotChainBuilder = SpiLoader.of(SlotChainBuilder.class).loadFirstInstanceOrDefault();

        if (slotChainBuilder == null) {
            // Should not go through here.
            RecordLog.warn("[SlotChainProvider] Wrong state when resolving slot chain builder, using default");
            slotChainBuilder = new DefaultSlotChainBuilder();
        } else {
            RecordLog.info("[SlotChainProvider] Global slot chain builder resolved: {}",
                    slotChainBuilder.getClass().getCanonicalName());
        }
        return slotChainBuilder.build();//执行DefaultSlotChainBuilder的build方法
        //DefaultSlotChainBuilder的build方法中会 创建DefaultProcessorSlotChain对象，同时使用 SpiLoader.of(ProcessorSlot.class).loadInstanceListSorted(); 加载配置文件中配置的ProcessorSlot的默认实现类
    }

    private SlotChainProvider() {
    }
}
