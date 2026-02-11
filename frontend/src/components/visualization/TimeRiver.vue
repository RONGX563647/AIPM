<template>
  <div class="time-river-container" ref="containerRef">
    <canvas ref="canvasRef" class="time-river-canvas"></canvas>
    <div class="time-river-overlay">
      <div class="river-info">
        <h3 class="river-title">时间河流</h3>
        <p class="river-subtitle">项目进度可视化</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  tasks: {
    type: Array,
    default: () => []
  },
  width: {
    type: Number,
    default: 800
  },
  height: {
    type: Number,
    default: 600
  }
})

const containerRef = ref(null)
const canvasRef = ref(null)
let animationId = null
let particles = []
let riverPath = []

// 粒子类
class Particle {
  constructor(x, y, progress = 0) {
    this.x = x
    this.y = y
    this.progress = progress
    this.size = Math.random() * 3 + 1
    this.speed = Math.random() * 2 + 1
    this.alpha = Math.random() * 0.8 + 0.2
    this.color = `hsl(${Math.random() * 60 + 40}, 80%, 60%)` // 金色系
  }

  update() {
    this.progress += 0.005
    if (this.progress > 1) this.progress = 0
    
    // 沿着河流路径移动
    const index = Math.floor(this.progress * (riverPath.length - 1))
    const point = riverPath[index]
    if (point) {
      this.x = point.x + (Math.sin(Date.now() * 0.001 + this.progress * 10) * 5)
      this.y = point.y + (Math.cos(Date.now() * 0.001 + this.progress * 15) * 3)
    }
    
    // 随机摆动
    this.x += Math.sin(Date.now() * 0.002 + this.progress * 20) * 0.5
    this.y += Math.cos(Date.now() * 0.003 + this.progress * 25) * 0.3
  }

  draw(ctx) {
    ctx.save()
    ctx.globalAlpha = this.alpha
    ctx.fillStyle = this.color
    
    // 发光效果
    ctx.shadowBlur = 10
    ctx.shadowColor = this.color
    
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fill()
    ctx.restore()
  }
}

// 生成河流路径
function generateRiverPath(width, height) {
  const points = []
  const segments = 100
  
  for (let i = 0; i <= segments; i++) {
    const t = i / segments
    const x = width * t
    // 创建波浪形河流
    const y = height * 0.5 + Math.sin(t * Math.PI * 3) * (height * 0.3)
    points.push({ x, y })
  }
  
  return points
}

// 绘制河流
function drawRiver(ctx, width, height) {
  // 绘制主河流
  ctx.strokeStyle = '#FFD700'
  ctx.lineWidth = 3
  ctx.shadowBlur = 15
  ctx.shadowColor = '#FFD700'
  
  ctx.beginPath()
  riverPath.forEach((point, index) => {
    if (index === 0) {
      ctx.moveTo(point.x, point.y)
    } else {
      ctx.lineTo(point.x, point.y)
    }
  })
  ctx.stroke()
  
  // 绘制河流发光效果
  ctx.strokeStyle = 'rgba(255, 215, 0, 0.3)'
  ctx.lineWidth = 8
  ctx.shadowBlur = 25
  ctx.beginPath()
  riverPath.forEach((point, index) => {
    if (index === 0) {
      ctx.moveTo(point.x, point.y)
    } else {
      ctx.lineTo(point.x, point.y)
    }
  })
  ctx.stroke()
  
  // 绘制河流两岸
  ctx.strokeStyle = 'rgba(255, 215, 0, 0.1)'
  ctx.lineWidth = 15
  ctx.shadowBlur = 0
  ctx.beginPath()
  riverPath.forEach((point, index) => {
    if (index === 0) {
      ctx.moveTo(point.x, point.y)
    } else {
      ctx.lineTo(point.x, point.y)
    }
  })
  ctx.stroke()
}

// 绘制任务节点
function drawTaskNodes(ctx, tasks) {
  tasks.forEach((task, index) => {
    const progress = task.progress || 0
    const pointIndex = Math.floor(progress * (riverPath.length - 1))
    const point = riverPath[pointIndex]
    
    if (point) {
      // 任务节点背景
      ctx.fillStyle = 'rgba(26, 27, 30, 0.8)'
      ctx.strokeStyle = '#FFD700'
      ctx.lineWidth = 2
      ctx.shadowBlur = 10
      ctx.shadowColor = '#FFD700'
      
      ctx.beginPath()
      ctx.arc(point.x, point.y, 15, 0, Math.PI * 2)
      ctx.fill()
      ctx.stroke()
      
      // 进度指示器
      ctx.beginPath()
      ctx.arc(point.x, point.y, 12, -Math.PI / 2, -Math.PI / 2 + (Math.PI * 2 * progress))
      ctx.strokeStyle = '#FFED4E'
      ctx.lineWidth = 3
      ctx.stroke()
      
      // 任务图标
      ctx.fillStyle = '#FFD700'
      ctx.font = '12px Arial'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillText('●', point.x, point.y)
    }
  })
}

// 动画循环
function animate() {
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  
  if (!ctx) return
  
  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 绘制背景渐变
  const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height)
  gradient.addColorStop(0, 'rgba(17, 17, 19, 0.8)')
  gradient.addColorStop(1, 'rgba(26, 27, 30, 0.9)')
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 绘制河流
  drawRiver(ctx, canvas.width, canvas.height)
  
  // 更新和绘制粒子
  particles.forEach(particle => {
    particle.update()
    particle.draw(ctx)
  })
  
  // 绘制任务节点
  drawTaskNodes(ctx, props.tasks)
  
  animationId = requestAnimationFrame(animate)
}

// 初始化
function init() {
  const canvas = canvasRef.value
  const container = containerRef.value
  
  if (!canvas || !container) return
  
  // 设置画布尺寸
  canvas.width = props.width
  canvas.height = props.height
  
  // 生成河流路径
  riverPath = generateRiverPath(canvas.width, canvas.height)
  
  // 创建粒子
  for (let i = 0; i < 50; i++) {
    const progress = Math.random()
    const pointIndex = Math.floor(progress * (riverPath.length - 1))
    const point = riverPath[pointIndex]
    if (point) {
      particles.push(new Particle(point.x, point.y, progress))
    }
  }
  
  // 开始动画
  animate()
}

// 响应式处理
function handleResize() {
  if (!containerRef.value) return
  
  const rect = containerRef.value.getBoundingClientRect()
  const canvas = canvasRef.value
  
  if (canvas) {
    canvas.width = rect.width
    canvas.height = rect.height
    riverPath = generateRiverPath(canvas.width, canvas.height)
  }
}

onMounted(() => {
  init()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', handleResize)
})

// 监听任务变化
watch(() => props.tasks, () => {
  // 任务数据更新时的处理
}, { deep: true })
</script>

<style scoped lang="scss">
.time-river-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-black) 0%, var(--color-secondary-black) 100%);
}

.time-river-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.time-river-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  background: radial-gradient(
    circle at 50% 50%,
    transparent 0%,
    rgba(17, 17, 19, 0.3) 70%,
    rgba(26, 27, 30, 0.6) 100%
  );
}

.river-info {
  position: absolute;
  top: var(--spacing-xl);
  left: var(--spacing-xl);
  color: var(--color-gold-primary);
}

.river-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xs) 0;
  text-shadow: 0 0 10px var(--color-gold-primary);
}

.river-subtitle {
  font-size: var(--font-size-sm);
  margin: 0;
  opacity: 0.8;
}
</style>