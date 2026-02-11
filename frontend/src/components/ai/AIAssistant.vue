<template>
  <div class="ai-assistant-container">
    <!-- 只显示AISidebar组件，因为现在AISidebar已经是一个完整的独立聊天组件 -->
    <AISidebar :initial-expanded="true" />
  </div>
</template>

<script setup>
import AISidebar from './AISidebar.vue'
</script>

<style scoped lang="scss">
.ai-assistant-container {
  height: 100%;
  
  .ai-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #2c2d31;
    
    h3 {
      margin: 0;
      color: #ffd700;
      font-size: 18px;
    }
  }
  
  .ai-tabs {
    height: calc(100% - 60px); // 减去头部高度
    
    :deep(.tab-container) {
      height: 100%;
      
      .el-tabs__content {
        height: calc(100% - 40px); // 减去标签头的高度
        
        .el-tab-pane {
          height: 100%;
          overflow: auto;
        }
      }
    }
  }
}

// 主题样式
:global(.dark-theme) {
  .ai-assistant-container {
    .ai-header {
      background: #1a1b1e;
      border-bottom: 1px solid #2c2d31;
      
      h3 {
        color: #ffd700;
      }
    }
  }
}

:global(.light-theme) {
  .ai-assistant-container {
    .ai-header {
      background: #ffffff;
      border-bottom: 1px solid #e0e0e0;
      
      h3 {
        color: #303133;
      }
    }
  }
}
</style>