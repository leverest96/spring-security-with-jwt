package spring.security.boot2.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.security.boot2.properties.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtProvider {
    private static final Long ACCESS_TOKEN_VALIDATE_TIME = 1000L * 60 * 30;
    private static final Long REFRESH_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24 * 365;

    private final SecretKey secretKey;

    public JwtProvider(final JwtProperties properties) {
        final String keyBase64Encoded = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    public String createAccessToken(long memberId, String loginId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE_TIME);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("memberId", Long.toString(memberId));
        payloads.put("loginId", loginId);

        return Jwts.builder()
                .claims(payloads)
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken(long memberId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDATE_TIME);

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

    public Long getLongClaimFromToken(String token, String name) {
        Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return Long.parseLong((String) claim.get(name));
    }

    public String getStringClaimFromToken(String token, String name) {
        Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return (String) claim.get(name);
    }

    public Long getClaimFromExpirationToken(String expirationToken, String name) {
        try {
            Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(expirationToken).getPayload();

            return Long.parseLong((String) claim.get(name));
        } catch (ExpiredJwtException e) {
            return Long.parseLong((String)e.getClaims().get(name));
        }
    }
}
