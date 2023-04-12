package com.example.test.security.authentication;

import com.example.test.properties.jwt.AccessTokenProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.util.WebUtils;

@RequiredArgsConstructor
// HttpServletRequest를 Authentication로 변환하기 위한 클래스
public class MemberAuthenticationConverter implements AuthenticationConverter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    // HttpServletRequest 객체에서 인증에 필요한 세부 정보를 추출하고 Authentication 객체의 details 속성으로 설정시키는 역할
    // IP 주소, 브라우저 종류 및 버전, 요청된 URI 등
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    public MemberAuthenticationConverter() {
        this(new WebAuthenticationDetailsSource());
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
        final String accessToken = (accessTokenCookie == null) ? (null) : (accessTokenCookie.getValue());

//        String accessToken = request.getHeader(AccessTokenProperties.COOKIE_NAME);

//        String accessToken = null;
//
//        // 인증 토큰의 인증 타입이 Bearer인지 확인
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            // 맞다면 Bearer를 제외한 토큰을 추출하여 반환
//            accessToken = bearerToken.substring(7);
//        }

        // Authentication의 구현 클래스 중 하나
        // 인증을 수행하기 위해 사용자의 자격 증명을 요구하지 않음
        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(null, accessToken);

        result.setDetails(authenticationDetailsSource.buildDetails(request));

        return result;
    }
}
