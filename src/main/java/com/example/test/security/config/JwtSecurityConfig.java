package com.example.test.security.config;

import com.example.test.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
// SecurityConfigurerAdapter 클래스를 상속받아서 추가로 필요한 설정들을 추가
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    // AuthenticationManager 객체는 SecurityConfig 클래스로부터 주입받을 예정
    private final AuthenticationManager authenticationManager;

    @Override
    public void configure(HttpSecurity http) {
        // JwtAuthenticationFilter를 LogoutFilter 다음에 실행되도록 추가
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
        http.addFilterAfter(filter, LogoutFilter.class);
    }
}
