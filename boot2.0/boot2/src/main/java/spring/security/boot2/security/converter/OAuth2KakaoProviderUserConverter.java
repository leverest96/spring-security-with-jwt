package spring.security.boot2.security.converter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import spring.security.boot2.domain.oauth.KakaoUser;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.enums.SocialType;
import spring.security.boot2.util.OAuth2Utils;

public final class OAuth2KakaoProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.KAKAO.getSocialName())) {
            return null;
        }

        if (providerUserRequest.getOAuth2User() instanceof OidcUser) {
            return null;
        }

        return new KakaoUser(
                OAuth2Utils.getOtherAttributes(providerUserRequest.getOAuth2User(), "kakao_account", "profile"),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}