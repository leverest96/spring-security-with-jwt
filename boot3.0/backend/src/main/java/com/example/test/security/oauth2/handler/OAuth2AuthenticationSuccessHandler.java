package com.example.test.security.oauth2.handler;

import com.example.test.domain.Member;
import com.example.test.domain.redis.AccessToken;
import com.example.test.properties.jwt.AccessTokenProperties;
import com.example.test.repository.MemberRepository;
import com.example.test.repository.RedisRepository;
import com.example.test.security.oauth2.oauth2user.ProviderUser;
import com.example.test.security.oauth2.oauth2userdetails.CustomOAuth2User;
import com.example.test.utility.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REDIRECT_URL = "http://localhost:5173/";

    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    @Override
    @Transactional
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final CustomOAuth2User principalUser = (CustomOAuth2User) authentication.getPrincipal();

        final ProviderUser providerUser = principalUser.providerUser();

        final Member member = findMemberWithRedisAccessToken(providerUser.getLoginId()).orElseGet(
                () -> createMember(providerUser)
        );

        final long memberId = member.getId();
        final String loginId = member.getLoginId();

        final String accessToken = accessTokenProvider.createAccessToken(memberId, loginId);
        redisRepository.save(new AccessToken(AccessToken.ACCESS_TOKEN_KEY, accessToken));

        final String refreshToken = refreshTokenProvider.createRefreshToken(memberId);
        member.updateRefreshToken(refreshToken);

        final String provider = Character.toUpperCase(member.getLoginType().getSocialName().charAt(0)) + member.getLoginType().getSocialName().substring(1);

        log.info("{} OAuth 2.0 authentication request: {}", provider, memberId);

        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URL);
    }

    private Optional<Member> findMemberWithRedisAccessToken(final String providedLoginId) {
        Optional<AccessToken> accessTokenUnchecked = redisRepository.findById(providedLoginId);

        if (accessTokenUnchecked.isEmpty()) {
            return Optional.empty();
        }

        try {
            String accessToken = accessTokenUnchecked.get().getAccessToken();

            final long memberId = accessTokenProvider.getLongClaimFromToken(accessToken, AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());
            final String loginId = accessTokenProvider.getStringClaimFromToken(accessToken, AccessTokenProperties.AccessTokenClaim.LOGIN_ID.getClaim());

            if (!loginId.equals(providedLoginId)) {
                return Optional.empty();
            }

            return memberRepository.findById(memberId);
        } catch (final Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    private Member createMember(final ProviderUser providerUser) {
        final Optional<Member> existingMember = memberRepository
                .findByEmailAndLoginType(providerUser.getEmail(), providerUser.getLoginType());

        if (existingMember.isPresent()) {
            return existingMember.get();
        }

        final Member member = Member.builder()
                .email(providerUser.getEmail())
                .password(UUID.randomUUID().toString())
                .nickname(providerUser.getNickname())
                .loginId(providerUser.getLoginId())
                .profile(providerUser.getProfile())
                .genderType(providerUser.getGenderType())
                .roleType(providerUser.getRole())
                .loginType(providerUser.getLoginType())
                .build();

        return memberRepository.save(member);
    }
}