package spring.security.boot2.security.oauth2.oauth2user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.models.users.ProviderUser;

import java.util.Collection;
import java.util.Map;

// oauth2에서 사용되는 클래스로 OAuth2User를 상속받는 클래스
// oauth2 로그인 때만 사용
@Getter
public class CustomOAuth2User implements OAuth2User {
    private final ProviderUser providerUser;

    public CustomOAuth2User(ProviderUser providerUser) {
        this.providerUser = providerUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return providerUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return providerUser.getAuthorities();
    }

    @Override
    public String getName() {
        return providerUser.getNickname();
    }
}
