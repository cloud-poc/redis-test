package org.akj.redis.service;

import org.akj.redis.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Order order);

    Map<String, List<String>> getOrderStatus(Order order);

    boolean updateOrderStatus(Order order);

    Order findOrderById(String id);
}
