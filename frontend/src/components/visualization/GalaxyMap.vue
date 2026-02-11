<template>
  <div class="galaxy-map-container" ref="containerRef">
    <canvas ref="canvasRef" class="galaxy-map-canvas"></canvas>
    <div class="galaxy-overlay">
      <div class="galaxy-info">
        <h3 class="galaxy-title">宇宙星图</h3>
        <p class="galaxy-subtitle">项目关联网络</p>
      </div>
      <div class="galaxy-stats" v-if="nodes.length > 0">
        <div class="stat-item">
          <span class="stat-value">{{ nodes.length }}</span>
          <span class="stat-label">节点</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ connections.length }}</span>
          <span class="stat-label">连接</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  nodes: {
    type: Array,
    default: () => []
  },
  connections: {
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
let stars = []
let galaxies = []

// 星体类
class Star {
  constructor(x, y, size = 1) {
    this.x = x
    this.y = y
    this.originalX = x
    this.originalY = y
    this.size = size
    this.brightness = Math.random() * 0.8 + 0.2
    this.pulseSpeed = Math.random() * 0.02 + 0.01
    this.angle = Math.random() * Math.PI * 2
    this.distance = Math.random() * 5 + 1
  }

  update() {
    // 脉冲效果
    this.brightness = 0.5 + Math.sin(Date.now() * this.pulseSpeed) * 0.3
    
    // 微小摆动
    this.x = this.originalX + Math.sin(this.angle + Date.now() * 0.001) * this.distance
    this.y = this.originalY + Math.cos(this.angle + Date.now() * 0.0015) * this.distance
  }

  draw(ctx) {
    const gradient = ctx.createRadialGradient(
      this.x, this.y, 0,
      this.x, this.y, this.size * 3
    )
    gradient.addColorStop(0, `rgba(255, 215, 0, ${this.brightness})`)
    gradient.addColorStop(0.5, `rgba(255, 215, 0, ${this.brightness * 0.5})`)
    gradient.addColorStop(1, 'rgba(255, 215, 0, 0)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size * 3, 0, Math.PI * 2)
    ctx.fill()
    
    // 核心亮点
    ctx.fillStyle = `rgba(255, 255, 255, ${this.brightness})`
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fill()
  }
}

// 星系类
class Galaxy {
  constructor(x, y, radius) {
    this.x = x
    this.y = y
    this.radius = radius
    this.rotation = Math.random() * Math.PI * 2
    this.rotationSpeed = (Math.random() - 0.5) * 0.002
    this.arms = 2 + Math.floor(Math.random() * 3)
    this.stars = []
    this.createStars()
  }

  createStars() {
    const starCount = Math.floor(this.radius * 0.5)
    for (let i = 0; i < starCount; i++) {
      const angle = (Math.PI * 2 * i) / starCount
      const distance = Math.random() * this.radius * 0.8
      const x = this.x + Math.cos(angle) * distance
      const y = this.y + Math.sin(angle) * distance
      const size = Math.random() * 1.5 + 0.5
      this.stars.push(new Star(x, y, size))
    }
  }

  update() {
    this.rotation += this.rotationSpeed
    this.stars.forEach(star => {
      star.update()
    })
  }

  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)
    ctx.translate(-this.x, -this.y)
    
    // 绘制星系臂
    ctx.strokeStyle = 'rgba(255, 215, 0, 0.3)'
    ctx.lineWidth = 2
    ctx.shadowBlur = 15
    ctx.shadowColor = '#FFD700'
    
    for (let arm = 0; arm < this.arms; arm++) {
      const armAngle = (Math.PI * 2 * arm) / this.arms
      ctx.beginPath()
      
      for (let i = 0; i <= 50; i++) {
        const t = i / 50
        const angle = armAngle + t * Math.PI * 1.5
        const distance = t * this.radius * 0.8
        const x = this.x + Math.cos(angle) * distance
        const y = this.y + Math.sin(angle) * distance
        
        if (i === 0) {
          ctx.moveTo(x, y)
        } else {
          ctx.lineTo(x, y)
        }
      }
      ctx.stroke()
    }
    
    // 绘制恒星
    this.stars.forEach(star => {
      star.draw(ctx)
    })
    
    ctx.restore()
  }
}

// 绘制连接线
function drawConnections(ctx, nodes, connections) {
  connections.forEach(conn => {
    const sourceNode = nodes.find(n => n.id === conn.source)
    const targetNode = nodes.find(n => n.id === conn.target)
    
    if (sourceNode && targetNode) {
      const gradient = ctx.createLinearGradient(
        sourceNode.x, sourceNode.y,
        targetNode.x, targetNode.y
      )
      gradient.addColorStop(0, 'rgba(255, 215, 0, 0.6)')
      gradient.addColorStop(1, 'rgba(255, 237, 78, 0.3)')
      
      ctx.strokeStyle = gradient
      ctx.lineWidth = 1
      ctx.shadowBlur = 10
      ctx.shadowColor = '#FFD700'
      
      ctx.beginPath()
      ctx.moveTo(sourceNode.x, sourceNode.y)
      ctx.lineTo(targetNode.x, targetNode.y)
      ctx.stroke()
    }
  })
}

// 绘制节点
function drawNodes(ctx, nodes) {
  nodes.forEach(node => {
    // 节点光晕
    const gradient = ctx.createRadialGradient(
      node.x, node.y, 0,
      node.x, node.y, 20
    )
    gradient.addColorStop(0, 'rgba(255, 215, 0, 0.8)')
    gradient.addColorStop(0.7, 'rgba(255, 215, 0, 0.3)')
    gradient.addColorStop(1, 'rgba(255, 215, 0, 0)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(node.x, node.y, 20, 0, Math.PI * 2)
    ctx.fill()
    
    // 节点核心
    ctx.fillStyle = '#FFED4E'
    ctx.beginPath()
    ctx.arc(node.x, node.y, 8, 0, Math.PI * 2)
    ctx.fill()
    
    // 节点边框
    ctx.strokeStyle = '#FFD700'
    ctx.lineWidth = 2
    ctx.beginPath()
    ctx.arc(node.x, node.y, 10, 0, Math.PI * 2)
    ctx.stroke()
    
    // 节点标签
    if (node.name) {
      ctx.fillStyle = '#FFD700'
      ctx.font = '12px Arial'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillText(node.name.substring(0, 8), node.x, node.y + 25)
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
  
  // 绘制深空背景
  const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height)
  gradient.addColorStop(0, '#0A0A0B')
  gradient.addColorStop(0.5, '#111113')
  gradient.addColorStop(1, '#1A1B1E')
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 绘制背景星星
  stars.forEach(star => {
    star.draw(ctx)
  })
  
  // 绘制星系
  galaxies.forEach(galaxy => {
    galaxy.update()
    galaxy.draw(ctx)
  })
  
  // 绘制连接线
  drawConnections(ctx, props.nodes, props.connections)
  
  // 绘制节点
  drawNodes(ctx, props.nodes)
  
  animationId = requestAnimationFrame(animate)
}

// 初始化背景星星
function initBackgroundStars(width, height) {
  stars = []
  const starCount = Math.floor((width * height) / 1000)
  
  for (let i = 0; i < starCount; i++) {
    const x = Math.random() * width
    const y = Math.random() * height
    const size = Math.random() * 2 + 0.5
    stars.push(new Star(x, y, size))
  }
}

// 初始化星系
function initGalaxies(width, height) {
  galaxies = []
  const galaxyCount = 3 + Math.floor(Math.random() * 3)
  
  for (let i = 0; i < galaxyCount; i++) {
    const x = Math.random() * width
    const y = Math.random() * height
    const radius = 50 + Math.random() * 100
    galaxies.push(new Galaxy(x, y, radius))
  }
}

// 初始化
function init() {
  const canvas = canvasRef.value
  const container = containerRef.value
  
  if (!canvas || !container) return
  
  // 设置画布尺寸
  canvas.width = props.width
  canvas.height = props.height
  
  // 初始化背景元素
  initBackgroundStars(canvas.width, canvas.height)
  initGalaxies(canvas.width, canvas.height)
  
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
    initBackgroundStars(canvas.width, canvas.height)
    initGalaxies(canvas.width, canvas.height)
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

// 监听数据变化
watch([() => props.nodes, () => props.connections], () => {
  // 数据更新时的处理
}, { deep: true })
</script>

<style scoped lang="scss">
.galaxy-map-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #0A0A0B 0%, #111113 100%);
}

.galaxy-map-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.galaxy-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  background: radial-gradient(
    circle at 30% 30%,
    transparent 0%,
    rgba(10, 10, 11, 0.6) 100%
  );
}

.galaxy-info {
  position: absolute;
  top: var(--spacing-xl);
  left: var(--spacing-xl);
  color: var(--color-gold-primary);
}

.galaxy-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xs) 0;
  text-shadow: 0 0 10px var(--color-gold-primary);
}

.galaxy-subtitle {
  font-size: var(--font-size-sm);
  margin: 0;
  opacity: 0.8;
}

.galaxy-stats {
  position: absolute;
  bottom: var(--spacing-xl);
  right: var(--spacing-xl);
  display: flex;
  gap: var(--spacing-lg);
  color: var(--color-gold-primary);
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  text-shadow: 0 0 8px var(--color-gold-primary);
}

.stat-label {
  font-size: var(--font-size-xs);
  opacity: 0.7;
}
</style>