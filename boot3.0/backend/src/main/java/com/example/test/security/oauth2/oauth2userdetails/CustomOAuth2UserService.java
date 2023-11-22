package com.example.test.security.oauth2.oauth2userdetails;

import com.example.test.security.oauth2.converter.ProviderUserConverter;
import com.example.test.security.oauth2.converter.ProviderUserRequest;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final ClientRegistration clientRegistration = userRequest.getClientRegistration();
        final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        final OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        final ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);
        final ProviderUser providerUser = providerUser(providerUserRequest);

        return new CustomOAuth2User(providerUser);
    }

    public ProviderUser providerUser(final ProviderUserRequest providerUserRequest){
        return providerUserConverter.convert(providerUserRequest);
    }
}
