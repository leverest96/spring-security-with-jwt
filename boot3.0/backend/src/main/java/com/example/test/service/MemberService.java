package com.example.test.service;

import com.example.test.domain.Member;
import com.example.test.domain.enums.GenderType;
import com.example.test.domain.enums.LoginType;
import com.example.test.domain.enums.MemberRole;
import com.example.test.domain.redis.AccessToken;
import com.example.test.dto.MemberInfoResponseDto;
import com.example.test.dto.MemberLoginRequestDto;
import com.example.test.dto.MemberLoginResponseDto;
import com.example.test.dto.MemberRegisterRequestDto;
import com.example.test.exception.MemberException;
import com.example.test.exception.status.MemberStatus;
import com.example.test.repository.MemberRepository;
import com.example.test.repository.RedisRepository;
import com.example.test.utility.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    public boolean checkEmailExistence(final String email) {
        return memberRepository.findByEmailAndLoginType(email, LoginType.NONE).isPresent();
    }

    @Transactional
    public void register(final MemberRegisterRequestDto requestDto) {
        if (checkEmailExistence(requestDto.getEmail())) {
            throw new MemberException(MemberStatus.EXISTING_EMAIL);
        }

        final Member member = Member.builder()
                .loginId(UUID.randomUUID().toString())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .profile("https://play-lh.googleusercontent.com/38AGKCqmbjZ9OuWx4YjssAz3Y0DTWbiM5HB0ove1pNBq_o9mtWfGszjZNxZdwt_vgHo")
                .roleType(requestDto.isAdmin() ? MemberRole.ADMIN : MemberRole.MEMBER)
                .loginType(LoginType.NONE)
                .genderType(GenderType.BLANK)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public MemberLoginResponseDto login(final MemberLoginRequestDto requestDto) {
        final Optional<Member> result = memberRepository.findByEmailAndLoginType(requestDto.getEmail(), LoginType.NONE);

        if (result.isEmpty()) {
            throw new MemberException(MemberStatus.NOT_EXISTING_EMAIL);
        }

        final Member member = result.get();

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new MemberException(MemberStatus.INCORRECT_PASSWORD);
        }

        if (!member.getLoginType().equals(LoginType.NONE)) {
            throw new MemberException(MemberStatus.INCORRECT_LOGIN_TYPE);
        }

        final Long memberId = member.getId();
        final String loginId = member.getLoginId();

        final String accessToken = accessTokenProvider.createAccessToken(memberId, loginId);
        final String refreshToken = refreshTokenProvider.createRefreshToken(memberId);
        final int refreshTokenValidSeconds = refreshTokenProvider.getValidSeconds();

        redisRepository.save(new AccessToken(AccessToken.ACCESS_TOKEN_KEY, accessToken));
        member.updateRefreshToken(refreshToken);

        return MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenValidSeconds(refreshTokenValidSeconds)
                .build();
    }

    public MemberInfoResponseDto member(final String loginId) {
        final Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new MemberException(MemberStatus.NOT_EXISTING_MEMBER)
        );

        return MemberInfoResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    @Transactional
    public void removeAccessToken() {
        final Optional<AccessToken> accessToken = redisRepository.findById(AccessToken.ACCESS_TOKEN_KEY);

        if (accessToken.isPresent()) {
            redisRepository.deleteById(AccessToken.ACCESS_TOKEN_KEY);
        } else {
            throw new IllegalArgumentException("로그인하지 않은 유저입니다.");
        }
    }
}