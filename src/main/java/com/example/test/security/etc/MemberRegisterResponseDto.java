package com.example.test.security.etc;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Builder
@RequiredArgsConstructor
public class MemberRegisterResponseDto {

    private final String nickname;

}
