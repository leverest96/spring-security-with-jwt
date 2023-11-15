package spring.security.boot2.security.oauth2.converter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import spring.security.boot2.models.users.social.KakaoUser;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.common.enums.LoginType;
import spring.security.boot2.common.util.OAuth2Utils;

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