package com.example.test.security.config;

import com.example.test.security.exception.AccessDeniedExceptionHandler;
import com.example.test.security.exception.AuthenticationExceptionHandler;
import com.example.test.security.filter.MemberAuthenticationFilter;
import com.example.test.security.authentication.MemberAuthenticationConverter;
import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.properties.security.SecurityCorsProperties;
import com.example.test.security.provider.MemberAuthenticationProvider;
import com.example.test.security.userdetails.MemberDetailsService;
import com.example.test.security.utility.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   AuthenticationFilter authenticationFilter,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   AccessDeniedHandler accessDeniedHandler) throws Exception {
        http.cors().configurationSource(corsConfigurationSource);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .rememberMe().disable();

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.POST, "/user/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .anyRequest().authenticated()
        );

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console()); // H2 콘솔에 대한 Security 무시
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(final SecurityCorsProperties properties) {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(properties.isAllowCredentials());
        corsConfiguration.setAllowedHeaders(properties.getAllowedHeaders());
        corsConfiguration.setAllowedMethods(properties.getAllowedMethods());
        corsConfiguration.setAllowedOrigins(properties.getAllowedOrigins());
        corsConfiguration.setMaxAge(corsConfiguration.getMaxAge());

        final UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return corsConfigurationSource;
    }

    @Bean
    public AuthenticationFilter authenticationFilter(final AuthenticationManager authenticationManager,
                                                     final AuthenticationConverter authenticationConverter,
                                                     final AuthenticationSuccessHandler authenticationSuccessHandler,
                                                     final AuthenticationFailureHandler authenticationFailureHandler) {
        System.out.println("filter 입장완료");
        final MemberAuthenticationFilter authenticationFilter =
                new MemberAuthenticationFilter(authenticationManager, authenticationConverter);

        authenticationFilter.setSuccessHandler(authenticationSuccessHandler);
        authenticationFilter.setFailureHandler(authenticationFailureHandler);

        return authenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("manager 입장완료");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationConverter authenticationConverter() {
        System.out.println("converter 입장완료");
        return new MemberAuthenticationConverter();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService) {
        System.out.println("provider 입장완료");
        return new MemberAuthenticationProvider(authenticationUserDetailsService);
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService(final @Qualifier("accessTokenProvider") JwtProvider accessTokenProvider) {
        System.out.println("memberservice 입장완료");
        return new MemberDetailsService(accessTokenProvider);
    }

    // Handler

    @Bean(name = "authenticationSuccessHandler")
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(final AuthenticationEntryPoint authenticationEntryPoint) {
        return new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(final JwtProvider accessTokenProvider,
                                                             final JwtProvider refreshTokenProvider,
                                                             final ObjectMapper objectMapper) {
        return new AuthenticationExceptionHandler(accessTokenProvider, refreshTokenProvider, objectMapper);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(final ObjectMapper objectMapper) {
        return new AccessDeniedExceptionHandler(objectMapper);
    }

    @Bean(name = "accessTokenProvider")
    public JwtProvider accessTokenProvider(final AccessTokenProperties properties) {
        System.out.println("atp 입장완료");
        return new JwtProvider(properties);
    }

    @Bean(name = "refreshTokenProvider")
    public JwtProvider refreshTokenProvider(final RefreshTokenProperties properties) {
        System.out.println("rtp 입장완료");
        return new JwtProvider(properties);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
