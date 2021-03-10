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
package com.alibaba.csp.sentinel.property;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.csp.sentinel.log.RecordLog;

public class DynamicSentinelProperty<T> implements SentinelProperty<T> {

    protected Set<PropertyListener<T>> listeners = Collections.synchronizedSet(new HashSet<PropertyListener<T>>());
    private T value = null;

    public DynamicSentinelProperty() {
    }

    public DynamicSentinelProperty(T value) {
        super();
        this.value = value;
    }

    @Override
    public void addListener(PropertyListener<T> listener) {
        listeners.add(listener);
        //添加监听器的时候 让这个监听器去加载值。
        //比如FlowRuleManager类中存在一个Listener属性，和SentinelProperty属性，
        //调用SentinelProperty传入listener， 然后调用listener的configLoad
        //而Listener 和FlowManager绑定着，SentinelProperty存在这属性，交给了Listener
        //本质就是交给了FlowManager，因为在 FlowPropertyListener的configLoad中我们看到
        //  FlowManager.flowRules.set(rules);也就是说直接使用SentinelProperty中存在的值替换掉了FlowManager中
        //flowRules中的值
        listener.configLoad(value);
    }

    @Override
    public void removeListener(PropertyListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * SentinelProperty中的值 什么时候来的？
     * DataSource启动 时候回去加载数据，然后调用SentinelProperty的updateValue
     * ApolloDataSource.loadAndUpdateRules()  (com.alibaba.csp.sentinel.datasource.apollo)
     *     ApolloDataSource.initialize()  (com.alibaba.csp.sentinel.datasource.apollo)
     *         ApolloDataSource.ApolloDataSource(String, String, String, Converter<String, T>)  (com.alibaba.csp.sentinel.datasource.apollo)
     * @param newValue the new value.
     * @return
     */
    @Override
    public boolean updateValue(T newValue) {
        if (isEqual(value, newValue)) {
            return false;
        }
        RecordLog.info("[DynamicSentinelProperty] Config will be updated to: {}", newValue);

        //更新的时候 如果不相等，直接替换原来的SentinelProperty中 保存的值，
        //然后在调用每一个Listener告知每一个Listener
        value = newValue;

        for (PropertyListener<T> listener : listeners) {
            listener.configUpdate(newValue);
        }
        return true;
    }

    private boolean isEqual(T oldValue, T newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }

        if (oldValue == null) {
            return false;
        }

        return oldValue.equals(newValue);
    }

    public void close() {
        listeners.clear();
    }
}
