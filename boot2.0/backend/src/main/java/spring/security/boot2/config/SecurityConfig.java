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
import org.springframework.web.cors.CorsUtils;
import spring.security.boot2.common.util.JwtProvider;
import spring.security.boot2.handler.AccessDeniedExceptionHandler;
import spring.security.boot2.handler.AuthenticationExceptionHandler;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import spring.security.boot2.security.oauth2.oauth2user.CustomOAuth2UserService;
import spring.security.boot2.security.web.authentication.CustomAuthenticationConverter;
import spring.security.boot2.security.web.authentication.CustomAuthenticationFilter;
import spring.security.boot2.security.web.authentication.CustomAuthenticationProvider;
import spring.security.boot2.security.web.userdetails.MemberDetailsService;

// security 구성의 가장 중요한 부분
// 대부분의 주입 관계나 security 설정을 담당
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
//                                                  final CorsConfigurationSource corsConfigurationSource,
                                                  final AuthenticationFilter authenticationFilter,
                                                  final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                                                  final AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                                                  final AuthenticationFailureHandler authenticationFailureHandler,
                                                  final AuthenticationEntryPoint authenticationEntryPoint,
                                                  final AccessDeniedHandler accessDeniedHandler) throws Exception {
        // 요청에 대한 권한 설정
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/member/register", "/api/member/login").permitAll()
                .anyRequest().authenticated();

        // JWT가 stateless한 상태를 유지하도록 spring security에서 session을 사용하지 않게 설정
        // stateless : 서버가 클라이언트의 상태를 보존하지 않음
        // 서버의 확장성 때문 (서버가 장애가 나더라도 그 서버와 연결된 클라이언트가 기존 작업을 처음부터 작업하지 않아도 됨)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 기존에 여기서 cors 에러를 해결하려 했으나 실패하여 WebConfig 클래스에서 해결
        //http.cors().configurationSource(corsConfigurationSource);

        // frontend 단에서 header에 X-XSRF-TOKEN을 넘겨줘야함
        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        // csrf : 상단 기입에 따라서 disable()로 임시 해결 / 추후 필요시 다시 할당 예정
        // httpBasic : Http Basic Auth 기반 로그인 이용 X
        // formLogin : 기본 로그인 페이지를 이용 X
        // logout : 기본 로그아웃 요청 사용 X
        // rememberMe : JWT 사용
        // headers : http header 사용 X (필요시 header 직접 custom)
        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .rememberMe().disable()
                .headers().disable();

        // 일반 로그인시 사용될 filter 설정
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // oauth2 로그인을 위한 설정
        // 이후 모든 redirect uri를 /login/oauth2/code/로 설정
        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // 예외 처리를 위한 설정
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }

    // 정적 폴더 및 H2 사용을 위한 spring security 적용 범위에서 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console());
    }

    // 상단 cors 설정시 사용될 cors 설정
    //@Bean
    //public CorsConfigurationSource corsConfigurationSource(final SecurityCorsProperties properties) {
    //    final CorsConfiguration corsConfiguration = new CorsConfiguration();
    //
    //    corsConfiguration.setAllowCredentials(properties.isAllowCredentials());
    //    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
    //    corsConfiguration.setAllowedOrigins(properties.getAllowedOrigins());
    //    corsConfiguration.setAllowedHeaders(properties.getAllowedHeaders());
    //    corsConfiguration.setAllowedMethods(properties.getAllowedMethods());
    //    corsConfiguration.setMaxAge(properties.getMaxAge());
    //
    //    final UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    //
    //    corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    //
    //    return corsConfigurationSource;
    //}

    // 이후 spring security에 필요한 주입 관계 설정
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
        return new CustomOAuth2UserService();
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