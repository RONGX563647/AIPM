<template>
  <div class="ai-sidebar" v-if="isVisible" :class="{ visible: isVisible }">
    <div class="ai-sidebar-header">
      <h3 class="ai-title">AI 助手</h3>
      <div class="ai-controls">
        <el-button size="small" @click="clearHistory" icon="Delete" class="clear-button">清空</el-button>
        <el-button size="small" @click="closeSidebar" icon="Close" class="close-button">关闭</el-button>
      </div>
    </div>
    
    <div class="ai-content">
      <div class="ai-chat-container">
        <div class="ai-messages" ref="messagesContainerRef">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="ai-message"
            :class="{ 'ai-user-message': msg.role === 'user', 'ai-assistant-message': msg.role === 'assistant' }"
          >
            <div class="message-avatar">
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><ChatDotRound /></el-icon>
            </div>
            <div class="message-content">
              <div v-if="msg.content" class="message-text">{{ msg.content }}</div>
            </div>
          </div>
        </div>
        
        <div class="ai-input-area">
          <el-input
            v-model="inputMessage"
            :rows="3"
            type="textarea"
            placeholder="向AI助手提问..."
            :disabled="isLoading"
            @keyup.enter="sendInput($event)"
          />
          <el-button 
            type="primary" 
            :loading="isLoading" 
            @click="sendMessage"
            :disabled="!inputMessage.trim()"
            class="send-button"
          >
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { 
  ArrowLeft, 
  ArrowRight, 
  Delete, 
  User, 
  ChatDotRound, 
  Promotion 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { aiChatApi } from '@/api/ai-chat.api'

// Props
const props = defineProps({
  initialVisible: {
    type: Boolean,
    default: false
  }
})

// Reactive data
const isVisible = ref(props.initialVisible)
const inputMessage = ref('')
const messages = ref([])
const isLoading = ref(false)
const messagesContainerRef = ref(null)

// Computed properties
const getStatusType = (status) => {
  switch (status) {
    case 'sending': return 'info'
    case 'sent': return 'success'
    case 'failed': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'sending': return '发送中...'
    case 'sent': return '已发送'
    case 'failed': return '发送失败'
    default: return ''
  }
}



const clearHistory = () => {
  messages.value = []
  localStorage.removeItem('aiChatHistory')
}

const closeSidebar = () => {
  isVisible.value = false
  // 发送事件通知悬浮球更新状态
  window.dispatchEvent(new CustomEvent('ai-sidebar-close'));
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: inputMessage.value,
    timestamp: Date.now(),
    status: 'sent'
  }
  messages.value.push(userMessage)
  const userInput = inputMessage.value
  inputMessage.value = ''

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 调用真实AI服务
  isLoading.value = true
  try {
    const response = await aiChatApi.sendMessage(userInput)
    
    if (response.code === 0 && response.data) {
      messages.value.push({
        role: 'assistant',
        content: response.data,
        timestamp: Date.now(),
        status: 'sent'
      })
    } else {
      messages.value.push({
        role: 'assistant',
        content: response.message || '抱歉，AI助手暂时无法响应，请稍后再试。',
        timestamp: Date.now(),
        status: 'failed'
      })
    }
    
    await nextTick()
    scrollToBottom()
    isLoading.value = false
    
    // 保存聊天历史
    saveChatHistory()
  } catch (error) {
    console.error('AI响应错误:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI助手暂时无法响应，请稍后再试。',
      timestamp: Date.now(),
      status: 'failed'
    })
    isLoading.value = false
  }
}

const sendInput = (event) => {
  if (event.shiftKey) return // 如果按下Shift键则不发送
  event.preventDefault()
  if (!event.ctrlKey) {  // Enter键发送
    sendMessage()
  }
}

const scrollToBottom = () => {
  if (messagesContainerRef.value) {
    messagesContainerRef.value.scrollTop = messagesContainerRef.value.scrollHeight
  }
}

const saveChatHistory = () => {
  localStorage.setItem('aiChatHistory', JSON.stringify(messages.value))
}

const loadChatHistory = () => {
  const saved = localStorage.getItem('aiChatHistory')
  if (saved) {
    try {
      messages.value = JSON.parse(saved)
    } catch (e) {
      console.error('加载聊天历史失败:', e)
    }
  }
}

// Lifecycle hooks
// 监听来自外部的关闭事件
const handleCloseEvent = () => {
  isVisible.value = false
}

// 监听来自外部的显示事件
const handleShowEvent = (event) => {
  if (event.detail.visible) {
    isVisible.value = true;
  }
};

// 点击外部区域关闭侧边栏
const handleClickOutside = (event) => {
  const sidebar = document.querySelector('.ai-sidebar');
  if (sidebar && !sidebar.contains(event.target)) {
    isVisible.value = false;
    // 发送事件通知悬浮球更新状态
    window.dispatchEvent(new CustomEvent('ai-sidebar-close'));
  }
};

onMounted(() => {
  loadChatHistory()
  scrollToBottom()
  
  // 监听显示和关闭事件
  window.addEventListener('ai-sidebar-toggle', handleShowEvent);
  window.addEventListener('ai-sidebar-close', handleCloseEvent);
  
  // 监听点击外部区域事件
  setTimeout(() => {
    document.addEventListener('click', handleClickOutside);
  }, 0);
})

// 在组件卸载时清理事件监听器
onUnmounted(() => {
  window.removeEventListener('ai-sidebar-toggle', handleShowEvent);
  window.removeEventListener('ai-sidebar-close', handleCloseEvent);
  document.removeEventListener('click', handleClickOutside);
})
</script>

<style scoped lang="scss">
.ai-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  height: 100vh;
  width: 400px;
  background: rgba(17, 17, 19, 0.2); /* 20%透明度 */
  border-left: 1px solid var(--color-secondary-black);
  z-index: 1000;
  overflow: hidden;
  box-shadow: -4px 0 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(10px); /* 添加模糊效果增强透明感 */
  
  &.visible {
    animation: slideIn 0.3s ease-out forwards;
  }
  
  @media (max-width: 768px) {
    width: 100%;
    z-index: 1001;
  }
  
  @keyframes slideIn {
    from {
      transform: translateX(100%);
    }
    to {
      transform: translateX(0);
    }
  }
  
  .ai-sidebar-header {
    display: flex;
    align-items: center;
    padding: 16px;
    background: var(--color-secondary-black);
    border-bottom: 1px solid var(--color-border);
    
    .ai-title {
      flex: 1;
      margin: 0 16px 0 12px;
      color: var(--color-gold-primary);
      font-size: 16px;
      font-weight: bold;
    }
    
    .ai-controls {
      display: flex;
      gap: 8px;
      
      .clear-button {
        background: linear-gradient(135deg, var(--color-gold-primary) 0%, var(--color-gold-dark) 100%);
        border: none;
        color: var(--color-primary-black);
        font-weight: bold;
        
        &:hover {
          background: linear-gradient(135deg, var(--color-gold-light) 0%, var(--color-gold-primary) 100%);
        }
      }
      
      .close-button {
        background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
        border: none;
        color: white;
        font-weight: bold;
        
        &:hover {
          background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
        }
      }
    }
  }
  
  .ai-content {
    height: calc(100% - 60px);
    display: flex;
    flex-direction: column;
    
    .ai-chat-container {
      display: flex;
      flex-direction: column;
      height: 100%;
      
      .ai-messages {
        flex: 1;
        overflow-y: auto;
        padding: 16px;
        display: flex;
        flex-direction: column;
        gap: 16px;
        
        .ai-message {
          display: flex;
          gap: 12px;
          
          .message-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: var(--color-secondary-black);
            display: flex;
            align-items: center;
            justify-content: center;
            
            .el-icon {
              color: var(--color-gold-primary);
              font-size: 16px;
            }
          }
          
          .message-content {
            flex: 1;
            
            .message-text {
              background: var(--color-secondary-black);
              padding: 12px;
              border-radius: 8px;
              color: var(--color-ultra-light-gray);
              white-space: pre-wrap;
              line-height: 1.5;
            }
          }
          
          &.ai-user-message {
            .message-content .message-text {
              background: var(--color-secondary-black);
              border-left: 3px solid var(--color-gold-primary);
            }
          }
          
          &.ai-assistant-message {
            .message-content .message-text {
              background: var(--color-secondary-black);
              border-left: 3px solid var(--color-blue-primary);
            }
          }
        }
      }
      
      .ai-input-area {
        padding: 16px;
        border-top: 1px solid var(--color-secondary-black);
        background: var(--color-primary-black);
        display: flex;
        gap: 8px;
        
        .el-textarea {
          :deep(.el-textarea__inner) {
            background: var(--color-secondary-black);
            border: 1px solid var(--color-border);
            color: var(--color-ultra-light-gray);
            
            &:focus {
              border-color: var(--color-gold-primary);
            }
          }
        }
        
        .send-button {
          background: linear-gradient(135deg, var(--color-gold-primary) 0%, var(--color-gold-dark) 100%);
          border: none;
          color: var(--color-primary-black);
          font-weight: bold;
          
          &:hover {
            background: linear-gradient(135deg, var(--color-gold-light) 0%, var(--color-gold-primary) 100%);
          }
        }
      }
    }
  }
}
</style>