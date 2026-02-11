import type { RouteRecordRaw } from 'vue-router'

/**
 * 路由配置
 */
export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/forgot-password/index.vue'),
    meta: {
      title: '找回密码',
      requiresAuth: false
    }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: {
      title: '工作台',
      requiresAuth: false
    }
  },
  {
    path: '/project',
    name: 'Project',
    component: () => import('@/views/project/index.vue'),
    meta: {
      title: '项目管理',
      requiresAuth: false
    }
  },
  {
    path: '/task',
    name: 'Task',
    component: () => import('@/views/task/index.vue'),
    meta: {
      title: '任务管理',
      requiresAuth: false
    }
  },
  {
    path: '/api-info',
    name: 'ApiInfo',
    component: () => import('@/views/api-info/index.vue'),
    meta: {
      title: '接口中心',
      requiresAuth: false
    }
  },
  {
    path: '/ai-review',
    name: 'AiReview',
    component: () => import('@/views/ai-review/index.vue'),
    meta: {
      title: 'AI代码评审',
      requiresAuth: false
    }
  },
  {
    path: '/monitoring',
    name: 'Monitoring',
    component: () => import('@/views/monitoring/index.vue'),
    meta: {
      title: '系统监控',
      requiresAuth: false
    }
  },
  {
    path: '/monitor',
    name: 'Monitor',
    component: () => import('@/views/monitor/index.vue'),
    meta: {
      title: '系统监控',
      requiresAuth: false
    }
  },
  {
    path: '/data-center',
    name: 'DataCenter',
    component: () => import('@/views/data-center/index.vue'),
    meta: {
      title: '数据中心',
      requiresAuth: false
    }
  },
  {
    path: '/deploy',
    name: 'Deploy',
    component: () => import('@/views/deploy/index.vue'),
    meta: {
      title: '部署记录',
      requiresAuth: false
    }
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/test/index.vue'),
    meta: {
      title: '自动化接口测试',
      requiresAuth: false
    }
  },
  {
    path: '/visualization-demo',
    name: 'VisualizationDemo',
    component: () => import('@/views/visualization-demo.vue'),
    meta: {
      title: '可视化组件演示',
      requiresAuth: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/'
  }
]