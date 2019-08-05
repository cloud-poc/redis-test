package org.akj.redis.service;

import org.akj.redis.entity.Amount;
import org.akj.redis.entity.Product;
import org.akj.redis.repository.ProductRepository;
import org.akj.springboot.common.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    @Test()
    public void test() {
        Assertions.assertThrows(BusinessException.class, () -> {
            service.findById("test");
        });
    }

    @RepeatedTest(value = 2)
    public void test1() {
        Product p = service.findById("44ddbe50-0135-4264-92dc-273784f61997");
        assertNotNull(p);
        System.out.println(p);
    }

    @Test
    public void test2() {
        Random random = new Random(20000);
        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < 100000; i++) {
            Product product = new Product();
            product.setName("PRODUCT-PRE-" + (i + 1));
            product.setDescription(UUID.randomUUID().toString().replace("-", ""));
            product.setCode("Z90" + (i + 1));
            Amount amount = new Amount();
            amount.setCurrency(random.nextInt(5) % 2 == 0 ? "HKD" : "CNY");
            amount.setAmount(BigDecimal.valueOf(random.nextInt(20000)));

            product.setPrice(amount);
            products.add(product);

            if (i % 50 == 0) {
                repository.saveAll(products);
                products.clear();
            }
        }
        repository.saveAll(products);
    }

}