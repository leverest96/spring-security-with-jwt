import { createStore } from "vuex";
import axios from "axios";
import router from ".";

axios.defaults.baseURL = 'http://localhost:8080'

export default createStore({
  state: {
    member: {},
    members: [],
    followers: [], 
    followings: [],
    loginUser: null,
  },
  getters: {
    followerCnt: function (state) {
      return state.followers.length;
    },
  },
  mutations: {
    SIGN_UP: function (state, member) {
      state.members.push(member)
    },
    LOG_IN: function (state, member) {
      state.loginUser = member
    },
  },
  actions: {
    signup: function ({ commit }, member) {
      const API_URL = '/member/signup';
      axios({
        url: API_URL,
        method: 'POST',
        data: member
      })
        .then(() => {
          commit;
          alert(member.nickname + '님 회원가입을 축하드립니다!');
          setTimeout(router.go(0), 500);
        })
        .catch(() => {
          alert(`회원 가입에 실패했습니다`);
        })
    },
    login: function ({ commit }, member) {
      const API_URL = '/member/login';

      axios({
        url: API_URL,
        method: 'POST',
        data: member
      })
        .then((res) => {
          commit('LOG_IN', res.data);
          alert(res.data.nickname + '님 환영합니다');
          const loginData = JSON.stringify(res.data);
          sessionStorage.setItem("loginUser", loginData);
          router.push('/')
        })
        .catch(() => {
            alert('로그인에 실패했습니다');
        })
    },
    logout: function ({ commit }) {
      const API_URL = '/member/logout';
      axios({
        url: API_URL,
        method: 'POST'
      })
        .then(() => {
          commit;
          alert('좋은 하루 되시길 바랍니다!');
          sessionStorage.removeItem('loginUser')
          router.push('/')
        })
        .catch(() => {
            alert('로그아웃에 실패했습니다');
        })
    },
  },
  modules: {},
});
  