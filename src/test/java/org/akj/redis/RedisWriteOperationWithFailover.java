package org.akj.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
public class RedisWriteOperationWithFailover {
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void test() {
        final String testKeyPrefix = "test:sentinel:sequence:";
        IntStream.range(0, 20).forEach(i -> {
            String key = testKeyPrefix + i;
            log.info("write record to redis, key: {}, value:{}", key, i);
            redisTemplate.opsForValue().set(key, i, Duration.ofMinutes(1));
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
