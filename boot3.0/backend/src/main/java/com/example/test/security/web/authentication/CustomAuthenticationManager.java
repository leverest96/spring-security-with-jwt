package com.example.test.security.web.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CustomAuthenticationManager implements AuthenticationManager {
    private final List<CustomAuthenticationProvider> authenticationProviders;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        AuthenticationException lastException = null;

        for (final CustomAuthenticationProvider authenticationProvider : authenticationProviders) {
            if (!authenticationProvider.supports(authentication.getClass())) {
                continue;
            }

            try {
                final Authentication result = authenticationProvider.authenticate(authentication);

                if (result != null) {
                    return result;
                }
            } catch (final AuthenticationException e) {
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