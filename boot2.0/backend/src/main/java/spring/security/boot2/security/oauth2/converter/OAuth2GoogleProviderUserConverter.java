package spring.security.boot2.security.oauth2.converter;

import spring.security.boot2.models.users.social.GoogleUser;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.common.enums.LoginType;
import spring.security.boot2.common.util.OAuth2Utils;

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