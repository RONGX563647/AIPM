import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { authGuard, titleGuard } from './guards'

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 执行认证守卫
  authGuard(to, from, next)
})

// 全局后置守卫
router.afterEach((to) => {
  // 执行标题守卫
  titleGuard(to)
})

export default router