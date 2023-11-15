package spring.security.boot2.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtility {
    // token의 expireDate만 사용할 때 사용
    public static Cookie createCookie(final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);

        // https 설정시 아래 두 옵션 주석 해제
        // cookie.setHttpOnly(true);
        // cookie.setSecure(false);
        cookie.setPath("/");

        return cookie;
    }

    // token의 expireDate뿐만 아니라 cookie의 maxAge를 사용할 때 사용
    public static Cookie createCookie(final String name, final String value, final int maxAge) {
        final Cookie cookie = createCookie(name, value);

        // https 설정시 아래 두 옵션 주석 해제
        // cookie.setHttpOnly(true);
        // cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    // 실제 응답에 Cookie를 얹는 메서드
    public static void addCookie(final HttpServletResponse response,
                                 final Cookie cookie) {
        response.addCookie(cookie);
    }

    // 실제 코드에서 사용될 메서드 1
    public static void addCookie(final HttpServletResponse response,
                                 final String name,
                                 final String value) {
        addCookie(response, createCookie(name, value));
    }

    // 실제 코드에서 사용될 메서드 2
    public static void addCookie(final HttpServletResponse response,
                                 final String name,
                                 final String value,
                                 final int maxAge) {
        addCookie(response, createCookie(name, value, maxAge));
    }

    // Cookie 삭제
    public static void deleteCookie(final HttpServletResponse response,
                                    final String name) {
        addCookie(response, createCookie(name, null, 0));
    }
}