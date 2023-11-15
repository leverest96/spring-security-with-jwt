import { useCookies } from 'react-cookie';

const IndexPage = () => {
  const COOKIE_KEY = "access_token";

  const [, , removeCookie] = useCookies(COOKIE_KEY);
  const logout = () => {
    try {
      removeCookie(COOKIE_KEY, { path: '/' });
      alert("로그아웃을 성공했습니다.");
    } catch (error) {
      alert("로그인 상태가 아닙니다.");
    }
  };

  return (
    <div className="container is-max-desktop" >
      <section className="section">
        <div className="box is-flex is-flex-direction-column py-6 has-background-light is-shadowless">
          <div className="block is-flex is-align-content-center is-justify-content-center">
            <h1 className="title">인덱스 페이지</h1>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center mt-5 mb-3">
            <h4 className="subtitle is-4">회원 기능</h4>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center">
            <div className="buttons are-medium">
              <a href="/register" className="button has-text-white has-background-grey">회원가입</a>
              <a href="/login" className="button has-text-white has-background-grey">로그인</a>
              <button className="button has-text-white has-background-grey" onClick={logout}>로그아웃</button>
            </div>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center mt-5 mb-3">
            <h4 className="subtitle is-4">페이지 기능</h4>
          </div>

          <div className="block is-flex is-align-content-center is-justify-content-center">
            <div className="buttons are-medium">
              <a href="/member" className="button has-text-white has-background-grey-dark">사용자 페이지</a>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default IndexPage;