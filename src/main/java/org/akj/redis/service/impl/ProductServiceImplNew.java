package org.akj.redis.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.akj.redis.entity.Product;
import org.akj.redis.repository.ProductRepository;
import org.akj.redis.service.ProductService;
import org.akj.springboot.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductServiceImplNew implements ProductService {
    private static final String KEY_NAME = "product";
    @Autowired
    private ProductRepository repository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Product findById(String id) {
        HashOperations<String,String, Product> hashOperations = redisTemplate.opsForHash();
        Product product = null;

        if (hashOperations.hasKey(KEY_NAME,id)) {
            log.info("loaded product info from Redis," + id);
            product = (Product)hashOperations.get(KEY_NAME,id);
        } else {
            Optional<Product> option = repository.findById(id);
            if (option.isPresent()) {
                log.info("loaded data from RDS," + id);
                product = option.get();

                log.info("cache product info into redis");
                hashOperations.put(KEY_NAME,id,product);
                redisTemplate.expire(KEY_NAME,10, TimeUnit.MINUTES);
            }else{
                log.info("no product exists," + id);
                throw new BusinessException("Error-030-001", "no product exist with the id:" + id);
            }
        }

        return product;
    }
}
