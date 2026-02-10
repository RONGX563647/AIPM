import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { jwtDecode } from 'jwt-decode'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const roles = ref([])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => true) // 取消RBAC，所有用户都有管理员权限
  const isSuperAdmin = computed(() => true) // 取消RBAC，所有用户都是超级管理员

  // 解析token获取用户信息
  const parseToken = () => {
    if (!token.value) {
      userInfo.value = null
      roles.value = []
      return
    }

    try {
      const decoded = jwtDecode(token.value)
      userInfo.value = {
        id: decoded.uid,
        username: decoded.uname,
        subject: decoded.sub
      }
      roles.value = decoded.roles || []
    } catch (error) {
      console.error('Token解析失败:', error)
      logout()
    }
  }

  // 登录
  const login = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    parseToken()
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    roles.value = []
    localStorage.removeItem('token')
  }

  // 检查权限 - 取消RBAC，所有用户都有所有权限
  const hasPermission = (requiredRoles) => {
    return true // 所有用户都有权限
  }

  // 初始化
  const init = () => {
    parseToken()
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    
    // 计算属性
    isLoggedIn,
    isAdmin,
    isSuperAdmin,
    
    // 方法
    login,
    logout,
    hasPermission,
    init
  }
})