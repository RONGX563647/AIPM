<template>
  <MainLayout v-if="isLoggedIn" />
  <router-view v-else />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '@/components/layout/MainLayout.vue'

const route = useRoute()
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token')
  const isLoginPage = route.path === '/login' || route.path === '/register' || route.path === '/forgot-password'
  return token && !isLoginPage
})
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  line-height: 1.5;
  color: #E5E5E5;
  background-color: #111113;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #1A1B1E;
  border-radius: 9999px;
}

::-webkit-scrollbar-thumb {
  background: #B38B2D;
  border-radius: 9999px;
}

::-webkit-scrollbar-thumb:hover {
  background: #FFD700;
}
</style>
