<template>
  <div class="code-universe-container" ref="containerRef">
    <canvas ref="canvasRef" class="code-universe-canvas"></canvas>
    <div class="code-overlay">
      <div class="code-info">
        <h3 class="code-title">代码宇宙</h3>
        <p class="code-subtitle">文件依赖关系图</p>
      </div>
      <div class="code-stats" v-if="files.length > 0">
        <div class="stat-item">
          <span class="stat-value">{{ files.length }}</span>
          <span class="stat-label">文件</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ dependencies.length }}</span>
          <span class="stat-label">依赖</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  files: {
    type: Array,
    default: () => []
  },
  dependencies: {
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
let codeParticles = []
let fileNodes = []

// 代码粒子类
class CodeParticle {
  constructor(x, y, char = '0') {
    this.x = x
    this.y = y
    this.char = char
    this.originalX = x
    this.originalY = y
    this.vx = (Math.random() - 0.5) * 2
    this.vy = (Math.random() - 0.5) * 2
    this.life = 1
    this.decay = 0.01 + Math.random() * 0.02
    this.size = 12 + Math.random() * 8
    this.color = this.getRandomColor()
  }

  getRandomColor() {
    const colors = [
      '#FFD700', // 金色
      '#FFED4E', // 浅金色
      '#34C759', // 绿色
      '#007AFF', // 蓝色
      '#FF9500'  // 橙色
    ]
    return colors[Math.floor(Math.random() * colors.length)]
  }

  update() {
    this.x += this.vx
    this.y += this.vy
    this.life -= this.decay
    
    // 边界反弹
    if (this.x <= 0 || this.x >= this.canvasWidth) {
      this.vx *= -1
    }
    if (this.y <= 0 || this.y >= this.canvasHeight) {
      this.vy *= -1
    }
    
    return this.life > 0
  }

  draw(ctx) {
    ctx.save()
    ctx.globalAlpha = this.life
    ctx.fillStyle = this.color
    ctx.font = `${this.size}px 'Courier New', monospace`
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    
    // 发光效果
    ctx.shadowBlur = 10
    ctx.shadowColor = this.color
    
    ctx.fillText(this.char, this.x, this.y)
    ctx.restore()
  }
}

// 文件节点类
class FileNode {
  constructor(x, y, name, type = 'file') {
    this.x = x
    this.y = y
    this.name = name
    this.type = type
    this.radius = 20 + Math.random() * 10
    this.connections = []
    this.pulse = 0
    this.rotation = 0
    this.color = this.getTypeColor(type)
  }

  getTypeColor(type) {
    const colors = {
      'file': '#FFD700',
      'folder': '#B38B2D',
      'component': '#34C759',
      'service': '#007AFF',
      'util': '#FF9500'
    }
    return colors[type] || '#FFD700'
  }

  update() {
    this.pulse = Math.sin(Date.now() * 0.003) * 0.3 + 0.7
    this.rotation += 0.01
  }

  draw(ctx) {
    ctx.save()
    
    // 节点光晕
    const glowSize = this.radius * this.pulse * 1.5
    const gradient = ctx.createRadialGradient(
      this.x, this.y, 0,
      this.x, this.y, glowSize
    )
    gradient.addColorStop(0, `${this.color}60`)
    gradient.addColorStop(0.7, `${this.color}30`)
    gradient.addColorStop(1, `${this.color}00`)
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.arc(this.x, this.y, glowSize, 0, Math.PI * 2)
    ctx.fill()
    
    // 节点主体
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)
    
    if (this.type === 'folder') {
      // 文件夹形状
      ctx.fillStyle = this.color
      ctx.fillRect(-this.radius, -this.radius * 0.6, this.radius * 2, this.radius * 1.2)
      ctx.fillRect(-this.radius * 0.8, -this.radius, this.radius * 1.6, this.radius * 0.4)
    } else {
      // 圆形节点
      ctx.fillStyle = this.color
      ctx.beginPath()
      ctx.arc(0, 0, this.radius, 0, Math.PI * 2)
      ctx.fill()
    }
    
    // 节点边框
    ctx.strokeStyle = '#FFFFFF'
    ctx.lineWidth = 2
    ctx.shadowBlur = 10
    ctx.shadowColor = '#FFFFFF'
    
    if (this.type === 'folder') {
      ctx.strokeRect(-this.radius, -this.radius * 0.6, this.radius * 2, this.radius * 1.2)
    } else {
      ctx.beginPath()
      ctx.arc(0, 0, this.radius, 0, Math.PI * 2)
      ctx.stroke()
    }
    
    // 文件名
    ctx.restore()
    ctx.fillStyle = '#FFFFFF'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText(
      this.name.split('/').pop().substring(0, 12),
      this.x,
      this.y + this.radius + 15
    )
  }
}

// 依赖连接类
class DependencyLink {
  constructor(source, target, type = 'import') {
    this.source = source
    this.target = target
    this.type = type
    this.pulse = 0
    this.flowDirection = Math.random() > 0.5 ? 1 : -1
  }

  update() {
    this.pulse = (Math.sin(Date.now() * 0.005) + 1) / 2
  }

  draw(ctx) {
    const gradient = ctx.createLinearGradient(
      this.source.x, this.source.y,
      this.target.x, this.target.y
    )
    
    const alpha = 0.4 + this.pulse * 0.4
    gradient.addColorStop(0, `rgba(255, 215, 0, ${alpha})`)
    gradient.addColorStop(1, `rgba(255, 237, 78, ${alpha * 0.5})`)
    
    ctx.strokeStyle = gradient
    ctx.lineWidth = 2 + this.pulse * 2
    ctx.shadowBlur = 10
    ctx.shadowColor = '#FFD700'
    
    // 绘制箭头
    const angle = Math.atan2(
      this.target.y - this.source.y,
      this.target.x - this.source.x
    )
    
    ctx.beginPath()
    ctx.moveTo(this.source.x, this.source.y)
    ctx.lineTo(this.target.x, this.target.y)
    ctx.stroke()
    
    // 箭头头部
    const headLength = 15
    ctx.beginPath()
    ctx.moveTo(this.target.x, this.target.y)
    ctx.lineTo(
      this.target.x - headLength * Math.cos(angle - Math.PI / 6),
      this.target.y - headLength * Math.sin(angle - Math.PI / 6)
    )
    ctx.lineTo(
      this.target.x - headLength * Math.cos(angle + Math.PI / 6),
      this.target.y - headLength * Math.sin(angle + Math.PI / 6)
    )
    ctx.closePath()
    ctx.fill()
  }
}

// 绘制代码雨背景
function drawCodeRain(ctx, width, height) {
  // 背景渐变
  const gradient = ctx.createLinearGradient(0, 0, 0, height)
  gradient.addColorStop(0, '#0A0A0B')
  gradient.addColorStop(0.5, '#111113')
  gradient.addColorStop(1, '#1A1B1E')
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, width, height)
  
  // 代码字符雨
  ctx.fillStyle = 'rgba(255, 215, 0, 0.1)'
  ctx.font = '14px monospace'
  
  const columns = Math.floor(width / 20)
  const drops = []
  
  for (let i = 0; i < columns; i++) {
    drops[i] = Math.random() * -100
  }
  
  drops.forEach((y, index) => {
    const char = String.fromCharCode(33 + Math.floor(Math.random() * 94))
    const x = index * 20
    ctx.fillText(char, x, y)
    
    if (y > height && Math.random() > 0.975) {
      drops[index] = 0
    }
    drops[index] += 10
  })
}

// 动画循环
function animate() {
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  
  if (!ctx) return
  
  // 更新画布尺寸引用
  codeParticles.forEach(p => {
    p.canvasWidth = canvas.width
    p.canvasHeight = canvas.height
  })
  
  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 绘制背景
  drawCodeRain(ctx, canvas.width, canvas.height)
  
  // 更新和绘制代码粒子
  codeParticles = codeParticles.filter(particle => {
    const alive = particle.update()
    if (alive) {
      particle.draw(ctx)
    }
    return alive
  })
  
  // 生成新的代码粒子
  if (Math.random() < 0.3 && codeParticles.length < 100) {
    const x = Math.random() * canvas.width
    const y = Math.random() * canvas.height
    const chars = '{}[]();:,.<>=+-*/%&|!?@#$^~`'
    const char = chars[Math.floor(Math.random() * chars.length)]
    codeParticles.push(new CodeParticle(x, y, char))
  }
  
  // 更新文件节点
  fileNodes.forEach(node => {
    node.update()
  })
  
  // 更新依赖连接
  props.dependencies.forEach(dep => {
    dep.update()
  })
  
  // 绘制依赖连接
  props.dependencies.forEach(dep => {
    dep.draw(ctx)
  })
  
  // 绘制文件节点
  fileNodes.forEach(node => {
    node.draw(ctx)
  })
  
  animationId = requestAnimationFrame(animate)
}

// 初始化文件节点
function initFileNodes(width, height) {
  fileNodes = []
  
  // 如果有传入文件数据
  if (props.files.length > 0) {
    props.files.forEach((file, index) => {
      const x = 100 + (width - 200) * Math.random()
      const y = 100 + (height - 200) * Math.random()
      const node = new FileNode(x, y, file.name || `File ${index}`, file.type || 'file')
      fileNodes.push(node)
    })
  } else {
    // 创建默认文件结构
    const defaultFiles = [
      { name: 'src/main.js', type: 'file' },
      { name: 'src/App.vue', type: 'component' },
      { name: 'src/components', type: 'folder' },
      { name: 'src/utils', type: 'folder' },
      { name: 'src/api', type: 'folder' },
      { name: 'src/store', type: 'folder' }
    ]
    
    defaultFiles.forEach((file, index) => {
      const angle = (Math.PI * 2 * index) / defaultFiles.length
      const distance = 150 + Math.random() * 100
      const x = width / 2 + Math.cos(angle) * distance
      const y = height / 2 + Math.sin(angle) * distance
      const node = new FileNode(x, y, file.name, file.type)
      fileNodes.push(node)
    })
  }
}

// 初始化依赖连接
function initDependencies() {
  props.dependencies.length = 0 // 清空现有依赖
  
  // 创建随机依赖关系
  for (let i = 0; i < fileNodes.length; i++) {
    const source = fileNodes[i]
    const connectionCount = 1 + Math.floor(Math.random() * 3)
    
    for (let j = 0; j < connectionCount; j++) {
      const targetIndex = Math.floor(Math.random() * fileNodes.length)
      const target = fileNodes[targetIndex]
      
      if (target && target !== source) {
        const dependency = new DependencyLink(source, target, 'import')
        props.dependencies.push(dependency)
        source.connections.push(dependency)
      }
    }
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
  
  // 初始化文件节点
  initFileNodes(canvas.width, canvas.height)
  
  // 初始化依赖连接
  initDependencies()
  
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
    initFileNodes(canvas.width, canvas.height)
    initDependencies()
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
watch([() => props.files, () => props.dependencies], () => {
  if (canvasRef.value) {
    initFileNodes(canvasRef.value.width, canvasRef.value.height)
    initDependencies()
  }
}, { deep: true })
</script>

<style scoped lang="scss">
.code-universe-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #0A0A0B 0%, #111113 100%);
}

.code-universe-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.code-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  background: radial-gradient(
    circle at 20% 20%,
    transparent 0%,
    rgba(10, 10, 11, 0.7) 100%
  );
}

.code-info {
  position: absolute;
  top: var(--spacing-xl);
  left: var(--spacing-xl);
  color: var(--color-gold-primary);
}

.code-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xs) 0;
  text-shadow: 0 0 10px var(--color-gold-primary);
}

.code-subtitle {
  font-size: var(--font-size-sm);
  margin: 0;
  opacity: 0.8;
}

.code-stats {
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