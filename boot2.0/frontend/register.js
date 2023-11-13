function checkEmailDuplication() {
  const email = $("#email-input").val();

  $.ajax({
    type: "HEAD",
    url: `/api/member/email/${email}`,
  })
    .done(function () {
      alert("중복된 이메일입니다.");
    })
    .fail(function (jqXHR) {
      if (jqXHR.status === 400) {
        alert("잘못된 이메일 형식입니다.");
      } else if (jqXHR.status === 404) {
        alert("사용 가능한 이메일입니다.");
      }
    });
}

function register() {
  const email = $("#email-input").val();
  const password = $("#password-input").val();
  const nickname = $("#nickname-input").val();

  $.ajax({
    type: "POST",
    url: "http://localhost:8080/api/member/register",
    contentType: "application/json",
    data: JSON.stringify({
      email: email,
      password: password,
      nickname: nickname,
    }),
  })
    .done(function (data) {
      alert(`${data.nickname}님 회원가입을 환영합니다.`);
      window.location.replace("/");
    })
    .fail(function () {
      alert("회원가입을 실패했습니다.");
    });
}
