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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import SmartSidebar from './SmartSidebar.vue'

const isSidebarCollapsed = ref(true)

const updateSidebarCollapsed = (collapsed) => {
  isSidebarCollapsed.value = collapsed
}

onMounted(() => {
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  if (savedCollapsed !== null) {
    isSidebarCollapsed.value = savedCollapsed === 'true'
  }
})
</script>

<style scoped lang="scss">
.main-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1A1B1E 100%);
}

.main-content {
  flex: 1;
  margin-left: 240px;
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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

@media (max-width: 768px) {
  .main-content {
    margin-left: 64px;
  }
}
</style>