package com.example.test.exception.handler;

import com.example.test.domain.Member;
import com.example.test.exception.ExceptionResponse;
import com.example.test.exception.MemberException;
import com.example.test.exception.status.MemberStatus;
import com.example.test.properties.jwt.AccessTokenProperties;
import com.example.test.properties.jwt.RefreshTokenProperties;
import com.example.test.repository.MemberRepository;
import com.example.test.utility.CookieUtility;
import com.example.test.utility.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    private final MemberRepository memberRepository;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
        String accessToken = (accessTokenCookie == null) ? (null) : (accessTokenCookie.getValue());

        try {
            if (!checkAccessTokenExpiration(accessToken)) {
                final long memberId = accessTokenProvider.getLongClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());
                final String loginId = accessTokenProvider.getStringClaimFromExpirationToken(accessToken, AccessTokenProperties.AccessTokenClaim.LOGIN_ID.getClaim());

                final Member member = memberRepository.findById(memberId).orElseThrow(
                        () -> new MemberException(MemberStatus.NOT_EXISTING_MEMBER)
                );

                final String exRefreshToken = member.getRefreshToken();

                if (verifyRefreshToken(exRefreshToken)) {
                    final long exRefreshTokenMemberId = refreshTokenProvider.getLongClaimFromExpirationToken(exRefreshToken,
                            AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());

                    if (exRefreshTokenMemberId == memberId) {
                        final String newRefreshToken = refreshTokenProvider.createRefreshToken(exRefreshTokenMemberId);

                        member.updateRefreshToken(newRefreshToken);
                    } else {
                        member.updateRefreshToken(null);

                        throw new Exception();
                    }
                }

                accessToken = accessTokenProvider.createAccessToken(memberId, loginId);

                CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, accessToken);

                response.sendRedirect(request.getRequestURI());
            }
        } catch (final Exception ex) {
            final String[] uriTokens = request.getRequestURI().substring(1).split("/");

            log.warn("Authentication exception occurrence: {}", authException.getMessage());

            CookieUtility.deleteCookie(response, AccessTokenProperties.COOKIE_NAME);

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