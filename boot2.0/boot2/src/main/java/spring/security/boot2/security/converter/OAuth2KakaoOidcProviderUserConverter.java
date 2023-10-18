package spring.security.boot2.security.converter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import spring.security.boot2.domain.oauth.KakaoOidcUser;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.enums.SocialType;
import spring.security.boot2.util.OAuth2Utils;

public final class OAuth2KakaoOidcProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {
        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.KAKAO.getSocialName())) {
            return null;
        }

        if (!(providerUserRequest.getOAuth2User() instanceof OidcUser)) {
            return null;
        }

        return new KakaoOidcUser(
                OAuth2Utils.getMainAttributes(providerUserRequest.getOAuth2User()),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}