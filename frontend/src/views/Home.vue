<template>
  <div class="home-container">
    <!-- 背景可视化 -->
    <div class="home-background">
      <VisualizationController 
        :default-visualization="'code-universe'"
        :show-controls="false"
        :auto-switch="true"
        :switch-interval="12000"
        class="background-visualization"
      />
    </div>
    
    <div class="home-content">
      <div class="welcome-section">
        <h1 class="welcome-title">欢迎回来，{{ username }}</h1>
        <p class="welcome-subtitle">AI全自动研发大脑 - 智能开发平台</p>
      </div>
      
      <div class="home-card">
        <h2 class="card-title">工作台</h2>
        <p class="card-description">智能开发平台的核心功能</p>
        
        <div class="feature-grid">
          <div class="feature-item" @click="navigateToProject">
            <el-icon class="feature-icon"><Management /></el-icon>
            <h3 class="feature-title">项目管理</h3>
            <p class="feature-description">管理您的项目和任务</p>
          </div>
          <div class="feature-item" @click="navigateToTask">
            <el-icon class="feature-icon"><Document /></el-icon>
            <h3 class="feature-title">任务管理</h3>
            <p class="feature-description">管理项目任务和进度</p>
          </div>
          <div class="feature-item" @click="navigateToApiInfo">
            <el-icon class="feature-icon"><Files /></el-icon>
            <h3 class="feature-title">接口中心</h3>
            <p class="feature-description">管理和调试API接口</p>
          </div>
          <div class="feature-item" @click="navigateToAiReview">
            <el-icon class="feature-icon"><DocumentChecked /></el-icon>
            <h3 class="feature-title">AI代码评审</h3>
            <p class="feature-description">AI智能评审代码质量和安全性</p>
          </div>
          <div class="feature-item" @click="navigateToMonitoring">
            <el-icon class="feature-icon"><Monitor /></el-icon>
            <h3 class="feature-title">系统监控</h3>
            <p class="feature-description">实时监控系统状态</p>
          </div>
          <div class="feature-item" @click="navigateToDataCenter">
            <el-icon class="feature-icon"><DataAnalysis /></el-icon>
            <h3 class="feature-title">数据中心</h3>
            <p class="feature-description">管理和分析系统数据</p>
          </div>
          <div class="feature-item" @click="navigateToDeploy">
            <el-icon class="feature-icon"><Box /></el-icon>
            <h3 class="feature-title">部署记录</h3>
            <p class="feature-description">管理项目部署历史</p>
          </div>
          <div class="feature-item" @click="navigateToTest">
            <el-icon class="feature-icon"><Check /></el-icon>
            <h3 class="feature-title">接口测试</h3>
            <p class="feature-description">自动化接口测试</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Management, Document, Files, Monitor, DocumentChecked, DataAnalysis, Box, Check } from '@element-plus/icons-vue'
import VisualizationController from '@/components/visualization/VisualizationController.vue'

const router = useRouter()
const username = ref('管理员')

const navigateToProject = () => {
  router.push('/project')
}

const navigateToTask = () => {
  router.push('/task')
}

const navigateToApiInfo = () => {
  router.push('/api-info')
}

const navigateToAiReview = () => {
  router.push('/ai-review')
}

const navigateToMonitoring = () => {
  router.push('/monitoring')
}

const navigateToDataCenter = () => {
  router.push('/data-center')
}

const navigateToDeploy = () => {
  router.push('/deploy')
}

const navigateToTest = () => {
  router.push('/test')
}

onMounted(() => {
  console.log('Home page mounted')
})
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1A1B1E 100%);
  color: #E5E5E5;
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.home-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0.1;
}

.background-visualization {
  width: 100%;
  height: 100%;
}

.home-content {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 32px;
  text-align: center;

  .welcome-title {
    font-size: 36px;
    font-weight: bold;
    background: linear-gradient(135deg, #FFD700 0%, #FFED4E 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 12px;
  }

  .welcome-subtitle {
    font-size: 16px;
    color: #8E8E93;
    margin: 0;
  }
}

.home-card {
  background: rgba(26, 27, 30, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 32px;
  border: 1px solid rgba(255, 215, 0, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.card-title {
  font-size: 24px;
  font-weight: bold;
  color: #FFD700;
  margin-bottom: 8px;
}

.card-description {
  font-size: 14px;
  color: #8E8E93;
  margin-bottom: 32px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.feature-item {
  background: rgba(44, 45, 49, 0.1);
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  border: 1px solid rgba(255, 215, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    border-color: rgba(255, 215, 0, 0.3);
    box-shadow: 0 8px 24px rgba(255, 215, 0, 0.1);
  }
}

.feature-icon {
  font-size: 48px;
  color: #FFD700;
  margin-bottom: 16px;
}

.feature-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #E5E5E5;
}

.feature-description {
  font-size: 14px;
  color: #8E8E93;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-container {
    padding: 16px;
  }
  
  .home-content {
    padding: 0;
  }
  
  .home-card {
    padding: 24px;
  }
  
  .welcome-section {
    .welcome-title {
      font-size: 28px;
    }

    .welcome-subtitle {
      font-size: 14px;
    }
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
}
</style>