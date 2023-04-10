package com.example.test.security.controller;

import com.example.test.security.properties.jwt.AccessTokenProperties;
import com.example.test.security.properties.jwt.RefreshTokenProperties;
import com.example.test.security.utility.CookieUtility;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final int id, final HttpServletResponse response) {
        final String sb = userService.login(id);

        System.out.println(sb);

//        CookieUtility.addCookie(response, AccessTokenProperties.COOKIE_NAME, responseDto.getAccessToken());
//        CookieUtility.addCookie(response, RefreshTokenProperties.COOKIE_NAME, responseDto.getRefreshToken(), responseDto.getRefreshTokenValidSeconds());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse response) {
        CookieUtility.deleteCookie(response, AccessTokenProperties.COOKIE_NAME);
        CookieUtility.deleteCookie(response, RefreshTokenProperties.COOKIE_NAME);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}