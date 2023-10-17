package spring.security.boot2.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// request 요청당 1회 가장 먼저 응답
// AuthenticationFilter는 OncePerRequestFilter가 extends 되어있다.
public class CustomAuthenticationFilter extends AuthenticationFilter {
    // 사용할 AuthenticationManager는 아래 생성자에서 initialize된다.
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      AuthenticationConverter authenticationConverter) {
        super(authenticationManager, authenticationConverter);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            CustomAuthenticationConverter authenticationConverter = new CustomAuthenticationConverter();

            Authentication jwtAuthenticationToken = authenticationConverter.convert(request);

            Authentication authentication = authenticationManager.authenticate(jwtAuthenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
