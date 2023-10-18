package spring.security.boot2.userdetail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.security.boot2.domain.oauth.GoogleUser;
import spring.security.boot2.domain.oauth.KeycloakUser;
import spring.security.boot2.domain.oauth.NaverUser;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.security.converter.ProviderUserConverter;
import spring.security.boot2.security.converter.ProviderUserRequest;
import spring.security.boot2.service.MemberService;

@Service
@Getter
@RequiredArgsConstructor
public abstract class AbstractOAuth2UserService {
    private final MemberService memberService;

//    private SelfCertification certification;

    private final ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

//    public void selfCertificate(ProviderUser providerUser){
//        certification.checkCertification(providerUser);
//    }

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest){
        if (memberService.checkExistence(providerUser.getUsername())) {
            ClientRegistration clientRegistration = userRequest.getClientRegistration();

            memberService.register(clientRegistration.getRegistrationId(), providerUser);
        }
    }

    public ProviderUser providerUser(
//            ProviderUserRequest providerUserRequest,
                                     ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if (registrationId.equals("keycloak")) {
            return new KeycloakUser(oAuth2User, clientRegistration);
        } else if (registrationId.equals("google")) {
            return new GoogleUser(oAuth2User, clientRegistration);
        } else if (registrationId.equals("naver")) {
            return new NaverUser(oAuth2User, clientRegistration);
//        } else if (registrationId.equals("kakao")) {
//            return new KakaoUser(oAuth2User, clientRegistration);
        }

//        return providerUserConverter.convert(providerUserRequest);
        return null;
    }
}
