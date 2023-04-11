package com.example.test.security.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.test.security.authentication.MemberAuthenticationConverter;
import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.AccessTokenProperties.AccessTokenClaim;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties.RefreshTokenClaim;
import com.example.test.security.utility.CookieUtility;
import com.example.test.security.utility.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    public static final String REFRESH_PREFIX = "Refresh ";

    private final JwtProvider accessTokenProvider;

    private final JwtProvider refreshTokenProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        String bearerToken = request.getHeader(MemberAuthenticationConverter.AUTHORIZATION_HEADER);

        String accessToken = null;

        // 인증 토큰의 인증 타입이 Bearer인지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(MemberAuthenticationConverter.BEARER_PREFIX)) {
            // 맞다면 Bearer를 제외한 토큰을 추출하여 반환
            accessToken = bearerToken.substring(7);
        }

        try {
            if (checkAccessTokenExpiration(accessToken)) {
                String refreshTempToken = request.getHeader(MemberAuthenticationConverter.AUTHORIZATION_HEADER);

                String refreshToken = null;

                if (StringUtils.hasText(refreshTempToken) && bearerToken.startsWith(REFRESH_PREFIX)) {
                    refreshToken = refreshTempToken.substring(7);
                }

                final DecodedJWT decodedRefreshToken = verifyRefreshToken(refreshToken);

                String userId = decodedRefreshToken.getClaim(RefreshTokenClaim.USER_ID.getClaim()).toString();

                userId = userId.substring(1, userId.length() - 1);

                CookieUtility.addCookie(
                        response,
                        AccessTokenProperties.COOKIE_NAME,
                        accessTokenProvider.generate(Map.of(AccessTokenClaim.USER_ID.getClaim(), userId))
                );

                CookieUtility.addCookie(
                        response,
                        RefreshTokenProperties.COOKIE_NAME,
                        refreshTokenProvider.generate(Map.of(RefreshTokenClaim.USER_ID.getClaim(), userId)),
                        refreshTokenProvider.getValidSeconds()
                );

                response.sendRedirect(request.getRequestURI());
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
                response.sendRedirect("/");
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
            accessTokenProvider.verify(accessToken);

            return false;
        } catch (final TokenExpiredException tokenExpiredException) {
            return true;
        } catch (final JWTVerificationException jwtVerificationException) {
            throw new BadCredentialsException(jwtVerificationException.getMessage());
        }
    }

    private DecodedJWT verifyRefreshToken(final String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException();
        }

        try {
            return refreshTokenProvider.verify(refreshToken);
        } catch (final JWTVerificationException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}