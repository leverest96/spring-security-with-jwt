package spring.security.boot2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.security.boot2.common.util.CookieUtility;
import spring.security.boot2.dto.MemberLoginDto;
import spring.security.boot2.dto.MemberLoginResponseDto;
import spring.security.boot2.dto.MemberRegisterDto;
import spring.security.boot2.models.users.Member;
import spring.security.boot2.properties.AccessTokenProperties;
import spring.security.boot2.properties.RefreshTokenProperties;
import spring.security.boot2.security.web.userdetails.MemberDetails;
import spring.security.boot2.service.MemberService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody MemberRegisterDto requestDto) {
        memberService.register(requestDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> login(final HttpServletResponse response, @RequestBody MemberLoginDto requestDto) {
        MemberLoginResponseDto responseDto = memberService.login(requestDto);

        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, responseDto.getAccessToken());
        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, responseDto.getRefreshToken(), responseDto.getRefreshTokenValidSeconds());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<Member> member(@AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = memberService.member(memberDetails.getMemberId());

        return ResponseEntity.ok().body(member);
    }
}
