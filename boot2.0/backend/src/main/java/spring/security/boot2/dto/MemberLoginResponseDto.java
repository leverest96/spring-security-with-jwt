package spring.security.boot2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MemberLoginResponseDto {
    private final String accessToken;
    private final String refreshToken;
    private final int refreshTokenValidSeconds;
}
