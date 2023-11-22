package com.example.test.utility;

import com.example.test.properties.jwt.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.util.*;

@Getter
public class JwtProvider {
    private static final Long ACCESS_TOKEN_VALIDATE_TIME = 1000L * 30;
    private static final Long REFRESH_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24 * 365;

    private final SecretKey secretKey;

    private final int validSeconds;

    public JwtProvider(final JwtProperties properties) {
        final String keyBase64Encoded = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
        this.validSeconds = properties.getValidSeconds();
    }

    public String createAccessToken(final long memberId, final String loginId) {
        final Date now = new Date();
        final Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE_TIME);

        final Map<String, Object> payloads = new HashMap<>();
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

    public String createRefreshToken(final long memberId) {
        final Date now = new Date();
        final Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDATE_TIME);

        final Map<String, Object> payloads = new HashMap<>();
        payloads.put("memberId", Long.toString(memberId));

        return Jwts.builder()
                .claims(payloads)
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("jwt not valid");
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("unsupported jwt");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("empty jwt");
        }
    }

    public Long getLongClaimFromToken(final String token, final String name) {
        final Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return Long.parseLong((String) claim.get(name));
    }

    public String getStringClaimFromToken(final String token, final String name) {
        final Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return (String) claim.get(name);
    }

    public Long getLongClaimFromExpirationToken(final String expirationToken, final String name) {
        try {
            final Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(expirationToken).getPayload();

            return Long.parseLong((String) claim.get(name));
        } catch (ExpiredJwtException e) {
            return Long.parseLong((String)e.getClaims().get(name));
        }
    }

    public String getStringClaimFromExpirationToken(final String expirationToken, final String name) {
        try {
            final Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(expirationToken).getPayload();

            return (String) claim.get(name);
        } catch (ExpiredJwtException e) {
            return (String)e.getClaims().get(name);
        }
    }
}