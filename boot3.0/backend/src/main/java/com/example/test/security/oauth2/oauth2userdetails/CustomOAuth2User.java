package com.example.test.security.oauth2.oauth2userdetails;

import com.example.test.security.oauth2.oauth2user.ProviderUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record CustomOAuth2User(ProviderUser providerUser) implements OAuth2User {
    @Override
    public Map<String, Object> getAttributes() {
        return providerUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return providerUser.getAuthorities();
    }

    @Override
    public String getName() {
        return providerUser.getNickname();
    }
}
