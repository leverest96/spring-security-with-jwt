package spring.security.boot2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.security.boot2.common.enums.GenderType;
import spring.security.boot2.common.enums.MemberRole;
import spring.security.boot2.dto.MemberLoginDto;
import spring.security.boot2.dto.MemberRegisterDto;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.repository.MemberRepository;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean checkExistence(final String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    public void register(final MemberRegisterDto requestDto) {
        Member member = Member.builder()
                .loginId(UUID.randomUUID().toString())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .profile("https://play-lh.googleusercontent.com/38AGKCqmbjZ9OuWx4YjssAz3Y0DTWbiM5HB0ove1pNBq_o9mtWfGszjZNxZdwt_vgHo")
                .role(MemberRole.MEMBER)
                .genderType(GenderType.BLANK)
                .build();

        memberRepository.save(member);
    }

    public void socialRegister(ProviderUser providerUser) {
        Member member = memberRepository.findByEmail(providerUser.getEmail()).orElseGet(
                () -> memberRepository.save(Member.builder()
                        .loginId(providerUser.getLoginId())
                        .nickname(providerUser.getNickname())
                        .email(providerUser.getEmail())
                        .profile(providerUser.getProfile())
                        .loginType(providerUser.getLoginType())
                        .role(providerUser.getRole())
                        .genderType(providerUser.getGenderType())
                        .build())
        );

        memberRepository.save(member);
    }

    public Member login(MemberLoginDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("이메일 없음")
        );

        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        return member;
    }
}
