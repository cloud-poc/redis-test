package org.akj.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class BitmapUnitTest {

    private static RedisClient redisClient;

    private static StatefulRedisConnection<String, String> connection;

    @BeforeAll
    public static void setup() {
        //redisClient = RedisClient.create("redis://localhost:6379/0");
        redisClient = RedisClient.create(RedisURI.Builder
                .redis("localhost", 6379).withDatabase(0)
                .build());
        connection = redisClient.connect();
    }

    @Test
    public void test() {
        String key = "test:bitmaps:comparison";
        List<Integer> list = List.of(10, 39, 44, 20, 75, 63, 25, 44, 29, 21, 65, 20);
        log.info("List before sorting,[{}]", list);
        RedisCommands<String, String> syncCommands = connection.sync();

        list.forEach(v -> {
            syncCommands.setbit(key, v, 1);
        });

        List<Integer> sortedList = new ArrayList<>(list.size());
        IntStream.range(1, list.stream().max(Integer::compareTo).get() + 1).forEach(i -> {
            if (list.contains(i) && syncCommands.getbit(key, Integer.valueOf(i)).intValue() == 1) {
                sortedList.add(i);
            }
        });
        log.info("List after sorting,[{}]", sortedList);
    }
}
