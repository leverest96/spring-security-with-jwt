import { useEffect, useState } from 'react';
import axios from 'axios';

const MemberPage = () => {
  const [email, setEmail] = useState('');
  const [nickname, setNickname] = useState('');

  useEffect(() => {
    const getMember = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/member", { withCredentials: true });
        const { email, nickname } = response.data;
        setEmail(email);
        setNickname(nickname);
      } catch (error) {
        window.location.replace("/")
      }
    };

    getMember();
  }, []);

  return (
    <div className="container is-max-desktop">
      <section className="section">
        <div className="box is-flex is-flex-direction-column py-6 has-background-white-bis is-shadowless">
          <div className="block is-flex is-align-content-center is-justify-content-center">
            <h1 className="title">사용자 페이지</h1>
          </div>

          <div className="my-5 px-6">
            <h5 className="title is-5 mb-3">이메일</h5>
            <p>{email}</p>
          </div>

          <div className="my-5 px-6">
            <h5 className="title is-5 mb-3">닉네임</h5>
            <p>{nickname}</p>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center">
            <a href="/" className="button">
              돌아가기
            </a>
          </div>
        </div>
      </section>
    </div>
  );
};

export default MemberPage;