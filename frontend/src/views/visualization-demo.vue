<template>
  <div class="demo-container">
    <div class="demo-header">
      <h1>资格可视化组件演示</h1>
      <p>AI全自动研发大脑 - 可视化组件展示</p>
    </div>
    
    <div class="demo-content">
      <div class="visualization-showcase">
        <div class="showcase-item" v-for="vis in visualizations" :key="vis.name">
          <h3 class="showcase-title">{{ vis.label }}</h3>
          <div class="showcase-visualization">
            <component 
              :is="vis.component" 
              :width="400" 
              :height="300"
              class="demo-component"
            />
          </div>
        </div>
      </div>
      
      <div class="controller-demo">
        <h3>可视化控制器演示</h3>
        <VisualizationController 
          :default-visualization="'time-river'"
          :show-controls="true"
          :auto-switch="true"
          :switch-interval="5000"
          :width="800"
          :height="500"
          class="controller-component"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TimeRiver from '@/components/visualization/TimeRiver.vue'
import GalaxyMap from '@/components/visualization/GalaxyMap.vue'
import CodeUniverse from '@/components/visualization/CodeUniverse.vue'
import VisualizationController from '@/components/visualization/VisualizationController.vue'

const visualizations = [
  { name: 'time-river', label: '时间河流', component: TimeRiver },
  { name: 'galaxy-map', label: '宇宙星图', component: GalaxyMap },
  { name: 'code-universe', label: '代码宇宙', component: CodeUniverse }
]
</script>

<style scoped lang="scss">
.demo-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1A1B1E 100%);
  color: #E5E5E5;
  padding: 32px;
}

.demo-header {
  text-align: center;
  margin-bottom: 48px;
  
  h1 {
    font-size: 36px;
    font-weight: bold;
    background: linear-gradient(135deg, #FFD700 0%, #FFED4E 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 16px;
  }
  
  p {
    font-size: 18px;
    color: #8E8E93;
    margin: 0;
  }
}

.demo-content {
  max-width: 1400px;
  margin: 0 auto;
}

.visualization-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(420px, 1fr));
  gap: 32px;
  margin-bottom: 48px;
}

.showcase-item {
  background: rgba(26, 27, 30, 0.6);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(255, 215, 0, 0.2);
  backdrop-filter: blur(10px);
}

.showcase-title {
  font-size: 20px;
  color: #FFD700;
  margin-bottom: 16px;
  text-align: center;
}

.showcase-visualization {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.demo-component {
  display: block;
  width: 100%;
}

.controller-demo {
  background: rgba(26, 27, 30, 0.6);
  border-radius: 12px;
  padding: 32px;
  border: 1px solid rgba(255, 215, 0, 0.2);
  backdrop-filter: blur(10px);
  text-align: center;
  
  h3 {
    font-size: 24px;
    color: #FFD700;
    margin-bottom: 24px;
  }
}

.controller-component {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

@media (max-width: 768px) {
  .demo-container {
    padding: 16px;
  }
  
  .demo-header h1 {
    font-size: 28px;
  }
  
  .visualization-showcase {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .showcase-item {
    padding: 16px;
  }
}
</style>