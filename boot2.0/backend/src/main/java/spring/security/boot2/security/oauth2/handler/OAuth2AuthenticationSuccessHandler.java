package spring.security.boot2.security.oauth2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;
import spring.security.boot2.common.util.CookieUtility;
import spring.security.boot2.common.util.JwtProvider;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.models.users.ProviderUser;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.repository.MemberRepository;
import spring.security.boot2.security.oauth2.oauth2user.CustomOAuth2User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

// oauth2 로그인 성공시 cookie에 값을 전달하기 위한 클래스
// 필요시 회원가입도 동시에 진행
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REDIRECT_URL = "http://localhost:5173/";

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

        final CustomOAuth2User principalUser = (CustomOAuth2User) authentication.getPrincipal();

        final ProviderUser providerUser = principalUser.getProviderUser();

        // access token의 정보를 통해 member를 찾고 없다면 생성
        final Member member = findMemberWithAccessToken(accessToken, providerUser.getLoginId()).orElseGet(
                () -> createMember(providerUser)
        );

        final long memberId = member.getId();
        final String loginId = member.getLoginId();

        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, accessTokenProvider.createAccessToken(memberId, loginId));
        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, refreshTokenProvider.createRefreshToken(memberId), refreshTokenProvider.getValidSeconds());

        final String provider = Character.toUpperCase(member.getLoginType().getSocialName().charAt(0)) + member.getLoginType().getSocialName().substring(1);

        log.info("{} OAuth 2.0 authentication request: {}", provider, memberId);

        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URL);
    }

    private Optional<Member> findMemberWithAccessToken(final String accessToken, final String providedLoginId) {
        if (accessToken == null) {
            return Optional.empty();
        }

        // access token과 현재 oauth2 로그인을 진행 중인 loginId를 비교하여 같다면 member 반환
        try {
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