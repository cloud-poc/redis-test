package org.akj.redis.service.mock;

import org.akj.redis.entity.Order;
import org.akj.redis.repository.OrderRepository;
import org.akj.redis.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceMock implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;

    private LinkedList<String> mockedOrderProcessFlow = new LinkedList<String>();

    @Autowired
    private OrderRepository repository;

    @Override
    public Order createOrder(Order order) {
        // 1. save order into database
        this.repository.save(order);

        //2. start order flow in redis
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("order:" + order.getId(), mockedOrderProcessFlow);
        return order;
    }

    @Override
    public Map<String, List<String>> getOrderStatus(Order order) {
        Map<String, List<String>> orderStatus = new HashMap<String, List<String>>();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        // 1.get all remaining step status
        List<String> range = listOperations.range("order:" + order.getId(), 0, -1);
        orderStatus.put("remaining", range);

        // 2. get finished step status
        String key = "order:finished:" + order.getId();
        if (redisTemplate.hasKey(key)) {
            List<String> range1 = listOperations.range(key, 0, -1);
            orderStatus.put("finished", range1);
        } else {
            orderStatus.put("finished", new ArrayList<String>(0));
        }

        return orderStatus;
    }

    @Override
    public boolean updateOrderStatus(Order order) {
        String keyForRemaining = "order:" + order.getId();
        String keyForFinished = "order:finished:" + order.getId();
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPopAndLeftPush(keyForRemaining, keyForFinished, 3, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public Order findOrderById(String id) {
        Optional<Order> byId = this.repository.findById(id);
        Order order = null;
        if(byId.isPresent()){
            order = byId.get();
        }

        return order;
    }

    @PostConstruct
    private void init() {
        mockedOrderProcessFlow.addAll(Arrays.asList("1.payment journey", "2.ship address confirmation(optional)", "3.delivery to express company for shipping", "4.Customer receive and confirmed", "5.Order finished"));
    }
}
