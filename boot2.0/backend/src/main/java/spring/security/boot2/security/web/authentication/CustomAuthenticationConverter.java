package spring.security.boot2.security.web.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.util.WebUtils;
import spring.security.boot2.properties.AccessTokenProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

// oauth2의 DelegatingProviderUserConverter의 내부 고려사항들 중 하나와 비슷
// 일반 로그인에서의 converter
// token을 authentication 객체로 반환 (여기서 PreAuthenticatedAuthenticationToken도 Authentication을 구현한 객체)
@RequiredArgsConstructor
public class CustomAuthenticationConverter implements AuthenticationConverter {
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    public CustomAuthenticationConverter() {
        this(new WebAuthenticationDetailsSource());
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
        final String accessToken = (accessTokenCookie == null) ? (null) : (accessTokenCookie.getValue());

        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(null, accessToken);

        result.setDetails(authenticationDetailsSource.buildDetails(request));

        return result;
    }
}
