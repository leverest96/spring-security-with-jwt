package com.example.test.properties.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
@RequiredArgsConstructor
@Getter
public class RedisProperties {
    private final String host;
    private final int port;
}
