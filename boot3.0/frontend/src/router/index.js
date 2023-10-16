import { createRouter, createWebHistory } from 'vue-router'
import AdminPage from '../views/MainPage.vue'
import IndexPage from '../views/SignPage.vue'
import LoginPage from '../views/SearchPage.vue'
import MemberPage from '../views/DetailPage.vue'
import SignupPage from '../views/MyPage.vue'

const routes = [
  {
    path: '/',
    name: 'main',
    component: IndexPage
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminPage
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage
  },
  {
    path: '/member/:id',
    name: 'member',
    component: MemberPage
  },
  {
    path: '/signup',
    name: 'signup',
    component: SignupPage
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
