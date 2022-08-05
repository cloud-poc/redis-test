package org.akj.redis;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ScanCommandTest {
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

    @SneakyThrows
    @Test
    public void test() {
        String queryKey = "test-string-*";
        RedisCommands<String, String> syncCommands = connection.sync();
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        ScanArgs scanArgs = new ScanArgs();
        scanArgs.match(queryKey);
        scanArgs.limit(2);
        KeyScanCursor<String> scanCursor = null;
        do {
            if (scanCursor == null) {
                scanCursor = syncCommands.scan(scanArgs);
            } else {
                scanCursor = syncCommands.scan(scanCursor, scanArgs);
            }

            // get all page per page
            keys.addAll(scanCursor.getKeys());
        }while(!scanCursor.isFinished());

        //todo: to replace it with pipelines
        keys.forEach(k -> {
            values.add(syncCommands.get(k));
        });

        log.info("Matched records, count: {}", values.size());
        values.forEach(System.out::println);
    }
}
