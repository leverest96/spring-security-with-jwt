package com.example.test.security.web.authentication;

import com.example.test.domain.redis.AccessToken;
import com.example.test.repository.RedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomAuthenticationConverter implements AuthenticationConverter {
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private RedisRepository redisRepository;

    public CustomAuthenticationConverter(final RedisRepository redisRepository) {
        this(new WebAuthenticationDetailsSource());
        this.redisRepository = redisRepository;
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        final Optional<AccessToken> redisAccessToken = redisRepository.findById(AccessToken.ACCESS_TOKEN_KEY);
        final String accessToken = redisAccessToken.map(AccessToken::getAccessToken).orElse((null));

        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(null, accessToken);

        result.setDetails(authenticationDetailsSource.buildDetails(request));

        return result;
    }
}
