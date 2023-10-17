package spring.security.boot2.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private final List<CustomAuthenticationProvider> authenticationProviders;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationException lastException = null;

        for (CustomAuthenticationProvider authenticationProvider : authenticationProviders) {
            if (!authenticationProvider.supports(authentication.getClass())) {
                continue;
            }

            try {
                Authentication result = authenticationProvider.authenticate(authentication);

                if (result != null) {
                    return result;
                }
            } catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (lastException != null) {
            throw lastException;
        } else {
            throw new ProviderNotFoundException("해당하는 AuthenticationProvider가 없습니다. Authentication : " + authentication.getClass());
        }
    }
}
