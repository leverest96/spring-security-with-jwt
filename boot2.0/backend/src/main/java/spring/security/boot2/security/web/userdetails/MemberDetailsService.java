package spring.security.boot2.security.web.userdetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import spring.security.boot2.common.util.JwtProvider;

@RequiredArgsConstructor
@Slf4j
public class MemberDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final JwtProvider accessTokenProvider;

    @Override
    public UserDetails loadUserDetails(final PreAuthenticatedAuthenticationToken token) throws AuthenticationException {
        try {
            final String accessToken = (String) token.getCredentials();

            final Long memberId = accessTokenProvider.getClaimFromToken(accessToken, "memberId");

            return MemberDetails.builder()
                    .memberId(memberId)
                    .build();
        } catch (final Exception ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}
