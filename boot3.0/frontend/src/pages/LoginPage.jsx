import axios from 'axios';

const LoginPage = () => {
  const login = () => {
    const email = document.getElementById("email-input").value;
    const password = document.getElementById("password-input").value;

    axios.post("http://localhost:8080/api/member/login", {
      email: email,
      password: password,
    }, {
      withCredentials: true
    })
      .then((response) => {
        alert("로그인을 성공했습니다." + response);
        window.location.replace("/");
      })
      .catch((error) => {
        alert("로그인을 실패했습니다." + error);
      });
  };

  return (
    <div className="container is-max-desktop">
      <section className="section">
        <div className="box is-flex is-flex-direction-column py-6 has-background-white-bis is-shadowless">
          <div className="block is-flex is-align-content-center is-justify-content-center">
            <h1 className="title">로그인 페이지</h1>
          </div>

          <div className="field mt-5 px-6">
            <p className="control has-icons-left">
              <input type="email" placeholder="이메일" id="email-input" className="input" />

              <span className="icon is-small is-left">
                <i className="fa-solid fa-envelope"></i>
              </span>
            </p>
          </div>

          <div className="field mt-4 mb-5 px-6">
            <p className="control has-icons-left">
              <input type="password" placeholder="비밀번호" id="password-input" className="input" />

              <span className="icon is-small is-left">
                <i className="fa-solid fa-lock"></i>
              </span>
            </p>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center">
            <div className="buttons">
              <button className="button is-info" onClick={login}>로그인</button>
              <a href="/" className="button">돌아가기</a>
            </div>
          </div>
          <div className="block is-flex is-align-content-center is-justify-content-center">
            <div>
              <a className='button' style={{ margin: "0 1rem 0 0" }} href='http://localhost:8080/oauth2/authorization/google'>구글</a>
              <a className='button' style={{ margin: "0 1rem 0 0", backgroundColor: "yellow" }} href='http://localhost:8080/oauth2/authorization/kakao'>카카오</a>
              <a className='button' style={{ backgroundColor: "green", color: "white" }} href='http://localhost:8080/oauth2/authorization/naver'>네이버</a>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default LoginPage;
