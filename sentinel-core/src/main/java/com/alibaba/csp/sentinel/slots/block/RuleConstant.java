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
package com.alibaba.csp.sentinel.slots.block;

import com.alibaba.csp.sentinel.node.IntervalProperty;

/**
 * @author youji.zj
 * @author jialiang.linjl
 */
public final class RuleConstant {

    public static final int FLOW_GRADE_THREAD = 0;
    public static final int FLOW_GRADE_QPS = 1;

    public static final int DEGRADE_GRADE_RT = 0;
    /**
     * Degrade by biz exception ratio in the current {@link IntervalProperty#INTERVAL} second(s).
     */
    public static final int DEGRADE_GRADE_EXCEPTION_RATIO = 1;
    /**
     * Degrade by biz exception count in the last 60 seconds.
     */
    public static final int DEGRADE_GRADE_EXCEPTION_COUNT = 2;

    public static final int DEGRADE_DEFAULT_SLOW_REQUEST_AMOUNT = 5;
    public static final int DEGRADE_DEFAULT_MIN_REQUEST_AMOUNT = 5;

    public static final int AUTHORITY_WHITE = 0;
    public static final int AUTHORITY_BLACK = 1;

    public static final int STRATEGY_DIRECT = 0;
    public static final int STRATEGY_RELATE = 1;
    public static final int STRATEGY_CHAIN = 2;

    public static final int CONTROL_BEHAVIOR_DEFAULT = 0;
    public static final int CONTROL_BEHAVIOR_WARM_UP = 1;
    /**
     * 漏桶算法，又称leaky bucket。为了理解漏桶算法，我们看一下对于该算法的示意图：
     * https://zhuanlan.zhihu.com/p/135889557
     *
     * 从图中我们可以看到，整个算法其实十分简单。首先，我们有一个固定容量的桶，有水流进来，也有水流出去。对于流进来的水来说，我们无法预计一共有多少水会流进来，
     * 也无法预计水流的速度。但是对于流出去的水来说，这个桶可以固定水流出的速率。而且，当桶满了之后，多余的水将会溢出。
     *
     * 我们将算法中的水换成实际应用中的请求，我们可以看到漏桶算法天生就限制了请求的速度。当使用了漏桶算法，我们可以保证接口会以一个常速速率来处理请求。所以漏桶算法天生不会出现临界问题。
     *
     * 漏桶算法可以粗略的认为就是注水漏水过程，往桶中以一定速率流出水，以任意速率流入水，当水超过桶流量则丢弃，因为桶容量是不变的，保证了整体的速率。
     * QPS设置为10，表示每秒通过10个请求，当我设置超时时间为8秒，那么假设来了 100个请求，0刻开始通过10个请求，然后其他的请求开始计算超时时间，
     * 在未来的超时时间内8秒内预计可通过80个请求，因此最终通过90个请求，必定存在10个请求是失败的。 所以针对这10个请求我们有两种处理方式
     * （1）剩余90个请求一起处于等待状态，每过1秒放行10个请求，最终10个请求等待了超过8秒 后失败
     * （2）迅速让十个请求提前失败， 其他80个请求匀速缓慢通过。
     * 漏桶算法就是第二种。
     *
     * 漏桶 算法的关键在在于，桶的容量是已知的，流出速度是已知的， 流进速度是未知的。桶的容量是已知的对应着 在未来的超时8秒内系统可以处理的
     * 请求数量是已知一定的，也是可预计的，因此对于超过计算能力的请求全部 迅速失败。 桶的容量是已知的，当水太多的时候，这些水直接溢出，而不是等待
     * 一定时间后再溢出
     *
     *
     */
    public static final int CONTROL_BEHAVIOR_RATE_LIMITER = 2;
    public static final int CONTROL_BEHAVIOR_WARM_UP_RATE_LIMITER = 3;

    public static final int DEFAULT_BLOCK_STRATEGY = 0;
    public static final int TRY_AGAIN_BLOCK_STRATEGY = 1;
    public static final int TRY_UNTIL_SUCCESS_BLOCK_STRATEGY = 2;

    public static final int DEFAULT_RESOURCE_TIMEOUT_STRATEGY = 0;
    public static final int RELEASE_RESOURCE_TIMEOUT_STRATEGY = 1;
    public static final int KEEP_RESOURCE_TIMEOUT_STRATEGY = 2;

    public static final String LIMIT_APP_DEFAULT = "default";
    public static final String LIMIT_APP_OTHER = "other";

    public static final int DEFAULT_SAMPLE_COUNT = 2;
    public static final int DEFAULT_WINDOW_INTERVAL_MS = 1000;

    private RuleConstant() {}
}
