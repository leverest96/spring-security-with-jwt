package spring.security.boot2.security;

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

@RequiredArgsConstructor
public class CustomAuthenticationConverter implements AuthenticationConverter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

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
