package com.cyp.controller;

import com.alibaba.fastjson.JSON;
import com.cyp.domain.Order;
import com.cyp.domain.Product;
import com.cyp.service.OrderService;
import com.cyp.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

//@RestController
@Slf4j
public class OrderController2 {


    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    // 下单，fegin负载均衡
    @GetMapping("/order/prod/{pid}")
    public Order order(@PathVariable("pid") Integer pid){
        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");

        //调用商品微服务
        Product product = productService.findByPid(pid);

        //模拟调用商品微服务需要2s的时间
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));

        Order order = new Order();
        order.setUid(1);
        order.setUsername("测试用户");
        order.setPid(product.getPid());
        order.setPname(product.getPname());
        order.setPprice(product.getPprice());
        order.setNumber(1);

//        orderService.save(order);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return order;
    }

    //测试高并发
    @RequestMapping("/order/message")
    public String message() {
        return "测试高并发";
    }

}
