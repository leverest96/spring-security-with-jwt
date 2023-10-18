package spring.security.boot2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import spring.security.boot2.domain.Member;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.userdetail.CustomOidcUserService;
import spring.security.boot2.util.JwtProvider;
import spring.security.boot2.util.StringUtility;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final MemberRepository memberRepository;

    private final JwtProvider accessTokenProvider;
    private final JwtProvider refreshTokenProvider;

    @Override
    @Transactional
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final Cookie accessTokenCookie = WebUtils.getCookie(request, AccessTokenProperties.COOKIE_NAME);
        final String accessToken = (accessTokenCookie == null) ? (null) : (accessTokenCookie.getValue());

        final CustomOidcUserService memberDetails = (CustomOidcUserService) authentication.getPrincipal();

        final OAuth oAuth = oAuthRepository.findByIdentifierAndProvider(memberDetails.getName(), memberDetails.getRegistrationId())
                .orElseGet(() -> {
                    final Member member = findProfileWithAccessToken(accessToken)
                            .orElseGet(this::createMember);

                    return createOAuth(member, memberDetails.getName(), memberDetails.getRegistrationId());
                });

        final long profileUuid = oAuth.getMember().getMemberId();

        CookieUtility.addCookie(
                response,
                AccessTokenProperties.COOKIE_NAME,
                accessTokenProvider.generate(Map.of(AccessTokenProperties.AccessTokenClaim.PROFILE_UUID.getClaim(), profileUuid))
        );

        CookieUtility.addCookie(
                response,
                RefreshTokenProperties.COOKIE_NAME,
                refreshTokenProvider.generate(Map.of(RefreshTokenProperties.RefreshTokenClaim.PROFILE_UUID.getClaim(), profileUuid)),
                refreshTokenProvider.getValidSeconds()
        );

        final String provider = Character.toUpperCase(member.getRegistrationId().charAt(0)) + member.getRegistrationId().substring(1);

        log.info("{} OAuth 2.0 authentication request: {}", provider, profileUuid);

        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URL);
    }

    private OAuth createOAuth(final Member member, final String identifier, final String provider) {
        try {
            final OAuth oAuth = OAuth.builder()
                    .member(member)
                    .identifier(identifier)
                    .provider(provider)
                    .build();

            return oAuthRepository.save(oAuth);
        } catch (final DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private Optional<Member> findProfileWithAccessToken(final String accessToken) {
        if (accessToken == null) {
            return Optional.empty();
        }

        try {
            final DecodedJWT decodedAccessToken = accessTokenProvider.verify(accessToken);

            final String profileUuid = decodedAccessToken.getClaim(AccessTokenProperties.AccessTokenClaim.PROFILE_UUID.getClaim()).asString();

            return profileRepository.findByUuid(profileUuid);
        } catch (final JWTVerificationException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    private Member createMember() {
        String nickname = StringUtility.generateRandomString(Member.NICKNAME_MAX_LENGTH);

        while (memberRepository.findByNickname(nickname).isPresent()) {
            nickname = StringUtility.generateRandomString(Member.NICKNAME_MAX_LENGTH);
        }

        final Member member = Member.builder()
                .nickname(nickname)
                .build();

        return memberRepository.save(member);
    }
}