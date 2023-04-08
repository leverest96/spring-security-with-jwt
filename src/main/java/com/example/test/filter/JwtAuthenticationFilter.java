package com.example.test.filter;

import com.example.test.security.tokens.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 1. 가장 먼저 요청을 받음

@RequiredArgsConstructor
// OncePerRequestFilter를 통해 요청당 1회 수행
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Authorization key
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 인증 타입
    public static final String BEARER_PREFIX = "Bearer ";

    // 필터 내부에서 사용할 AuthenticationManager를 외부로부터 전달받음
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 추출한 토큰을 확인
        String jwt = resolveToken(request);

        // jwt 문자열의 유효성을 검증
        if (StringUtils.hasText(jwt)) {
            try {
                // 추출한 토큰을 기반으로 Token을 생성
                Authentication jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
                // AuthenticationManager에게 token을 전달하여 인증 요청
                Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);
                // 인증 성공시 SecurityContextHolder 클래스에 token을 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException authenticationException) {
                // 오류 발생시 인증 정보 제거
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    // header에 Authorization key가 있는지 확인
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // 인증 토큰의 인증 타입이 Bearer인지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // 맞다면 Bearer를 제외한 토큰을 추출하여 반환
            return bearerToken.substring(7);
        }

        return null;
    }
}
