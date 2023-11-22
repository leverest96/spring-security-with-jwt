package spring.security.boot2.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "jwt.access-token")
@ConstructorBinding
public class AccessTokenProperties extends JwtProperties {
    public static final String COOKIE_NAME = "access_token";

    public AccessTokenProperties(final String secretKey, final int validSeconds) {
        super(secretKey, validSeconds);
    }

    @RequiredArgsConstructor
    @Getter
    public enum AccessTokenClaim {
        MEMBER_ID("memberId"),
        LOGIN_ID("loginId");

        private final String claim;
    }
}