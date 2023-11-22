package com.example.test.exception.handler;

import com.example.test.exception.ExceptionResponse;
import com.example.test.properties.jwt.AccessTokenProperties;
import com.example.test.properties.jwt.RefreshTokenProperties;
import com.example.test.utility.CookieUtility;
import com.example.test.utility.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
            if (!checkAccessTokenExpiration(accessToken)) {
                final Cookie refreshTokenCookie = WebUtils.getCookie(request, RefreshTokenProperties.COOKIE_NAME);
                final String refreshToken = (refreshTokenCookie == null) ? (null) : (refreshTokenCookie.getValue());

                if (verifyRefreshToken(refreshToken)) {
                    final long memberId = accessTokenProvider.getLongClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());
                    final String loginId = accessTokenProvider.getStringClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.LOGIN_ID.getClaim());

                    CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, accessTokenProvider.createAccessToken(memberId, loginId));
                    CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, refreshTokenProvider.createRefreshToken(memberId), refreshTokenProvider.getValidSeconds());

                    response.sendRedirect(request.getRequestURI());
                }
            }
        } catch (final Exception ex) {
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