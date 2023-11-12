package spring.security.boot2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.security.boot2.dto.MemberLoginDto;
import spring.security.boot2.dto.MemberRegisterDto;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.service.MemberService;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody MemberRegisterDto requestDto) {
        memberService.register(requestDto);
    }

    @GetMapping("login")
    public ResponseEntity<Member> login(@RequestBody MemberLoginDto requestDto) {
        return memberService.login(requestDto);
    }

    @PostMapping("/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestBody SocialLoginRequestDto requestDto) throws IOException {
        return socialLoginService.kakaoLogin(requestDto);
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDto> googleLogin(@RequestBody SocialLoginRequestDto requestDto) throws IOException {
        return socialLoginService.googleLogin(requestDto);
    }

}
