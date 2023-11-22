package com.example.test.security.oauth2.converter;

import com.example.test.domain.enums.LoginType;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import com.example.test.security.oauth2.oauth2user.social.NaverUser;
import com.example.test.utility.OAuth2Utils;

public final class OAuth2NaverProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(LoginType.NAVER.getSocialName())) {
            return null;
        }

        return new NaverUser(
                OAuth2Utils.getSubAttributes(providerUserRequest.getOAuth2User(), "response"),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}