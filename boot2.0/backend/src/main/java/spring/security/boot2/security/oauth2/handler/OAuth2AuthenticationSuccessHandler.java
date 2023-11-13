package spring.security.boot2.security.oauth2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import spring.security.boot2.common.util.CookieUtility;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.models.users.PrincipalUser;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.common.util.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REDIRECT_URL = "/";

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

        final PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        final ProviderUser providerUser = principalUser.getProviderUser();

        final Member member = findMemberWithAccessToken(accessToken).orElseGet(
                () -> createMember(providerUser)
        );

        final long memberId = member.getId();

        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, accessTokenProvider.createAccessToken(memberId));

        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, refreshTokenProvider.createRefreshToken());

        final String provider = Character.toUpperCase(member.getLoginType().getSocialName().charAt(0)) + member.getLoginType().getSocialName().substring(1);

        log.info("{} OAuth 2.0 authentication request: {}", provider, memberId);

        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URL);
    }

    private Optional<Member> findMemberWithAccessToken(final String accessToken) {
        if (accessToken == null) {
            return Optional.empty();
        }

        try {
            final long memberId = accessTokenProvider.getClaimFromToken(accessToken, AccessTokenProperties.AccessTokenClaim.MEMBER_ID.getClaim());

            return memberRepository.findById(memberId);
        } catch (final Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    private Member createMember(final ProviderUser providerUser) {
        final Member member = Member.builder()
                .email(providerUser.getEmail())
                .nickname(providerUser.getNickname())
                .role(providerUser.getRole())
                .loginId(providerUser.getLoginId())
                .genderType(providerUser.getGenderType())
                .profile(providerUser.getProfile())
                .loginType(providerUser.getLoginType())
                .build();

        return memberRepository.save(member);
    }
}