package com.example.test.security.etc;

import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.utility.CookieUtility;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("")
    public ResponseEntity<MemberRegisterResponseDto> register(@RequestBody final MemberRegisterRequestDto requestDto) {
        final MemberRegisterResponseDto result = memberService.register(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> login(@RequestBody final MemberLoginRequestDto requestDto,
                                                        final HttpServletResponse response) {
        final MemberLoginResponseDto result = memberService.login(requestDto);

        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, result.getAccessToken());
        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, result.getRefreshToken(), 6480000);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}