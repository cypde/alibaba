package com.cyp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 陈玉鹏
 * @Description: TODO
 * @DateTime: 2021/7/6 15:50
 */

//@RestController
//@RefreshScope //动态刷新的注解（推荐）
public class NacosConfigController {

    @Autowired
    private ConfigurableApplicationContext context;

    @Value("${config.appName}")
    private String appName;

    @Value("${config.env}")
    private String env;

    @GetMapping("/test-config1")
    public String testConfig1() {
        return context.getEnvironment().getProperty("config.appName");
    }

    @GetMapping("/test-config2")
    public String testConfig2() {
        return appName;
    }

    @GetMapping("/test-config3")
    public String testConfig3() {
        return env;
    }
}
