package spring.security.boot2.userdetail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.security.boot2.domain.Member;
import spring.security.boot2.domain.oauth.ProviderUser;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.security.converter.ProviderUserConverter;
import spring.security.boot2.security.converter.ProviderUserRequest;

@Service
@Getter
@RequiredArgsConstructor
public abstract class AbstractOAuth2UserService {
    private UserService userService;
    private MemberRepository memberRepository;

    private SelfCertification certification;

    private final ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    public void selfCertificate(ProviderUser providerUser){
        certification.checkCertification(providerUser);
    }

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest){
        Member member = memberRepository.findByUsername(providerUser.getUsername());

        if(user == null){
            ClientRegistration clientRegistration = userRequest.getClientRegistration();
            userService.register(clientRegistration.getRegistrationId(),providerUser);
        }else{
            System.out.println("userRequest = " + userRequest);
        }
    }

    public ProviderUser providerUser(ProviderUserRequest providerUserRequest){
        return providerUserConverter.convert(providerUserRequest);
    }
}
