package com.example.test.security.oauth2.oauth2user;

import com.example.test.domain.enums.GenderType;
import com.example.test.domain.enums.LoginType;
import com.example.test.domain.enums.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProviderUser {
    String getLoginId();
    String getNickname();
    String getProfile();
    String getEmail();
    LoginType getLoginType();
    MemberRole getRole();
    GenderType getGenderType();
    LocalDateTime getDeletedAt();
    List<? extends GrantedAuthority> getAuthorities();
    Map<String, Object> getAttributes();
    OAuth2User getOAuth2User();
}
