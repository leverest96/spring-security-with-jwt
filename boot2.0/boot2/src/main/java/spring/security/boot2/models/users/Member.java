package spring.security.boot2.models.users;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements ProviderUser {
    public static final int NICKNAME_MAX_LENGTH = 20;
    public static final String ROLE_USER = "USER";
    public static final String GENDER_MALE = "MALE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String loginType;

    @Column(nullable = false)
    private String genderType;

    private LocalDateTime deleteAt;

    @Builder
    public Member(String loginId,
                  String nickname,
                  String email,
                  String profile,
                  String role,
                  String loginType,
                  String genderType) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.role = role;
        this.loginType = loginType;
        this.genderType = genderType;
    }

    @Override
    public String getLoginId() {
        return loginId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public String getLoginType() {
        return loginType;
    }

    @Override
    public String getGenderType() {
        return genderType;
    }

    @Override
    public LocalDateTime getDeleteAt() {
        return deleteAt;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return null;
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
