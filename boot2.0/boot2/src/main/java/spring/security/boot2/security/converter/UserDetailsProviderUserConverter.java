package spring.security.boot2.security.converter;

import spring.security.boot2.domain.Member;
import spring.security.boot2.domain.MemberDto;
import spring.security.boot2.domain.oauth.ProviderUser;

public final class UserDetailsProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    @Override
    public ProviderUser convert(final ProviderUserRequest providerUserRequest) {
        if(providerUserRequest.getMemberDto() == null) {
            return null;
        }

        MemberDto memberDto = providerUserRequest.getMemberDto();

        return Member.builder()
                .id(memberDto.getId())
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .authorities(memberDto.getAuthorities())
                .email(memberDto.getEmail())
                .provider("none")
                .build();
    }
}