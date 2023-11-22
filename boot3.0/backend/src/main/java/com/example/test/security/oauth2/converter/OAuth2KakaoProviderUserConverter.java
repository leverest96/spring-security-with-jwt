package com.example.test.security.oauth2.converter;

import com.example.test.domain.enums.LoginType;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import com.example.test.security.oauth2.oauth2user.social.KakaoUser;
import com.example.test.utility.OAuth2Utils;

public final class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(LoginType.KAKAO.getSocialName())) {
            return null;
        }

        return new KakaoUser(
                OAuth2Utils.getOtherAttributes(providerUserRequest.getOAuth2User(), "kakao_account", "profile"),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}