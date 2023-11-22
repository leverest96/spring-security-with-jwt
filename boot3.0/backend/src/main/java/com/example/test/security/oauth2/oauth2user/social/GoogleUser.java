package com.example.test.security.oauth2.oauth2user.social;

import com.example.test.security.oauth2.oauth2user.Attributes;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUser extends OAuth2ProviderUser {
    public GoogleUser(final Attributes attributes,
                      final OAuth2User oAuth2User,
                      final ClientRegistration clientRegistration) {
        super(attributes.mainAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getLoginId() {
        return (String)getAttributes().get("sub");
    }

    @Override
    public String getNickname() {
        return (String)getAttributes().get("name");
    }

    @Override
    public String getProfile() {
        return (String)getAttributes().get("picture");
    }
}
