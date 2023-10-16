package com.example.test.properties.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.cors")
@RequiredArgsConstructor
@Getter
public class SecurityCorsProperties {
    private final boolean allowCredentials;

    private final List<String> allowedHeaders;

    private final List<String> allowedMethods;

    private final List<String> allowedOrigins;

    private final long maxAge;
}