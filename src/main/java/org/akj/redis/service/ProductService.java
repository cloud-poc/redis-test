package org.akj.redis.service;

import org.akj.redis.entity.Product;

public interface ProductService {
    Product findById(String id);
}
