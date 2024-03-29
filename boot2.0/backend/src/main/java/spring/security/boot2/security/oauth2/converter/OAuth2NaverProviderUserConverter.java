package spring.security.boot2.security.oauth2.converter;

import spring.security.boot2.common.enums.LoginType;
import spring.security.boot2.common.util.OAuth2Utils;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.models.users.social.NaverUser;

// naver 로그인시 사용되는 converter
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