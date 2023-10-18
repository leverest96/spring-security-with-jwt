package spring.security.boot2.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.security.boot2.domain.oauth.ProviderUser;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements ProviderUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String registrationId;

    private String id;
    private String username;
    private String password;
    private String email;
    private String picture;
    private boolean isCertificated;
    private String provider;
    private List<? extends GrantedAuthority> authorities;

    @Builder
    public Member(String registrationId,
                  String id,
                  String username,
                  String password,
                  String email,
                  String picture,
                  String provider,
                  List<? extends GrantedAuthority> authorities) {
        this.registrationId = registrationId;
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.authorities = authorities;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getPicture() {
        return null;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public OAuth2User getOAuth2User() {
        return null;
    }
}
