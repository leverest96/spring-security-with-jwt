package spring.security.boot2.models.users.social;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.models.Attributes;

public class KakaoOidcUser extends OAuth2ProviderUser {
    public KakaoOidcUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getMainAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getLoginId() {
        return (String)getAttributes().get("id");
    }

    @Override
    public String getNickname() {
        return (String)getAttributes().get("nickname");
    }

    @Override
    public String getProfile() {
        return (String)getAttributes().get("profile_image_url");
    }
}