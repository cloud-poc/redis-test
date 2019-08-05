package org.akj.redis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.akj.redis.entity.Amount;
import org.akj.redis.entity.Product;
import org.akj.redis.repository.ProductRepository;
import org.akj.redis.service.ProductService;
import org.akj.springboot.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private static final String KEY_NAME_PREFIX = "product:";
    @Autowired
    private ProductRepository repository;
    @Autowired
    private RedisTemplate redisTemplate;

    private List<String> KeyList = Arrays.asList(new String[]{"id", "name", "description", "price.amount", "price.currency"});

    public Product findById(String id) {
        String key = KEY_NAME_PREFIX + id;
        HashOperations hashOperations = redisTemplate.opsForHash();
        Product product = null;

        if (redisTemplate.hasKey(key)) {
            log.info("loaded product info from Redis," + key);
            // TODO - to refactor - hard code the index
            product = new Product();
            List list = hashOperations.multiGet(key, KeyList);
            product.setId((String) list.get(0));
            product.setName((String) list.get(1));
            product.setDescription((String) list.get(2));

            Amount amount = new Amount();
            amount.setAmount(new BigDecimal((String) list.get(3)));
            amount.setCurrency((String) list.get(4));
            product.setPrice(amount);
        } else {
            Optional<Product> option = repository.findById(id);
            if (option.isPresent()) {
                log.info("loaded data from RDS," + key);
                product = option.get();

                log.info("cache product info into redis");
                Map<String, String> map = new Hashtable<String, String>();
                map.put("id", product.getId());
                map.put("name", product.getName());
                map.put("description", product.getDescription());
                map.put("price.amount", product.getPrice().getAmount().toString());
                map.put("price.currency", product.getPrice().getCurrency());
                hashOperations.putAll(key, map);
                redisTemplate.expire(key, 10, TimeUnit.MINUTES);
            } else {
                log.info("no product exists," + key);
                throw new BusinessException("Error-030-001", "no product exist with the id:" + id);
            }
        }

        return product;
    }
}
