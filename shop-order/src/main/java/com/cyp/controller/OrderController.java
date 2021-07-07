package com.cyp.controller;

import com.alibaba.fastjson.JSON;
import com.cyp.domain.Order;
import com.cyp.domain.Product;
import com.cyp.service.OrderService;
import com.cyp.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

//@RestController
@Slf4j
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

//    // 准备买一件商品
//    @GetMapping("/order/prod/{pid}")
//    public Order order(@PathVariable("pid") Integer pid){
//        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");
//
    //        //调用商品微服务,查询商品信息
//        //问题:
//        //1 一旦服务提供者的地址信息变化了,我们就不得不去修改服务调用者的java代码
//        //2 一旦无法提供者做了集群,服务调用者一方无法实现负载均衡的去调用
//        //3 一旦微服务变得越来越多,如何来管理这个服务清单就成了问题
//        //通过restTemplate调用商品微服务
//        Product product = restTemplate.getForObject(
//                "http://localhost:8081/product/" + pid, Product.class);
//        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));
//
//        Order order = new Order();
//        order.setUid(1);
//        order.setUsername("测试用户");
//        order.setPid(product.getPid());
//        order.setPname(product.getPname());
//        order.setPprice(product.getPprice());
//        order.setNumber(1);
//
//        orderService.save(order);
//        return order;
//    }

//    // 准备买一件商品
//    @GetMapping("/order/prod/{pid}")
//    public Order order(@PathVariable("pid") Integer pid){
//        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");
//
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
//        ServiceInstance instance = instances.get(0);
//
//        //通过restTemplate调用商品微服务
//        Product product = restTemplate.getForObject(
//                "http://"+instance.getHost()+":"+instance.getPort()+"/product/" + pid, Product.class);
//
//        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));
//
//        Order order = new Order();
//        order.setUid(1);
//        order.setUsername("测试用户");
//        order.setPid(product.getPid());
//        order.setPname(product.getPname());
//        order.setPprice(product.getPprice());
//        order.setNumber(1);
//
//        orderService.save(order);
//        return order;
//    }

//    // 下单，自定义负载均衡
//    @GetMapping("/order/prod/{pid}")
//    public Order order(@PathVariable("pid") Integer pid){
//        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");
//
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
//        // 随机选择
//        int index = new Random().nextInt(instances.size());
//        ServiceInstance instance = instances.get(index);
//
//        //通过restTemplate调用商品微服务
//        Product product = restTemplate.getForObject(
//                "http://"+instance.getHost()+":"+instance.getPort()+"/product/" + pid, Product.class);
//
//        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));
//
//        Order order = new Order();
//        order.setUid(1);
//        order.setUsername("测试用户");
//        order.setPid(product.getPid());
//        order.setPname(product.getPname());
//        order.setPprice(product.getPprice());
//        order.setNumber(instance.getPort());
//
//        orderService.save(order);
//        return order;
//    }

//    // 下单，Ribbon负载均衡
//    @GetMapping("/order/prod/{pid}")
//    public Order order(@PathVariable("pid") Integer pid){
//        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");
//
//        //调用商品微服务,查询商品信息
//        //问题:
//        // 1 代码可读性不好
//        // 2 编程风格不统一
//        Product product =
//                restTemplate.getForObject("http://service-product/product/" + pid, Product.class);
//
//        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));
//
//        Order order = new Order();
//        order.setUid(1);
//        order.setUsername("测试用户");
//        order.setPid(product.getPid());
//        order.setPname(product.getPname());
//        order.setPprice(product.getPprice());
//        order.setNumber(instance.getPort());
//
//        orderService.save(order);
//        return order;
//    }

    // 下单，fegin负载均衡
    @GetMapping("/order/prod/{pid}")
    public Order order(@PathVariable("pid") Integer pid){
        log.info(">>客户下单，这时候要调用商品微服务查询商品信息");

        //通过fegin调用商品微服务
        Product product = productService.findByPid(pid);

        if (product.getPid() == -100){
            Order order = new Order();
            order.setOid(-100L);
            order.setPname("下单失败");
            return order;
        }

        log.info(">>商品信息,查询结果:" + JSON.toJSONString(product));

        Order order = new Order();
        order.setUid(1);
        order.setUsername("测试用户");
        order.setPid(product.getPid());
        order.setPname(product.getPname());
        order.setPprice(product.getPprice());
        order.setNumber(1);

        orderService.save(order);

        //向rocketmq中写入下单成功的消息
        //参数1 指定topic
        //参数2 指定消息体
        rocketMQTemplate.convertAndSend("order-topic",order);
        return order;
    }

}
