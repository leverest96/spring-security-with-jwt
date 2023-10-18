package spring.security.boot2.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.security.boot2.properties.JwtProperties;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {
    private static final Long ACCESS_TOKEN_VALIDATE_TIME = 1000L * 60 * 30;
    private static final Long REFRESH_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24 * 365;

    private final SecretKey secretKey;

    public JwtProvider(final JwtProperties properties) {
        final String keyBase64Encoded = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    public String createAccessToken(long memberId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE_TIME);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("memberId", Long.toString(memberId));

        return Jwts.builder()
                .claims(payloads)
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDATE_TIME);

        return Jwts.builder()
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("jwt not valid");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("unsupported jwt");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("empty jwt");
        }
    }

    public boolean isExpired(String token) {
        Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        Date expiration = claim.getExpiration();
        return expiration.before(new Date());
    }

    public Long getClaimFromToken(String token, String name) {
        Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return Long.parseLong((String) claim.get(name));
    }

    public Long getClaimFromExpirationToken(String expirationToken, String name) {
        try {
            Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(expirationToken).getPayload();

            return Long.parseLong((String) claim.get(name));
        } catch (ExpiredJwtException e) {
            return Long.parseLong((String)e.getClaims().get(name));
        }
    }

    public String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        accessToken = accessToken.replace("Bearer ", "");
        return accessToken;
    }
}
