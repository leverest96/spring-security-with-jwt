package spring.security.boot2.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import spring.security.boot2.properties.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// JWT 생성용 코드
public class JwtProvider {
    private static final Long ACCESS_TOKEN_VALIDATE_TIME = 1000L * 60 * 30;
    private static final Long REFRESH_TOKEN_VALIDATE_TIME = 1000L * 60 * 60 * 24 * 365;

    private final SecretKey secretKey;

    // 생성자 및 초기화
    public JwtProvider(final JwtProperties properties) {
        final String keyBase64Encoded = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    // AccessToken 생성
    public String createAccessToken(long memberId, String loginId) {
        // 생성 및 만료 일자
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE_TIME);

        // 페이로드 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("memberId", Long.toString(memberId));
        payloads.put("loginId", loginId);

        // 생성
        return Jwts.builder()
                .claims(payloads)
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(long memberId) {
        // 생성 및 만료 일자
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDATE_TIME);

        // 페이로드 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("memberId", Long.toString(memberId));

        // 생성
        return Jwts.builder()
                .claims(payloads)
                .subject("auth")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // Token 유효성 검증
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

    // Token에서 memberId를 판별하기 위한 메서드
    public Long getLongClaimFromToken(String token, String name) {
        Claims claim = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return Long.parseLong((String) claim.get(name));
    }

    // Token에서 loginId를 판별하기 위한 메서드
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
