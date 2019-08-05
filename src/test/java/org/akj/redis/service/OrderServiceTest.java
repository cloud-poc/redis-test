package org.akj.redis.service;

import org.akj.redis.entity.Order;
import org.akj.redis.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    private ProductService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        order.setId("7f55a0df-6ce4-443e-8204-f20234915c3a");
        order.setNotes("please ship them to me using SF express");
        Product p = service.findById("08de47f8-a652-4f87-9d80-70ba5712cac5");
        order.setTotalPrice(p.getPrice());
        order.setProducts(Arrays.asList(p));

        Order orderCreated = orderService.createOrder(order);

        assertEquals(order, orderCreated);
    }

    @Test
    public void testGetOrderStatus() {
        Order order = new Order();
        order.setId("7f55a0df-6ce4-443e-8204-f20234915c3a");
        Map<String, List<String>> orderStatus = orderService.getOrderStatus(order);

        System.out.println("==========Order status===========");
        orderStatus.get("remaining").forEach(System.out::println);
    }

    @Test
    public void testUpdateOrderStatus() {
        Order order = new Order();
        order.setId("7f55a0df-6ce4-443e-8204-f20234915c3a");
        orderService.updateOrderStatus(order);
        Map<String, List<String>> orderStatus = orderService.getOrderStatus(order);

        System.out.println("==========Order status===========");
        System.out.println("[Remaining]");
        orderStatus.get("remaining").forEach(System.out::println);
        System.out.println("[Finished]");
        orderStatus.get("finished").forEach(System.out::println);
    }

    @Test
    public void testFindOrderById() {
        Order order = orderService.findOrderById("7f55a0df-6ce4-443e-8204-f20234915c3a");
        System.out.println(order);
    }
}