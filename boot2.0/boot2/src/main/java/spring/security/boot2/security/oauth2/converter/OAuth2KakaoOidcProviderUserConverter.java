package spring.security.boot2.security.oauth2.converter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import spring.security.boot2.models.users.social.KakaoOidcUser;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.common.enums.SocialType;
import spring.security.boot2.common.util.OAuth2Utils;

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