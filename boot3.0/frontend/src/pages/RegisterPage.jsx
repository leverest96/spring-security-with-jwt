import { useState } from 'react';
import axios from 'axios';

const RegisterPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [isAdmin, setAdmin] = useState(false);

  const checkEmailDuplication = () => {
    alert('이메일 중복 확인');
  };

  const register = () => {
    const email = document.getElementById("email-input").value;
    const password = document.getElementById("password-input").value;
    const nickname = document.getElementById("nickname-input").value;
    const isAdmin = document.getElementById("admin-checkbox").value;

    axios.post("http://localhost:8080/api/member/register", {
        email: email,
        nickname: nickname,
        password: password,
        admin: isAdmin
    })
      .then((response) => {
        alert("회원가입을 성공했습니다." + response);
        window.location.replace("/");
      })
      .catch((error) => {
        alert("회원가입을 실패했습니다." + error);
      });
  };

  return (
    <div className="container is-max-desktop">
      <section className="section">
        <div className="box is-flex is-flex-direction-column py-6 has-background-white-bis is-shadowless">
          <div className="block is-flex is-align-content-center is-justify-content-center">
            <h1 className="title">회원가입 페이지</h1>
          </div>

          <div className="field has-addons mt-5 px-6">
            <p className="control has-icons-left is-expanded">
              <input
                type="email"
                placeholder="이메일"
                id="email-input"
                className="input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />

              <span className="icon is-small is-left">
                <i className="fa-solid fa-envelope"></i>
              </span>
            </p>

            <p className="control">
              <button className="button" onClick={checkEmailDuplication}>
                중복 확인
              </button>
            </p>
          </div>

          <div className="field mt-4 px-6">
            <p className="control has-icons-left">
              <input
                type="password"
                placeholder="비밀번호"
                id="password-input"
                className="input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />

              <span className="icon is-small is-left">
                <i className="fa-solid fa-lock"></i>
              </span>
            </p>
          </div>

          <div className="field mt-4 px-6">
            <p className="control has-icons-left">
              <input
                type="text"
                placeholder="닉네임"
                id="nickname-input"
                className="input"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
              />

              <span className="icon is-small is-left">
                <i className="fa-solid fa-user"></i>
              </span>
            </p>
          </div>

          <div className="field mt-4 px-6" style={{ "display": "flex", "justifyContent": "center" }}>
            <p className="control has-icons-left">
              <input
                type="checkbox"
                id="admin-checkbox"
                value={isAdmin}
                onChange={() => setAdmin(true)}
              />
              관리자
            </p>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center">
            <div className="buttons">
              <button className="button is-info" onClick={register}> 
                회원가입
              </button>
              <a href="/" className="button">
                돌아가기
              </a>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default RegisterPage;