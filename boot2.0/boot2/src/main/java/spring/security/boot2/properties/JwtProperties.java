package spring.security.boot2.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class JwtProperties {
    private final String secretKey;

    private final int validSeconds;
}