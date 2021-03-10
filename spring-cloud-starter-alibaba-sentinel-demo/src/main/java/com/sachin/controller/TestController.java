package com.sachin.controller;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Collections;

@RestController
public class  TestController {
    @PostConstruct
    public void init() {
        initFlowRule();

    }
    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello Sentinel";
    }
    @GetMapping(value = "/updateRules")
    public String updateRules() {
        initFlowRule();
        return "Hello Sentinel";
    }


    private  void updateFlowRule() {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("/hello");
        flowRule.setCount(5);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        flowRule.setLimitApp("default");
        FlowRuleManager.loadRules(Collections.singletonList(flowRule));
    }


    private  void initFlowRule() {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("/hello");
        flowRule.setCount(5);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        flowRule.setLimitApp("default");
        FlowRuleManager.loadRules(Collections.singletonList(flowRule));
    }

}