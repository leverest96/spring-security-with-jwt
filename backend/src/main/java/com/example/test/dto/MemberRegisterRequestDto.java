package com.example.test.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Builder
@RequiredArgsConstructor
public class MemberRegisterRequestDto {
    private final String email;
    private final String password;
    private final String nickname;
    private final boolean admin;
}