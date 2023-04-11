package com.example.test.security.etc;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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