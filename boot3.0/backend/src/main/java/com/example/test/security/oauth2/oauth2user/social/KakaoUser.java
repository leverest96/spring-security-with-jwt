package com.example.test.security.oauth2.oauth2user.social;

import com.example.test.security.oauth2.oauth2user.Attributes;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class KakaoUser extends OAuth2ProviderUser {
    private final Map<String, Object> subAttributes;

    public KakaoUser(final Attributes attributes,
                     final OAuth2User oAuth2User,
                     final ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
        this.subAttributes = attributes.otherAttributes();
    }

    @Override
    public String getLoginId() {
        return String.valueOf(getAttributes().get("id"));
    }

    @Override
    public String getNickname() {
        return (String) subAttributes.get("nickname");
    }

    @Override
    public String getProfile() {
        return (String) subAttributes.get("profile_image_url");
    }
}
