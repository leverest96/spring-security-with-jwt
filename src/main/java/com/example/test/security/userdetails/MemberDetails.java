package com.example.test.security.userdetails;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
// 사용자 정보를 제공하기 위한 용도의 클래스로, 보안 목적으로 스프링 시큐리티에서 직접 사용되진 않고 Authentication 객체에 캡슐화되어 보관
public class MemberDetails implements UserDetails {
    private final String userId;

    private final String nickname;

    private final Collection<? extends GrantedAuthority> authorities;

    @Builder
    public MemberDetails(String userId, String nickname, String... authorities) {
        this.userId = userId;
        this.nickname = nickname;
        this.authorities = (authorities == null) ? (null) : (AuthorityUtils.createAuthorityList(authorities));
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    // 사용자에게 부여된 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    // 인증에 사용된 비밀번호
    public String getPassword() {
        return null;
    }

    @Override
    // 인증에 사용된 사용자 이름
    public String getUsername() {
        return null;
    }

    @Override
    // 계정 만료 여부
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // 계정 잠금 여부
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // 자격 증명 만료 여부
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 활성화 여부
    public boolean isEnabled() {
        return true;
    }
}