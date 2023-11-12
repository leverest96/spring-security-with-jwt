package spring.security.boot2.security.web.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) return null;

        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("No access token credentials found in request");
        }

        final PreAuthenticatedAuthenticationToken preAuthenticatedToken = (PreAuthenticatedAuthenticationToken) authentication;

        final UserDetails userDetails = authenticationUserDetailsService.loadUserDetails(preAuthenticatedToken);

        final PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(
                userDetails,
                authentication.getCredentials(),
                userDetails.getAuthorities()
        );

        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
