package com.sachin;

import com.alibaba.csp.sentinel.init.InitExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ApplicationStart {

    public static void main(String[] args) {
        //triggerSentinelInit();
        SpringApplication.run(ApplicationStart.class, args);
    }

    /*private static void triggerSentinelInit() {
        new Thread(() -> InitExecutor.doInit()).start();
    }*/
}
