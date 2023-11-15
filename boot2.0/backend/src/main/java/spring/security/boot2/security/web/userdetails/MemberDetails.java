package spring.security.boot2.security.web.userdetails;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

// 일반 로그인에서 사용되는 클래스로 UserDetails를 상속받는 클래스
// 이후 access token에서 정보를 불러와 사용할 때에도 MemberDetails에서 불러옴
@Getter
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {
    private final long memberId;
    private final String loginId;

    private final List<? extends GrantedAuthority> authorities;

    @Builder
    public MemberDetails(final long memberId, final String loginId, final String... authorities) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.authorities = (authorities == null) ? (null) : (AuthorityUtils.createAuthorityList(authorities));
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}