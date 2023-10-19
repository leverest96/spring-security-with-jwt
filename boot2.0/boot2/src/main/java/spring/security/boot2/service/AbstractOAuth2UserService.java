package spring.security.boot2.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.security.boot2.security.oauth2.converter.ProviderUserConverter;
import spring.security.boot2.security.oauth2.converter.ProviderUserRequest;
import spring.security.boot2.models.users.ProviderUser;

@Service
@Getter
public abstract class AbstractOAuth2UserService {
    @Autowired
    private MemberService memberService;

    @Autowired
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    public void register(ProviderUser providerUser) {
        if (memberService.checkExistence(providerUser.getNickname())) {
            memberService.register(providerUser);
        }
    }

    public ProviderUser providerUser(ProviderUserRequest providerUserRequest){
        return providerUserConverter.convert(providerUserRequest);
    }
}
