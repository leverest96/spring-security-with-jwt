package spring.security.boot2.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Builder
public class MemberDto {
    private String registrationId;
    private String id;
    private String ci;
    private String username;
    private String password;
    private String provider;
    private String email;
    private String picture;
    private List<? extends GrantedAuthority> authorities;
}