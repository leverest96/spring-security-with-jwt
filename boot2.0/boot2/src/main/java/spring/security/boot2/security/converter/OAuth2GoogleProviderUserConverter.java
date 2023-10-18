package spring.security.boot2.security.converter;

import spring.security.boot2.domain.oauth.GoogleUser;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.enums.SocialType;
import spring.security.boot2.util.OAuth2Utils;

public final class OAuth2GoogleProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {

        if (!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.GOOGLE.getSocialName())) {
            return null;
        }

        return new GoogleUser(
                OAuth2Utils.getMainAttributes(providerUserRequest.getOAuth2User()),
                providerUserRequest.getOAuth2User(),
                providerUserRequest.getClientRegistration()
        );
    }
}