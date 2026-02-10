import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'

/**
 * 路由守卫：检查用户是否登录
 * @param to 目标路由
 * @param from 来源路由
 * @param next 导航守卫回调
 */
export const authGuard = (
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext
) => {
  // 检查路由是否需要认证
  const requiresAuth = to.meta.requiresAuth !== false
  
  // 检查localStorage中是否有token
  const token = localStorage.getItem('token')
  
  if (requiresAuth && !token) {
    // 需要认证但没有token，跳转到登录页
    next('/login')
  } else if (!requiresAuth && token && to.path === '/login') {
    // 不需要认证但已有token，跳转到首页
    next('/')
  } else {
    // 其他情况正常导航
    next()
  }
}

/**
 * 路由守卫：设置页面标题
 * @param to 目标路由
 */
export const titleGuard = (to: RouteLocationNormalized) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - AI全自动研发大脑`
  } else {
    document.title = 'AI全自动研发大脑'
  }
}