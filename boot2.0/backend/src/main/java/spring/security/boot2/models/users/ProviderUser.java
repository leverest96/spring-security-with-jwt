package spring.security.boot2.models.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.common.enums.GenderType;
import spring.security.boot2.common.enums.LoginType;
import spring.security.boot2.common.enums.MemberRole;

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
