package spring.security.boot2.userdetail;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.security.converter.ProviderUserConverter;
import spring.security.boot2.security.converter.ProviderUserRequest;
import spring.security.boot2.service.MemberService;

@Service
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    public CustomOAuth2UserService(MemberService memberService, ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter) {
        super(memberService, providerUserConverter);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);

        super.register(providerUser, userRequest);

        return oAuth2User;

//        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration,oAuth2User);
//        ProviderUser providerUser = providerUser(providerUserRequest);

        // 본인인증 체크
        // 기본은 본인인증을 하지 않은 상태임
//        selfCertificate(providerUser);

//        return new PrincipalUser(providerUser);
    }
}