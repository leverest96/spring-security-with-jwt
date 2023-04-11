package com.example.test.security.etc;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.test.security.utility.JwtProvider;
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

    private final JwtProvider jwtProvider;

    public boolean checkEmailExistence(final String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public MemberRegisterResponseDto register(final MemberRegisterRequestDto requestDto) {
        if (checkEmailExistence(requestDto.getEmail())) {
            throw new IllegalArgumentException("no email");
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
            throw new IllegalArgumentException("empty member");
        }

        final Member member = result.get();

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("password incorrect");
        }

        final Map<String, String> payload = new HashMap<>();

        payload.put("email", member.getEmail());
        payload.put("nickname", member.getNickname());
        payload.put("role", member.getRole().getDisplayName());

        final Map<String, String> refreshPayload = new HashMap<>();

        payload.put("email", member.getEmail());

        return MemberLoginResponseDto.builder()
                .accessToken(jwtProvider.generate(payload))
                .refreshToken(jwtProvider.generate(refreshPayload))
                .build();
    }

}