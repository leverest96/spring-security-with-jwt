package com.example.test.controller;

import com.example.test.dto.MemberInfoResponseDto;
import com.example.test.dto.MemberLoginRequestDto;
import com.example.test.dto.MemberLoginResponseDto;
import com.example.test.dto.MemberRegisterRequestDto;
import com.example.test.properties.jwt.AccessTokenProperties;
import com.example.test.properties.jwt.RefreshTokenProperties;
import com.example.test.security.web.userdetails.MemberDetails;
import com.example.test.utility.CookieUtility;
import com.example.test.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @RequestMapping(value = "/email/{email}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkEmailExistence(@PathVariable final String email) {
        return (memberService.checkEmailExistence(email)) ?
                (ResponseEntity.ok().build()) :
                (ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody final MemberRegisterRequestDto requestDto) {
        memberService.register(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> login(@RequestBody final MemberLoginRequestDto requestDto,
                                                        final HttpServletResponse response) {
        final MemberLoginResponseDto result = memberService.login(requestDto);

        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, result.getAccessToken());
        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, result.getRefreshToken(), result.getRefreshTokenValidSeconds());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<MemberInfoResponseDto> member(@AuthenticationPrincipal final MemberDetails memberDetails) {
        final MemberInfoResponseDto responseDto = memberService.member(memberDetails.getLoginId());

        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        CookieUtility.deleteCookie(response, AccessTokenProperties.COOKIE_NAME);
        CookieUtility.deleteCookie(response, RefreshTokenProperties.COOKIE_NAME);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}