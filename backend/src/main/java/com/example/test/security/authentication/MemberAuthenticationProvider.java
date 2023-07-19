package com.example.test.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class MemberAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // supports 메서드로 Authentication 객체를  AuthenticationProvider가 처리할 수 있는지 확인
        // 처리할 수 없다면 Authentication을 null로 return
        if (!supports(authentication.getClass())) return null;

        // AuthenticationProvider가 처리 가능한 Authentication 객체이지만
        // 인증 요청에 필요한 credentials 정보가 전달되지 않은 경우,
        // 즉 사용자가 자격 증명 정보를 제공하지 않은 경우에는 이에 대한 예외 처리
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("No access token credentials found in request");
        }

        // 전달받은 Authentication 객체를 PreAuthenticatedAuthenticationToken으로 형변환
        PreAuthenticatedAuthenticationToken preAuthenticatedToken = (PreAuthenticatedAuthenticationToken) authentication;
        // authenticationUserDetailsService를 이용하여 전달받은 토큰에 대한 UserDetails 객체를 가져온다.
        UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(preAuthenticatedToken);
        // principal : 사용자를 식별하는 정보 (사용자 아이디, 이메일 주소, 전화번호 등)
        // credentials : 인증에 필요한 자격 증명 정보 (패스워드, 토큰 등)
        // authorities : 사용자가 가지고 있는 권한 정보
        // details : 인증 요청과 함께 제공된 추가 정보 (IP 주소, 브라우저 정보, 인증 요청 시간 등)
        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(
                userDetails,
                authentication.getCredentials(),
                userDetails.getAuthorities()
        );

        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
