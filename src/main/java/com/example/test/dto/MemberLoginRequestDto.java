package com.example.test.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Builder
@RequiredArgsConstructor
public class MemberLoginRequestDto {
    private final String email;
    private final String password;
}