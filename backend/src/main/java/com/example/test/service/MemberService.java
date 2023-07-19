package com.example.test.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.test.domain.Member;
import com.example.test.domain.Role;
import com.example.test.dto.MemberLoginRequestDto;
import com.example.test.dto.MemberLoginResponseDto;
import com.example.test.dto.MemberRegisterRequestDto;
import com.example.test.dto.MemberRegisterResponseDto;
import com.example.test.exception.MemberException;
import com.example.test.exception.status.MemberStatus;
import com.example.test.repository.MemberRepository;
import com.example.test.utility.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    public boolean checkEmailExistence(final String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public MemberRegisterResponseDto register(final MemberRegisterRequestDto requestDto) {
        if (checkEmailExistence(requestDto.getEmail())) {
            throw new MemberException(MemberStatus.EXISTING_EMAIL);
        }

        final Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role((requestDto.isAdmin()) ? (Role.ADMIN) : (Role.USER))
                .build();

        final Member result = memberRepository.save(member);

        return MemberRegisterResponseDto.builder()
                .nickname(result.getNickname())
                .build();
    }

    public MemberLoginResponseDto login(final MemberLoginRequestDto requestDto) throws JWTCreationException {
        final Optional<Member> result = memberRepository.findByEmail(requestDto.getEmail());

        if (result.isEmpty()) {
            throw new MemberException(MemberStatus.NOT_EXISTING_EMAIL);
        }

        final Member member = result.get();

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new MemberException(MemberStatus.INCORRECT_PASSWORD);
        }

        final Map<String, String> payload = new HashMap<>();

        payload.put("userId", member.getId().toString());
        payload.put("email", member.getEmail());
        payload.put("nickname", member.getNickname());
        payload.put("role", member.getRole().getDisplayName());

        final Map<String, String> refreshPayload = new HashMap<>();

        payload.put("email", member.getEmail());

        return MemberLoginResponseDto.builder()
                .accessToken(accessTokenProvider.generate(payload))
                .refreshToken(refreshTokenProvider.generate(refreshPayload))
                .build();
    }

}