package com.example.test.security.web.authentication;

import com.example.test.repository.RedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends AuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;

    public CustomAuthenticationFilter(final AuthenticationManager authenticationManager,
                                      final AuthenticationConverter authenticationConverter,
                                      final RedisRepository redisRepository) {
        super(authenticationManager, authenticationConverter);
        this.authenticationManager = authenticationManager;
        this.redisRepository = redisRepository;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            final CustomAuthenticationConverter authenticationConverter = new CustomAuthenticationConverter(redisRepository);

            final Authentication jwtAuthenticationToken = authenticationConverter.convert(request);

            final Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (final AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
