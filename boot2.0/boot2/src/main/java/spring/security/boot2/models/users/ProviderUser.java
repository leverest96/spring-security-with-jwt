package spring.security.boot2.models.users;

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
    String getLoginType();
    String getRole();
    String getGenderType();
    LocalDateTime getDeleteAt();
    List<? extends GrantedAuthority> getAuthorities();
    Map<String, Object> getAttributes();
    OAuth2User getOAuth2User();
}
