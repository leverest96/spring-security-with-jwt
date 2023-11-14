package spring.security.boot2.config;

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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spring.security.boot2.common.util.JwtProvider;
import spring.security.boot2.handler.AccessDeniedExceptionHandler;
import spring.security.boot2.handler.AuthenticationExceptionHandler;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.properties.SecurityCorsProperties;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.security.oauth2.converter.DelegatingProviderUserConverter;
import spring.security.boot2.security.oauth2.converter.ProviderUserConverter;
import spring.security.boot2.security.oauth2.converter.ProviderUserRequest;
import spring.security.boot2.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import spring.security.boot2.security.oauth2.oauth2user.OAuth2MemberService;
import spring.security.boot2.security.web.authentication.CustomAuthenticationConverter;
import spring.security.boot2.security.web.authentication.CustomAuthenticationFilter;
import spring.security.boot2.security.web.authentication.CustomAuthenticationProvider;
import spring.security.boot2.security.web.userdetails.MemberDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
//                                                  final CorsConfigurationSource corsConfigurationSource,
                                                  final AuthenticationFilter authenticationFilter,
                                                  final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService,
                                                  final AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                                                  final AuthenticationFailureHandler authenticationFailureHandler,
                                                  final AuthenticationEntryPoint authenticationEntryPoint,
                                                  final AccessDeniedHandler accessDeniedHandler) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/member/register", "/api/member/login").permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.cors().configurationSource(corsConfigurationSource);

//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.csrf().disable();

        http.httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .rememberMe().disable()
                .headers().disable();

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console());
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource(final SecurityCorsProperties properties) {
//        final CorsConfiguration corsConfiguration = new CorsConfiguration();
//
//        corsConfiguration.setAllowCredentials(properties.isAllowCredentials());
//        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
//        corsConfiguration.setAllowedOrigins(properties.getAllowedOrigins());
//        corsConfiguration.setAllowedHeaders(properties.getAllowedHeaders());
//        corsConfiguration.setAllowedMethods(properties.getAllowedMethods());
//        corsConfiguration.setMaxAge(properties.getMaxAge());
//
//        final UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
//
//        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
//
//        return corsConfigurationSource;
//    }

    @Bean
    public AuthenticationFilter authenticationFilter(final AuthenticationManager authenticationManager,
                                                     final AuthenticationConverter authenticationConverter,
                                                     final AuthenticationSuccessHandler authenticationSuccessHandler,
                                                     final AuthenticationFailureHandler authenticationFailureHandler) {
        final AuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authenticationManager, authenticationConverter);

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
        return new CustomAuthenticationConverter();
    }

    @Bean(name = "authenticationSuccessHandler")
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(final AuthenticationEntryPoint authenticationEntryPoint) {
        return new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService) {
        return new CustomAuthenticationProvider(authenticationUserDetailsService);
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService(
            final @Qualifier("accessTokenProvider") JwtProvider accessTokenProvider
    ) {
        return new MemberDetailsService(accessTokenProvider);
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter = new DelegatingProviderUserConverter();

        return new OAuth2MemberService(providerUserConverter);
    }

    @Bean(name = "oAuth2AuthenticationSuccessHandler")
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
                                                                           final JwtProvider accessTokenProvider,
                                                                           final JwtProvider refreshTokenProvider) {
        return new OAuth2AuthenticationSuccessHandler(memberRepository, accessTokenProvider, refreshTokenProvider);
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