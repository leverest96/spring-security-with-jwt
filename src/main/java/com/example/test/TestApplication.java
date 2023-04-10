package com.example.test;

import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.properties.security.SecurityCorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        SecurityCorsProperties.class,
        AccessTokenProperties.class,
        RefreshTokenProperties.class
})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
