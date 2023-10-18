package spring.security.boot2.security.converter;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.domain.Member;
import spring.security.boot2.domain.MemberDto;

@Getter
public class ProviderUserRequest {
    private ClientRegistration clientRegistration;
    private OAuth2User oAuth2User;
    private MemberDto memberDto;

    @Builder
    public ProviderUserRequest(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        this.clientRegistration = clientRegistration;
        this.oAuth2User = oAuth2User;
    }

    @Builder
    public ProviderUserRequest(MemberDto memberDto) {
        this.memberDto = memberDto;
    }
}