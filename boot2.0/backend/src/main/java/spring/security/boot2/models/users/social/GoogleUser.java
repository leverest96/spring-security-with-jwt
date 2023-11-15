package spring.security.boot2.models.users.social;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.models.Attributes;

// google 로그인에 대한 처리
public class GoogleUser extends OAuth2ProviderUser {
    public GoogleUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(attributes.getMainAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getLoginId() {
        return (String)getAttributes().get("sub");
    }

    @Override
    public String getNickname() {
        return (String)getAttributes().get("name");
    }

    @Override
    public String getProfile() {
        return (String)getAttributes().get("picture");
    }
}
