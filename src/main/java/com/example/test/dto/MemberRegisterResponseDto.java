package com.example.test.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Builder
@RequiredArgsConstructor
public class MemberRegisterResponseDto {
    private final String nickname;
}
