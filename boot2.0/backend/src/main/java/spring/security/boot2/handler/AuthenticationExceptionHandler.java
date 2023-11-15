package spring.security.boot2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.WebUtils;
import spring.security.boot2.common.util.CookieUtility;
import spring.security.boot2.common.util.JwtProvider;
import spring.security.boot2.exception.ExceptionResponse;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

// 인증 실패시 처리하는 클래스
// 원래는 이곳에서 access token의 재발급을 하면 안되지만 다른 클래스를 만들지 않고 처리가 가능하여 임시로 이곳에서 진행
// 추후 다른 interceptor 혹은 filter를 이용하여 처리 예정
@RequiredArgsConstructor
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
        final String accessToken = (accessTokenCookie == null) ? (null) : (accessTokenCookie.getValue());

        try {
            // access token 재발급 로직
            if (!checkAccessTokenExpiration(accessToken)) {
                final Cookie refreshTokenCookie = WebUtils.getCookie(request, RefreshTokenProperties.COOKIE_NAME);
                final String refreshToken = (refreshTokenCookie == null) ? (null) : (refreshTokenCookie.getValue());

                // refresh token 재발급 로직
                if (verifyRefreshToken(refreshToken)) {
                    final long memberId = accessTokenProvider.getLongClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());
                    final String loginId = accessTokenProvider.getStringClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.LOGIN_ID.getClaim());

                    CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, accessTokenProvider.createAccessToken(memberId, loginId));
                    CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, refreshTokenProvider.createRefreshToken(memberId), refreshTokenProvider.getValidSeconds());

                    response.sendRedirect(request.getRequestURI());
                }
            }
        } catch (final Exception ex) {
            // 인증 처리 실패시 로직
            final String[] uriTokens = request.getRequestURI().substring(1).split("/");

            log.warn("Authentication exception occurrence: {}", authException.getMessage());

            CookieUtility.deleteCookie(response, AccessTokenProperties.COOKIE_NAME);
            CookieUtility.deleteCookie(response, RefreshTokenProperties.COOKIE_NAME);

            if (uriTokens.length > 0 && uriTokens[0].equals("api")) {
                final String responseBody = objectMapper.writeValueAsString(
                        new ExceptionResponse(List.of(authException.getMessage()))
                );

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(responseBody);
            } else if (accessToken == null) {
                response.sendRedirect("http://localhost:5173/");
            } else {
                response.sendRedirect(request.getRequestURI());
            }
        }
    }

    // access token 만료 여부 확인
    private boolean checkAccessTokenExpiration(final String accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException();
        }

        try {
            return accessTokenProvider.validateToken(accessToken);
        } catch (final Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    // refresh token 만료 여부 확인
    private boolean verifyRefreshToken(final String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException();
        }

        try {
            return refreshTokenProvider.validateToken(refreshToken);
        } catch (final Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}