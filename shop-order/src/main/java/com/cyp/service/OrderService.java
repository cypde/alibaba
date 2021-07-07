package com.cyp.service;

import com.cyp.dao.OrderDao;
import com.cyp.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;

public interface OrderService {

     void save(Order order);
}
