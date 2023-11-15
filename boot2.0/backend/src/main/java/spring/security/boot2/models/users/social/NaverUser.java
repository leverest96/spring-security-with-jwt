package spring.security.boot2.models.users.social;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.models.Attributes;

// naver 로그인에 대한 처리
// naver 로그인은 개인적으로 하지 못하므로 일단 사용 X
public class NaverUser extends OAuth2ProviderUser {
    public NaverUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration){
        super(attributes.getSubAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getLoginId() {
        return (String)getAttributes().get("id");
    }

    @Override
    public String getNickname() {
        return (String)getAttributes().get("name");
    }

    @Override
    public String getProfile() {
        return (String)getAttributes().get("profile_image");
    }
}
