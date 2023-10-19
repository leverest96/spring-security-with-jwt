package spring.security.boot2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean checkExistence(final String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    public void register(ProviderUser providerUser) {
        Member member = Member.builder()
                .loginId(providerUser.getLoginId())
                .nickname(providerUser.getNickname())
                .profile(providerUser.getProfile())
                .email(providerUser.getEmail())
                .loginType(providerUser.getLoginType())
                .role(providerUser.getRole())
                .genderType(providerUser.getGenderType())
                .build();

        memberRepository.save(member);
    }
}
