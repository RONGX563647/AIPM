<template>
  <div
    class="smart-sidebar"
    :class="{ 'is-collapsed': isCollapsed, 'is-fixed': isFixed }"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <div class="sidebar-content">
      <div class="sidebar-header">
        <div class="logo-container">
          <el-icon class="logo-icon" :size="24"><Management /></el-icon>
          <transition name="fade">
            <span v-show="!isCollapsed" class="logo-text">AI研发大脑</span>
          </transition>
        </div>
        <el-button
          v-show="!isCollapsed"
          class="collapse-btn"
          @click="toggleFixed"
          text
        >
          <el-icon><component :is="isFixed ? Lock : Unlock" /></el-icon>
        </el-button>
      </div>

      <el-scrollbar class="sidebar-menu">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          @select="handleMenuSelect"
          class="sidebar-el-menu"
        >
          <el-menu-item 
            v-for="item in accessibleMenus" 
            :key="item.index" 
            :index="item.index"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>

      <div class="sidebar-footer">
        <div class="user-info" v-show="!isCollapsed">
          <div class="user-avatar">
            <el-avatar :size="40" :src="userAvatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="user-details">
            <div class="user-name">{{ username }}</div>
            <div class="user-role">{{ userRole }}</div>
          </div>
        </div>

        <div class="quick-actions">
          <el-tooltip content="设置" placement="right" :disabled="!isCollapsed">
            <el-button class="action-btn" text @click="handleSettings">
              <el-icon><Setting /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip v-if="!isCollapsed" content="通知" placement="right" :disabled="!isCollapsed">
            <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
              <el-button class="action-btn" text @click="handleNotification">
                <el-icon><Bell /></el-icon>
              </el-button>
            </el-badge>
          </el-tooltip>
          <el-tooltip v-if="!isCollapsed" content="退出登录" placement="right" :disabled="!isCollapsed">
            <el-button class="action-btn" text @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>

    <div class="sidebar-glow"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, defineEmits } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  Management,
  Document,
  Files,
  Monitor,
  DocumentChecked,
  DataAnalysis,
  Box,
  HomeFilled,
  User,
  Setting,
  Bell,
  SwitchButton,
  Lock,
  Unlock,
  Refresh
} from '@element-plus/icons-vue'

const emit = defineEmits(['update:collapsed'])

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(true)
const isFixed = ref(false)
const username = ref('管理员')
const userRole = ref('超级管理员')
const userAvatar = ref('')
const notificationCount = ref(3)

// 菜单项配置
const menuItems = computed(() => [
  {
    index: 'home',
    icon: HomeFilled,
    title: '工作台',
    path: '/',
    roles: [] // 所有用户都可以访问
  },
  {
    index: 'project',
    icon: Management,
    title: '项目管理',
    path: '/project',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  },
  {
    index: 'task',
    icon: Document,
    title: '任务管理',
    path: '/task',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  },
  {
    index: 'api-info',
    icon: Files,
    title: '接口中心',
    path: '/api-info',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  },
  {
    index: 'test',
    icon: Box,
    title: '接口测试',
    path: '/test',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  },
  {
    index: 'ai-review',
    icon: DocumentChecked,
    title: 'AI代码评审',
    path: '/ai-review',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  },
  
  {
    index: 'monitoring',
    icon: Monitor,
    title: '系统监控',
    path: '/monitor',
    roles: ['ADMIN', 'SUPER_ADMIN'] // 仅管理员可访问
  },
  {
    index: 'data-center',
    icon: DataAnalysis,
    title: '数据中心',
    path: '/data-center',
    roles: ['ADMIN', 'SUPER_ADMIN'] // 仅管理员可访问
  },
  {
    index: 'deploy',
    icon: Box,
    title: '部署记录',
    path: '/deploy',
    roles: ['ADMIN', 'SUPER_ADMIN'] // 仅管理员可访问
  },
  {
    index: 'sync',
    icon: Refresh,
    title: '数据同步',
    path: '/sync',
    roles: ['USER', 'ADMIN', 'SUPER_ADMIN']
  }
])

// 取消鉴权，所有用户都能看到所有菜单
const accessibleMenus = computed(() => {
  return menuItems.value
})

const activeMenu = computed(() => {
  const path = route.path
  const activeItem = menuItems.value.find(item => item.path === path)
  return activeItem ? activeItem.index : 'home'
})

const handleMouseEnter = () => {
  if (!isFixed.value) {
    isCollapsed.value = false
    emit('update:collapsed', false)
  }
}

const handleMouseLeave = () => {
  if (!isFixed.value) {
    setTimeout(() => {
      isCollapsed.value = true
      emit('update:collapsed', true)
    }, 200)
  }
}

const toggleFixed = () => {
  isFixed.value = !isFixed.value
  if (!isFixed.value) {
    setTimeout(() => {
      isCollapsed.value = true
      emit('update:collapsed', true)
    }, 300)
  }
}

const handleMenuSelect = (index) => {
  const menuItem = menuItems.value.find(item => item.index === index)
  if (menuItem) {
    router.push(menuItem.path)
  }
}

const handleSettings = () => {
  router.push('/setting')
}

const handleNotification = () => {
  router.push('/notification')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(() => {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  const savedFixed = localStorage.getItem('sidebarFixed')
  
  if (savedCollapsed !== null) {
    isCollapsed.value = savedCollapsed === 'true'
    emit('update:collapsed', isCollapsed.value)
  }
  if (savedFixed !== null) {
    isFixed.value = savedFixed === 'true'
  }
})

watch(isCollapsed, (newValue) => {
  localStorage.setItem('sidebarCollapsed', newValue.toString())
  emit('update:collapsed', newValue)
})

watch(isFixed, (newValue) => {
  localStorage.setItem('sidebarFixed', newValue.toString())
})
</script>

<style scoped lang="scss">
.smart-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  width: 240px;
  z-index: 1000;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.is-collapsed {
    width: 64px;
  }

  .sidebar-content {
    height: 100%;
    background: rgba(26, 27, 30, 0.95);
    backdrop-filter: blur(16px);
    border-right: 1px solid rgba(255, 215, 0, 0.2);
    display: flex;
    flex-direction: column;
    position: relative;
    z-index: 2;
  }

  .sidebar-header {
    padding: 16px;
    border-bottom: 1px solid rgba(255, 215, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 64px;

    .logo-container {
      display: flex;
      align-items: center;
      gap: 12px;
      overflow: hidden;

      .logo-icon {
        color: #FFD700;
        flex-shrink: 0;
        filter: drop-shadow(0 0 8px rgba(255, 215, 0, 0.5));
      }

      .logo-text {
        font-size: 16px;
        font-weight: bold;
        background: linear-gradient(135deg, #FFD700 0%, #FFED4E 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        white-space: nowrap;
      }
    }

    .collapse-btn {
      color: #8E8E93;
      padding: 4px;
      
      &:hover {
        color: #FFD700;
      }
    }
  }

  .sidebar-menu {
    flex: 1;
    overflow-x: hidden;

    &:deep(.el-scrollbar__wrap) {
      overflow-x: hidden;
    }

    &:deep(.el-scrollbar__bar) {
      opacity: 0;
      
      &:hover {
        opacity: 1;
      }
    }

    &:deep(.el-scrollbar__thumb) {
      background: rgba(255, 215, 0, 0.3);
      border-radius: 4px;
    }
  }

  .sidebar-el-menu {
    border: none;
    background: transparent;
    padding: 8px 0;

    &:deep(.el-menu-item) {
      color: #8E8E93;
      height: 48px;
      line-height: 48px;
      margin: 4px 12px;
      border-radius: 8px;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(255, 215, 0, 0.1);
        color: #FFD700;
      }

      &.is-active {
        background: linear-gradient(135deg, rgba(255, 215, 0, 0.2) 0%, rgba(255, 215, 0, 0.1) 100%);
        color: #FFD700;
        border: 1px solid rgba(255, 215, 0, 0.3);
        box-shadow: 0 0 12px rgba(255, 215, 0, 0.2);
      }

      .el-menu-tooltip__trigger {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
      }

      .el-icon {
        font-size: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .sidebar-footer {
    padding: 16px;
    border-top: 1px solid rgba(255, 215, 0, 0.1);

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      padding: 8px;
      background: rgba(44, 45, 49, 0.6);
      border-radius: 8px;
      border: 1px solid rgba(255, 215, 0, 0.1);

      .user-avatar {
        flex-shrink: 0;

        .el-avatar {
          background: linear-gradient(135deg, #FFD700 0%, #B38B2D 100%);
          border: 2px solid rgba(255, 215, 0, 0.3);
        }
      }

      .user-details {
        flex: 1;
        overflow: hidden;

        .user-name {
          font-size: 14px;
          font-weight: 500;
          color: #E5E5E5;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .user-role {
          font-size: 12px;
          color: #8E8E93;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }

    .quick-actions {
      display: flex;
      justify-content: space-around;
      padding: 4px 0;
      width: 100%;
      box-sizing: border-box;

      .action-btn {
        color: #8E8E93;
        padding: 8px;
        border-radius: 8px;
        transition: all 0.2s ease;
        min-width: 36px;
        max-width: 48px;
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover {
          color: #FFD700;
          background: rgba(255, 215, 0, 0.1);
        }

        .el-icon {
          font-size: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }

      .notification-badge {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;

        &:deep(.el-badge__content) {
          background: #FF3B30;
          border: 1px solid rgba(255, 215, 0, 0.3);
        }
      }
    }
  }

  .sidebar-glow {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(180deg, rgba(255, 215, 0, 0.05) 0%, transparent 50%, rgba(255, 215, 0, 0.05) 100%);
    pointer-events: none;
    z-index: 1;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .smart-sidebar {
    width: 64px;

    &.is-collapsed {
      width: 64px;
    }

    .sidebar-header {
      padding: 12px;
      justify-content: center;

      .logo-container {
        gap: 0;

        .logo-text {
          display: none;
        }
      }

      .collapse-btn {
        display: none;
      }
    }

    .sidebar-footer {
      .user-info {
        display: none;
      }

      .quick-actions {
        .action-btn {
          padding: 6px;

          .el-icon {
            font-size: 18px;
          }
        }
      }
    }
  }
}
</style>