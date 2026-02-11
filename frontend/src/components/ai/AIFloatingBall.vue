<template>
  <div 
    v-if="!props.hidden"
    class="ai-floating-ball" 
    @mousedown="startDrag"
    @touchstart="startDrag"
    :style="{ left: position.x + 'px', top: position.y + 'px' }"
  >
    <el-badge :is-dot="hasUnreadMessages" class="badge">
      <el-icon :size="20" color="var(--color-gold-primary)">
        <ChatLineRound />
      </el-icon>
    </el-badge>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ChatLineRound } from '@element-plus/icons-vue'

const props = defineProps({
  hidden: {
    type: Boolean,
    default: false
  }
});

// 响应式数据
const hasUnreadMessages = ref(false)

// 拖拽相关状态
const isDragging = ref(false)
const dragStartPos = ref({ x: 0, y: 0 })
const position = ref({ x: 20, y: 20 }) // 默认位置
const startPos = ref({ x: 0, y: 0 }) // 记录拖拽开始时的位置
const hasMoved = ref(false) // 标记是否发生了移动

// 方法
const toggleSidebar = () => {
  // 通知全局状态AI侧边栏的显示状态已改变
  window.dispatchEvent(new CustomEvent('ai-sidebar-toggle', {
    detail: { visible: true }
  }));
  
  hasUnreadMessages.value = false
}

// 开始拖拽
const startDrag = (e) => {
  e.preventDefault(); // 防止拖拽时选中文本
  isDragging.value = true;
  hasMoved.value = false;
  
  const clientX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX;
  const clientY = e.type === 'touchstart' ? e.touches[0].clientY : e.clientY;
  
  // 记录鼠标/触摸点相对于悬浮球左上角的偏移
  dragStartPos.value = {
    x: clientX - position.value.x,
    y: clientY - position.value.y
  };
  
  // 记录开始位置
  startPos.value = {
    x: clientX,
    y: clientY
  };
  
  // 添加事件监听器
  document.addEventListener('mousemove', handleDrag);
  document.addEventListener('touchmove', handleDrag, { passive: false });
  document.addEventListener('mouseup', endDrag);
  document.addEventListener('touchend', endDrag);
}

// 拖拽过程
const handleDrag = (e) => {
  if (!isDragging.value) return;
  
  e.preventDefault();
  
  const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX;
  const clientY = e.type === 'touchmove' ? e.touches[0].clientY : e.clientY;
  
  // 检查是否发生了移动
  const moveDistance = Math.sqrt(
    Math.pow(clientX - startPos.value.x, 2) + 
    Math.pow(clientY - startPos.value.y, 2)
  );
  
  if (moveDistance > 5) { // 移动超过5像素才认为是拖拽
    hasMoved.value = true;
    
    // 计算新位置
    let newX = clientX - dragStartPos.value.x;
    let newY = clientY - dragStartPos.value.y;
    
    // 获取窗口尺寸
    const windowWidth = window.innerWidth;
    const windowHeight = window.innerHeight;
    const ballSize = 60; // 悬浮球的大小
    
    // 边界检查
    newX = Math.max(0, Math.min(windowWidth - ballSize, newX));
    newY = Math.max(0, Math.min(windowHeight - ballSize, newY));
    
    position.value = {
      x: newX,
      y: newY
    };
  }
}

// 结束拖拽
const endDrag = () => {
  if (!isDragging.value) return;
  
  isDragging.value = false;
  
  // 如果没有发生移动，则认为是点击，执行切换侧边栏操作
  if (!hasMoved.value) {
    toggleSidebar();
  }
  
  // 移除事件监听器
  document.removeEventListener('mousemove', handleDrag);
  document.removeEventListener('touchmove', handleDrag);
  document.removeEventListener('mouseup', endDrag);
  document.removeEventListener('touchend', endDrag);
  
  // 保存位置到本地存储
  localStorage.setItem('aiFloatingBallPosition', JSON.stringify(position.value));
}

// 监听侧边栏关闭事件
const handleSidebarClose = () => {
  // 悬浮球不需要处理侧边栏关闭事件
}

onMounted(() => {
  // 监听来自AISidebar的关闭事件
  window.addEventListener('ai-sidebar-close', handleSidebarClose)
  
  // 尝试从本地存储加载位置
  const savedPosition = localStorage.getItem('aiFloatingBallPosition');
  if (savedPosition) {
    try {
      const pos = JSON.parse(savedPosition);
      position.value = pos;
    } catch (e) {
      console.error('加载悬浮球位置失败:', e);
    }
  }
})

onUnmounted(() => {
  // 移除事件监听器
  document.removeEventListener('ai-sidebar-close', handleSidebarClose);
  
  // 移除拖拽事件监听器（以防万一）
  document.removeEventListener('mousemove', handleDrag);
  document.removeEventListener('touchmove', handleDrag);
  document.removeEventListener('mouseup', endDrag);
  document.removeEventListener('touchend', endDrag);
})
</script>

<style scoped lang="scss">
.ai-floating-ball {
  position: fixed;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-gold-primary) 0%, var(--color-gold-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move;
  z-index: 2000;
  box-shadow: 0 4px 20px var(--color-gold-glass);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  
  &:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 25px var(--color-gold-primary-glow);
  }
  
  &:active {
    cursor: grabbing;
  }
  
  .badge {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .ai-floating-ball {
    width: 50px;
    height: 50px;
  }
}
</style>