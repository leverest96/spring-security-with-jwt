package spring.security.boot2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.boot2.common.enums.GenderType;
import spring.security.boot2.common.enums.MemberRole;
import spring.security.boot2.common.util.JwtProvider;
import spring.security.boot2.dto.MemberLoginDto;
import spring.security.boot2.dto.MemberLoginResponseDto;
import spring.security.boot2.dto.MemberRegisterDto;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.repository.MemberRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    public boolean checkExistence(final String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }

    public void register(final MemberRegisterDto requestDto) {
        Member member = Member.builder()
                .loginId(UUID.randomUUID().toString())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
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

    public MemberLoginResponseDto login(MemberLoginDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("이메일 없음")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        final Long id = member.getId();

        final String accessToken = accessTokenProvider.createAccessToken(id);

        final String refreshToken = refreshTokenProvider.createRefreshToken();

        return MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Member member(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("멤버 없음")
        );
    }
}
