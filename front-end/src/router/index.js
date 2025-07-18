import { createRouter, createWebHashHistory } from 'vue-router'
import HomePage from '@/components/HomePage.vue'
import TicketPage from '@/components/TicketPage.vue'
import MyPage from '@/components/MyPage.vue'
import LoginPage from '@/components/LoginPage.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage
  },
  {
    path: '/ticket',
    name: 'ticket',
    component: TicketPage
  },
  {
    path: '/my',
    name: 'my',
    component: MyPage,
    meta: {
      requiresAuth: true  // 需要登录才能访问
    }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
