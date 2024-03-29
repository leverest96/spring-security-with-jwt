package spring.security.boot2.models.users;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.security.boot2.common.enums.GenderType;
import spring.security.boot2.common.enums.LoginType;
import spring.security.boot2.common.enums.MemberRole;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    public static final int NICKNAME_MAX_LENGTH = 20;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private LocalDateTime deletedAt;

    @Builder
    public Member(String loginId,
                  String nickname,
                  String email,
                  String password,
                  String profile,
                  MemberRole role,
                  LoginType loginType,
                  GenderType genderType) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
        this.loginType = loginType;
        this.genderType = genderType;
    }
}
