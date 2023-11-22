package com.example.test.security.oauth2.oauth2user.social;

import com.example.test.domain.enums.GenderType;
import com.example.test.domain.enums.LoginType;
import com.example.test.domain.enums.MemberRole;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public abstract class OAuth2ProviderUser implements ProviderUser {
    private final Map<String, Object> attributes;
    private final OAuth2User oAuth2User;
    private final ClientRegistration clientRegistration;

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public LoginType getLoginType() {
        return clientRegistration.getRegistrationId().equals("kakao") ?
                LoginType.KAKAO : clientRegistration.getRegistrationId().equals("google") ?
                LoginType.GOOGLE : LoginType.NAVER;
    }

    @Override
    public MemberRole getRole() {
        return MemberRole.MEMBER;
    }

    @Override
    public GenderType getGenderType() {
        return GenderType.BLANK;
    }

    @Override
    public LocalDateTime getDeletedAt() {
        return null;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority((authority.getAuthority())))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
