package com.example.test.security.manager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class MemberAuthenticationConverter implements AuthenticationConverter {
    // Authorization key
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 인증 타입
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    public MemberAuthenticationConverter() {
        this(new WebAuthenticationDetailsSource());
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        System.out.println("2번");
        // header에 Authorization key가 있는지 확인
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        String accessToken = null;

        // 인증 토큰의 인증 타입이 Bearer인지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // 맞다면 Bearer를 제외한 토큰을 추출하여 반환
            accessToken = bearerToken.substring(7);
        }

//        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
//
//        if (accessTokenCookie == null) {
//            return null;
//        }
//
//        final String accessToken = accessTokenCookie.getValue();

        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(null, accessToken);

        result.setDetails(authenticationDetailsSource.buildDetails(request));

        return result;
    }
}
