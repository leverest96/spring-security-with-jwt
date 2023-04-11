package com.example.test.security.etc;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class MemberLoginResponseDto {

    private final String accessToken;
    private final String refreshToken;

}