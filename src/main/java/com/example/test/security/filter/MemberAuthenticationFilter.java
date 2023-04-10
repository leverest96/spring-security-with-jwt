package com.example.test.security.filter;

import com.example.test.security.manager.MemberAuthenticationConverter;
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

// request 요청당 1회 가장 먼저 응답
// AuthenticationFilter는 OncePerRequestFilter가 extends 되어있다.
public class MemberAuthenticationFilter extends AuthenticationFilter {
    // 사용할 AuthenticationManager는 아래 생성자에서 initialize된다.
    private final AuthenticationManager authenticationManager;

    public MemberAuthenticationFilter(AuthenticationManager authenticationManager,
                                      AuthenticationConverter authenticationConverter) {
        super(authenticationManager, authenticationConverter);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("1번");
        try {
            // request를 AuthenticationConverter를 통해 Authentication 객체로 변환
            MemberAuthenticationConverter authenticationConverter = new MemberAuthenticationConverter();
            Authentication jwtAuthenticationToken = authenticationConverter.convert(request);
            // AuthenticationManager에게 token을 전달하여 인증 요청
            Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);
            // 인증 성공시 SecurityContextHolder 클래스에 token을 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException authenticationException) {
            // 오류 발생시 인증 정보 제거
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
