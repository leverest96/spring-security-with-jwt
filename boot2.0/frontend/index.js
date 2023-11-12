function logout() {
    const result = $.removeCookie('accessToken', {
        'path': './index.html'
    });

    if (result) {
        alert('로그아웃을 성공했습니다.');
    } else {
        alert('로그인 상태가 아닙니다.');
    }
}