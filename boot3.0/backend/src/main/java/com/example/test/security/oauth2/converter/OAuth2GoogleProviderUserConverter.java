package com.example.test.security.oauth2.converter;

import com.example.test.domain.enums.LoginType;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import com.example.test.security.oauth2.oauth2user.social.GoogleUser;
import com.example.test.utility.OAuth2Utils;

public final class OAuth2GoogleProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(LoginType.GOOGLE.getSocialName())) {
            return null;
        }

        return new GoogleUser(
                OAuth2Utils.getMainAttributes(providerUserRequest.getOAuth2User()),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}