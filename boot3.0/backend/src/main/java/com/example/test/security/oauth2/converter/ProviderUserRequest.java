package com.example.test.security.oauth2.converter;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class ProviderUserRequest {
    private final ClientRegistration clientRegistration;
    private final OAuth2User oAuth2User;

    @Builder
    public ProviderUserRequest(final ClientRegistration clientRegistration,
                               final OAuth2User oAuth2User) {
        this.clientRegistration = clientRegistration;
        this.oAuth2User = oAuth2User;
    }
}