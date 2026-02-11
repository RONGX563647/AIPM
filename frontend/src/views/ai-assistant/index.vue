<template>
  <div class="ai-assistant-page">
    <div class="page-header">
      <h1>AI 助手</h1>
      <p>智能助手，随时为您提供帮助</p>
    </div>
    
    <div class="page-content">
      <AIAssistant />
    </div>
  </div>
</template>

<script setup>
import AIAssistant from '@/components/ai/AIAssistant.vue'
</script>

<style scoped lang="scss">
.ai-assistant-page {
  padding: 24px;
  min-height: 100%;
  background: transparent;
  
  .page-header {
    margin-bottom: 24px;
    
    h1 {
      color: #ffd700;
      font-size: 24px;
      margin: 0 0 8px 0;
    }
    
    p {
      color: #8e8e93;
      margin: 0;
    }
  }
  
  .page-content {
    background: rgba(26, 27, 30, 0.6);
    border-radius: 8px;
    padding: 16px;
    height: calc(100vh - 150px);
    overflow: hidden;
  }
}
</style>