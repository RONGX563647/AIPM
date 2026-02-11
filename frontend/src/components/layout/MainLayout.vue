<template>
  <div class="main-layout">
    <SmartSidebar @update:collapsed="updateSidebarCollapsed" />
    
    <div class="main-content" :class="{ 'is-collapsed': isSidebarCollapsed }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
    
    <AISidebar ref="aiSidebarRef" />
    <AIFloatingBall :hidden="isSidebarVisible" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import SmartSidebar from './SmartSidebar.vue'
import AISidebar from '../ai/AISidebar.vue'
import AIFloatingBall from '../ai/AIFloatingBall.vue'

const isSidebarCollapsed = ref(true)
const isSidebarVisible = ref(false) // AI侧边栏是否显示
const aiSidebarRef = ref(null)

const updateSidebarCollapsed = (collapsed) => {
  isSidebarCollapsed.value = collapsed
}

// 处理AI侧边栏显示/隐藏
const handleAISidebarToggle = (event) => {
  isSidebarVisible.value = event.detail.visible
  
  // 根据AI侧边栏的显示状态更新body类，以便CSS控制主内容区域
  if (event.detail.visible) {
    document.body.classList.add('ai-sidebar-open')
  } else {
    document.body.classList.remove('ai-sidebar-open')
  }
}

// 处理AI侧边栏关闭事件
const handleAISidebarClose = () => {
  isSidebarVisible.value = false;
  document.body.classList.remove('ai-sidebar-open');
}

onMounted(() => {
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  if (savedCollapsed !== null) {
    isSidebarCollapsed.value = savedCollapsed === 'true'
  }
  
  // 监听AI侧边栏开关事件
  window.addEventListener('ai-sidebar-toggle', handleAISidebarToggle)
  window.addEventListener('ai-sidebar-close', handleAISidebarClose)
})

onUnmounted(() => {
  // 清理事件监听器
  window.removeEventListener('ai-sidebar-toggle', handleAISidebarToggle)
  window.removeEventListener('ai-sidebar-close', handleAISidebarClose)
  
  // 移除可能存在的类
  document.body.classList.remove('ai-sidebar-open')
})
</script>

<style scoped lang="scss">
.main-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1A1B1E 100%);
  position: relative;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  margin-right: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100vh;

  &.is-collapsed {
    margin-left: 64px;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1100px) {
  body:not(.ai-sidebar-open) .main-content {
    margin-right: 0; // 在中等屏幕上不预留AI侧边栏空间
  }
}

@media (max-width: 768px) {
  body:not(.ai-sidebar-open) .main-content {
    margin-left: 64px;
    margin-right: 0; // 在小屏幕上不预留AI侧边栏空间
  }
}
</style>