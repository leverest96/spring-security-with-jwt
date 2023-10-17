package spring.security.boot2.config;

import com.example.test.exception.handler.AccessDeniedExceptionHandler;
import com.example.test.exception.handler.AuthenticationExceptionHandler;
import com.example.test.security.filter.MemberAuthenticationFilter;
import com.example.test.security.authentication.MemberAuthenticationConverter;
import com.example.test.properties.jwt.AccessTokenProperties;
import com.example.test.properties.jwt.RefreshTokenProperties;
import com.example.test.properties.security.SecurityCorsProperties;
import com.example.test.security.authentication.MemberAuthenticationProvider;
import com.example.test.security.userdetails.MemberDetailsService;
import com.example.test.utility.JwtProvider;
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
import spring.security.boot2.handler.AccessDeniedExceptionHandler;
import spring.security.boot2.handler.AuthenticationExceptionHandler;

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

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/register", "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/admin").hasAuthority("관리자")
                .antMatchers(HttpMethod.POST, "/api/member/email/**").anonymous()
                .antMatchers(HttpMethod.POST, "/api/member").anonymous()
                .antMatchers(HttpMethod.POST, "/api/member/login").anonymous()
                .anyRequest().authenticated();

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
        final MemberAuthenticationFilter authenticationFilter =
                new MemberAuthenticationFilter(authenticationManager, authenticationConverter);

        authenticationFilter.setSuccessHandler(authenticationSuccessHandler);
        authenticationFilter.setFailureHandler(authenticationFailureHandler);

        return authenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationConverter authenticationConverter() {
        return new MemberAuthenticationConverter();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService) {
        return new MemberAuthenticationProvider(authenticationUserDetailsService);
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService(final @Qualifier("accessTokenProvider") JwtProvider accessTokenProvider) {
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
        return new JwtProvider(properties);
    }

    @Bean(name = "refreshTokenProvider")
    public JwtProvider refreshTokenProvider(final RefreshTokenProperties properties) {
        return new JwtProvider(properties);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}