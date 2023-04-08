package com.example.test.security.tokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// AuthenticationProvider에서 사용하기 위해 AbstractAuthenticationToken 클래스를 상속받음
// 인증을 위해 AuthenticationManager 클래스에게 전달될 클래스
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private String jsonWebToken;
    private Object principal;
    private Object credentials;

    public JwtAuthenticationToken(String jsonWebToken) {
        super(null);
        this.jsonWebToken = jsonWebToken;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal,
                                  Object credentials,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getJsonWebToken() {
        return this.jsonWebToken;
    }
}
