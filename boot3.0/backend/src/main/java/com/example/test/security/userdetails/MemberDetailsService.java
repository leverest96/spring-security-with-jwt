package com.example.test.security.userdetails;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.test.properties.jwt.AccessTokenProperties.AccessTokenClaim;
import com.example.test.utility.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
@Slf4j
// 사용자의 데이터를 로드하기 위한 클래스
public class MemberDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final JwtProvider accessTokenProvider;

    @Override
    // PreAuthenticatedAuthenticationToken을 기반으로 UserDetails 생성
    public UserDetails loadUserDetails(final PreAuthenticatedAuthenticationToken token) throws AuthenticationException {
        try {
            // 토큰의 credentials를 읽는다.
            final String accessToken = (String) token.getCredentials();
            // 읽어들인 토큰을 토대로 verify 진행
            final DecodedJWT decodedAccessToken = accessTokenProvider.verify(accessToken);

            // 해독된 jwt 토큰에서 userId를 읽어온다.
            final String userId = decodedAccessToken.getClaim(AccessTokenClaim.USER_ID.getClaim()).asString();
            final String email = decodedAccessToken.getClaim(AccessTokenClaim.EMAIL.getClaim()).asString();
            final String nickname = decodedAccessToken.getClaim(AccessTokenClaim.NICKNAME.getClaim()).asString();
            final String[] role = {decodedAccessToken.getClaim(AccessTokenClaim.ROLE.getClaim()).asString()};

            log.info("Member authentication request: {}", userId);

            // userId를 통해 memberdetails를 만들어낸다.
            return MemberDetails.builder()
                    .userId(userId)
                    .email(email)
                    .nickname(nickname)
                    .authorities(role)
                    .build();
        } catch (final JWTVerificationException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}
