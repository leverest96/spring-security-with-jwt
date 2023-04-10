package com.example.test.security.controller;

import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.utility.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final JwtProvider accessTokenProvider;

    private final JwtProvider refreshTokenProvider;

    public String login(final int id) {
        final Users user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        final int userId = user.getId();

        final Map<String, Integer> accessTokenPayload = new HashMap<>();
        accessTokenPayload.put(AccessTokenProperties.AccessTokenClaim.USER_ID.getClaim(), userId);

        final Map<String, Integer> refreshTokenPayload = new HashMap<>();
        refreshTokenPayload.put(RefreshTokenProperties.RefreshTokenClaim.USER_ID.getClaim(), userId);

        StringBuilder sb = new StringBuilder();

        final String accessToken = accessTokenProvider.generate(accessTokenPayload);
        sb.append(accessToken);

        final String refreshToken = refreshTokenProvider.generate(refreshTokenPayload);
        sb.append(refreshToken);

        final int refreshTokenValidSeconds = refreshTokenProvider.getValidSeconds();
        sb.append(refreshTokenValidSeconds);

        return sb.toString();
    }
}