package spring.security.boot2.security.oauth2.converter;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

// oauth2 로그인의 종류 확인 및 기본 OAuth2User 초기화
@Getter
public class ProviderUserRequest {
    private final ClientRegistration clientRegistration;
    private final OAuth2User oAuth2User;

    @Builder
    public ProviderUserRequest(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        this.clientRegistration = clientRegistration;
        this.oAuth2User = oAuth2User;
    }
}