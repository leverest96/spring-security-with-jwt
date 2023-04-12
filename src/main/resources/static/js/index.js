function logout() {
    $.ajax({
        type: 'POST',
        url: '/api/member/logout',
        contentType: 'application/json'
    }).done(function () {
        alert('로그인을 성공했습니다.');
        window.location.replace('/');
    }).fail(function () {
        alert('로그인을 실패했습니다.');
    });
}