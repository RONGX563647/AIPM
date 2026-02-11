<template>
  <div class="visualization-controller">
    <div class="visualization-display" ref="displayRef">
      <component 
        :is="currentComponent" 
        v-bind="componentProps"
        class="visualization-component"
      />
    </div>
    
    <div class="visualization-controls" v-if="showControls">
      <div class="control-panel">
        <el-button-group>
          <el-button 
            v-for="vis in visualizations" 
            :key="vis.name"
            :type="currentVisualization === vis.name ? 'primary' : 'default'"
            @click="switchVisualization(vis.name)"
            size="small"
            class="control-button"
          >
            {{ vis.label }}
          </el-button>
        </el-button-group>
        
        <el-button 
          @click="toggleAutoSwitch"
          :type="autoSwitch ? 'success' : 'default'"
          size="small"
          class="auto-switch-button"
        >
          {{ autoSwitch ? '自动切换: 开' : '自动切换: 关' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import TimeRiver from './TimeRiver.vue'
import GalaxyMap from './GalaxyMap.vue'
import CodeUniverse from './CodeUniverse.vue'

const props = defineProps({
  defaultVisualization: {
    type: String,
    default: 'time-river'
  },
  showControls: {
    type: Boolean,
    default: true
  },
  autoSwitch: {
    type: Boolean,
    default: false
  },
  switchInterval: {
    type: Number,
    default: 10000 // 10秒
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

const emit = defineEmits(['visualization-change'])

const currentVisualization = ref(props.defaultVisualization)
const autoSwitchTimer = ref(null)
const displayRef = ref(null)

// 可视化组件配置
const visualizations = [
  { name: 'time-river', label: '时间河流', component: TimeRiver },
  { name: 'galaxy-map', label: '宇宙星图', component: GalaxyMap },
  { name: 'code-universe', label: '代码宇宙', component: CodeUniverse }
]

// 当前组件
const currentComponent = computed(() => {
  const vis = visualizations.find(v => v.name === currentVisualization.value)
  return vis ? vis.component : TimeRiver
})

// 组件属性
const componentProps = computed(() => {
  const baseProps = {
    width: props.width,
    height: props.height
  }

  switch (currentVisualization.value) {
    case 'time-river':
      return {
        ...baseProps,
        tasks: [
          { id: 1, name: '项目A', progress: 0.3 },
          { id: 2, name: '项目B', progress: 0.7 },
          { id: 3, name: '项目C', progress: 0.9 }
        ]
      }
    
    case 'galaxy-map':
      return {
        ...baseProps,
        nodes: [
          { id: 1, name: '核心模块', x: 200, y: 150 },
          { id: 2, name: '用户系统', x: 400, y: 100 },
          { id: 3, name: '数据层', x: 300, y: 300 },
          { id: 4, name: 'API服务', x: 500, y: 250 }
        ],
        connections: [
          { source: 1, target: 2 },
          { source: 1, target: 3 },
          { source: 2, target: 4 },
          { source: 3, target: 4 }
        ]
      }
    
    case 'code-universe':
      return {
        ...baseProps,
        files: [
          { name: 'src/main.js', type: 'file' },
          { name: 'src/App.vue', type: 'component' },
          { name: 'src/components', type: 'folder' },
          { name: 'src/utils', type: 'folder' },
          { name: 'src/api', type: 'folder' }
        ],
        dependencies: []
      }
    
    default:
      return baseProps
  }
})

// 切换可视化
function switchVisualization(name) {
  currentVisualization.value = name
  emit('visualization-change', name)
  
  // 重置自动切换定时器
  if (autoSwitchTimer.value) {
    clearInterval(autoSwitchTimer.value)
  }
  
  if (props.autoSwitch) {
    startAutoSwitch()
  }
}

// 自动切换
function toggleAutoSwitch() {
  const newAutoSwitch = !props.autoSwitch
  // 这里可以通过emit通知父组件更新props.autoSwitch
  console.log('Auto switch:', newAutoSwitch)
  
  if (newAutoSwitch) {
    startAutoSwitch()
  } else {
    stopAutoSwitch()
  }
}

function startAutoSwitch() {
  if (autoSwitchTimer.value) {
    clearInterval(autoSwitchTimer.value)
  }
  
  autoSwitchTimer.value = setInterval(() => {
    const currentIndex = visualizations.findIndex(v => v.name === currentVisualization.value)
    const nextIndex = (currentIndex + 1) % visualizations.length
    switchVisualization(visualizations[nextIndex].name)
  }, props.switchInterval)
}

function stopAutoSwitch() {
  if (autoSwitchTimer.value) {
    clearInterval(autoSwitchTimer.value)
    autoSwitchTimer.value = null
  }
}

// 响应式处理
function handleResize() {
  if (!displayRef.value) return
  
  const rect = displayRef.value.getBoundingClientRect()
  // 可以在这里更新组件尺寸
}

onMounted(() => {
  if (props.autoSwitch) {
    startAutoSwitch()
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  stopAutoSwitch()
  window.removeEventListener('resize', handleResize)
})

// 暴露方法给父组件
defineExpose({
  switchVisualization,
  getCurrentVisualization: () => currentVisualization.value
})
</script>

<style scoped lang="scss">
.visualization-controller {
  position: relative;
  width: 100%;
  height: 100%;
}

.visualization-display {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.visualization-component {
  width: 100%;
  height: 100%;
  display: block;
}

.visualization-controls {
  position: absolute;
  bottom: var(--spacing-lg);
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  pointer-events: auto;
}

.control-panel {
  background: rgba(26, 27, 30, 0.8);
  backdrop-filter: blur(10px);
  border: var(--border-gold-sm);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  display: flex;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-lg);
}

.control-button {
  transition: all var(--duration-fast) var(--ease-in-out);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-gold);
  }
}

.auto-switch-button {
  transition: all var(--duration-fast) var(--ease-in-out);
  
  &:hover {
    transform: translateY(-2px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .control-panel {
    flex-direction: column;
    align-items: center;
    padding: var(--spacing-sm);
    gap: var(--spacing-sm);
  }
  
  .control-button {
    width: 100%;
  }
}
</style>