package spring.security.boot2.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRegisterDto {
    private String email;
    private String nickname;
    private String password;
}
