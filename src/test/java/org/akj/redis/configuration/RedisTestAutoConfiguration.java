package org.akj.redis.configuration;

import com.github.fppt.jedismock.operations.RedisOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(value = {RedisOperation.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
@Profile({"local", "integration-test"})
@Slf4j
public class RedisTestAutoConfiguration {
    @Bean
    public RedisServer redisServer() {
        final RedisServer redisServer = new RedisServer();
        return redisServer;
    }

    class RedisServer {
        private com.github.fppt.jedismock.RedisServer redisServer = null;

        @PostConstruct
        public void init() {
            this.start();
            System.setProperty("spring.redis.host", "localhost");
            System.setProperty("spring.redis.port", this.getPort() + "");
            log.info("Set redis host and port to system property, port={}", this.getPort());
        }

        public int getPort() {
            return this.redisServer.getBindPort();
        }

        @SneakyThrows
        public void start() {
            log.info("Starting redis service with jedis-mock.");
            redisServer = com.github.fppt.jedismock.RedisServer.newRedisServer();
            redisServer.start();
            log.info("Redis test instance has started.");
        }

        @SneakyThrows
        public void close() {
            log.info("Shutting down redis test instance.");
            if (redisServer != null) redisServer.stop();

            log.info("Redis test instance has stopped.");
        }

    }
}
