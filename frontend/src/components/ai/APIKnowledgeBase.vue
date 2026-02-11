<template>
  <div class="api-knowledge-base">
    <div class="knowledge-header">
      <h3>API知识库</h3>
      <el-button type="primary" size="small" @click="loadAPIDocs">加载API文档</el-button>
    </div>
    
    <div class="knowledge-search">
      <el-input
        v-model="searchQuery"
        placeholder="搜索API..."
        prefix-icon="Search"
        clearable
      />
    </div>
    
    <div class="knowledge-content">
      <el-collapse v-model="activeNames" accordion>
        <el-collapse-item title="项目管理API" name="project">
          <div class="api-group">
            <el-card 
              v-for="api in filteredProjectAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="任务管理API" name="task">
          <div class="api-group">
            <el-card 
              v-for="api in filteredTaskAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="接口管理API" name="apiInfo">
          <div class="api-group">
            <el-card 
              v-for="api in filteredApiInfoAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="AI代码评审API" name="aiReview">
          <div class="api-group">
            <el-card 
              v-for="api in filteredAiReviewAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="系统监控API" name="monitor">
          <div class="api-group">
            <el-card 
              v-for="api in filteredMonitorAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="数据中心API" name="dataCenter">
          <div class="api-group">
            <el-card 
              v-for="api in filteredDataCenterAPIs" 
              :key="api.path" 
              class="api-card"
              @click="selectAPI(api)"
            >
              <div class="api-header">
                <span :class="'method-' + api.method.toLowerCase()">{{ api.method.toUpperCase() }}</span>
                <span class="api-path">{{ api.path }}</span>
              </div>
              <div class="api-description">{{ api.description }}</div>
            </el-card>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    
    <el-dialog v-model="showAPIDetail" title="API详情" width="60%">
      <div v-if="selectedAPI" class="api-detail">
        <h4>{{ selectedAPI.method.toUpperCase() }} {{ selectedAPI.path }}</h4>
        <p><strong>描述:</strong> {{ selectedAPI.description }}</p>
        
        <div v-if="selectedAPI.parameters && selectedAPI.parameters.length > 0">
          <h5>参数:</h5>
          <el-table :data="selectedAPI.parameters" style="width: 100%" size="small">
            <el-table-column prop="name" label="参数名" width="150"></el-table-column>
            <el-table-column prop="type" label="类型" width="100"></el-table-column>
            <el-table-column prop="required" label="必需" width="80">
              <template #default="{ row }">
                <el-tag :type="row.required ? 'danger' : 'success'">
                  {{ row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明"></el-table-column>
          </el-table>
        </div>
        
        <div v-if="selectedAPI.responses && Object.keys(selectedAPI.responses).length > 0">
          <h5>响应:</h5>
          <el-table :data="responseList" style="width: 100%" size="small">
            <el-table-column prop="code" label="状态码" width="100"></el-table-column>
            <el-table-column prop="description" label="说明"></el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAPIDetail = false">关闭</el-button>
          <el-button type="primary" @click="useAPI">使用此API</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

// 响应式数据
const activeNames = ref(['project'])
const showAPIDetail = ref(false)
const selectedAPI = ref(null)
const searchQuery = ref('')

// API定义
const apis = ref({
  project: [
    {
      method: 'GET',
      path: '/api/project/list',
      description: '获取项目列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'name', type: 'string', required: false, description: '项目名称' },
        { name: 'status', type: 'string', required: false, description: '项目状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/project/create',
      description: '创建项目',
      parameters: [
        { name: 'name', type: 'string', required: true, description: '项目名称' },
        { name: 'description', type: 'string', required: false, description: '项目描述' },
        { name: 'git_url', type: 'string', required: false, description: 'Git仓库地址' },
        { name: 'tech_stack', type: 'string', required: false, description: '技术栈' },
        { name: 'status', type: 'string', required: true, description: '项目状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/project/update',
      description: '更新项目',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '项目ID' },
        { name: 'name', type: 'string', required: false, description: '项目名称' },
        { name: 'description', type: 'string', required: false, description: '项目描述' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/project/delete',
      description: '删除项目',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '项目ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ],
  task: [
    {
      method: 'GET',
      path: '/api/task/list',
      description: '获取任务列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'projectId', type: 'integer', required: false, description: '项目ID' },
        { name: 'status', type: 'string', required: false, description: '任务状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/task/create',
      description: '创建任务',
      parameters: [
        { name: 'projectId', type: 'integer', required: true, description: '项目ID' },
        { name: 'title', type: 'string', required: true, description: '任务标题' },
        { name: 'description', type: 'string', required: false, description: '任务描述' },
        { name: 'assignee', type: 'string', required: false, description: '负责人' },
        { name: 'status', type: 'string', required: false, description: '任务状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/task/update',
      description: '更新任务',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '任务ID' },
        { name: 'title', type: 'string', required: false, description: '任务标题' },
        { name: 'status', type: 'string', required: false, description: '任务状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/task/delete',
      description: '删除任务',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '任务ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ],
  apiInfo: [
    {
      method: 'GET',
      path: '/api/api-info/list',
      description: '获取接口列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'projectId', type: 'integer', required: false, description: '项目ID' },
        { name: 'method', type: 'string', required: false, description: '请求方法' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/api-info/create',
      description: '创建接口',
      parameters: [
        { name: 'projectId', type: 'integer', required: true, description: '项目ID' },
        { name: 'path', type: 'string', required: true, description: '接口路径' },
        { name: 'method', type: 'string', required: true, description: '请求方法' },
        { name: 'params', type: 'object', required: false, description: '请求参数(JSON)' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/api-info/update',
      description: '更新接口',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '接口ID' },
        { name: 'path', type: 'string', required: false, description: '接口路径' },
        { name: 'params', type: 'object', required: false, description: '请求参数(JSON)' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/api-info/delete',
      description: '删除接口',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '接口ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ],
  aiReview: [
    {
      method: 'GET',
      path: '/api/code-ai-review/list',
      description: '获取AI代码评审列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'projectId', type: 'integer', required: false, description: '项目ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/code-ai-review/create',
      description: '创建AI代码评审',
      parameters: [
        { name: 'projectId', type: 'integer', required: true, description: '项目ID' },
        { name: 'code', type: 'string', required: true, description: '待评审代码' },
        { name: 'language', type: 'string', required: true, description: '编程语言' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/code-ai-review/update',
      description: '更新AI代码评审',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '评审ID' },
        { name: 'status', type: 'string', required: false, description: '评审状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/code-ai-review/delete',
      description: '删除AI代码评审',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '评审ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ],
  monitor: [
    {
      method: 'GET',
      path: '/api/monitor/list',
      description: '获取监控指标列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'type', type: 'string', required: false, description: '指标类型' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/monitor/create',
      description: '创建监控指标',
      parameters: [
        { name: 'name', type: 'string', required: true, description: '指标名称' },
        { name: 'type', type: 'string', required: true, description: '指标类型' },
        { name: 'threshold', type: 'number', required: true, description: '阈值' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/monitor/update',
      description: '更新监控指标',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '指标ID' },
        { name: 'threshold', type: 'number', required: false, description: '阈值' },
        { name: 'status', type: 'string', required: false, description: '状态' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/monitor/delete',
      description: '删除监控指标',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '指标ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ],
  dataCenter: [
    {
      method: 'GET',
      path: '/api/data-metric/list',
      description: '获取数据指标列表',
      parameters: [
        { name: 'pageNum', type: 'integer', required: true, description: '页码' },
        { name: 'pageSize', type: 'integer', required: true, description: '每页数量' },
        { name: 'category', type: 'string', required: false, description: '指标类别' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'POST',
      path: '/api/data-metric/create',
      description: '创建数据指标',
      parameters: [
        { name: 'name', type: 'string', required: true, description: '指标名称' },
        { name: 'category', type: 'string', required: true, description: '指标类别' },
        { name: 'value', type: 'number', required: true, description: '指标值' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'PUT',
      path: '/api/data-metric/update',
      description: '更新数据指标',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '指标ID' },
        { name: 'value', type: 'number', required: false, description: '指标值' },
        { name: 'category', type: 'string', required: false, description: '指标类别' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    },
    {
      method: 'DELETE',
      path: '/api/data-metric/delete',
      description: '删除数据指标',
      parameters: [
        { name: 'id', type: 'integer', required: true, description: '指标ID' }
      ],
      responses: {
        '200': { description: '成功' }
      }
    }
  ]
})

// 计算属性 - 带搜索过滤的API列表
const filteredProjectAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.project
  const query = searchQuery.value.toLowerCase()
  return apis.value.project.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const filteredTaskAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.task
  const query = searchQuery.value.toLowerCase()
  return apis.value.task.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const filteredApiInfoAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.apiInfo
  const query = searchQuery.value.toLowerCase()
  return apis.value.apiInfo.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const filteredAiReviewAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.aiReview
  const query = searchQuery.value.toLowerCase()
  return apis.value.aiReview.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const filteredMonitorAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.monitor
  const query = searchQuery.value.toLowerCase()
  return apis.value.monitor.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const filteredDataCenterAPIs = computed(() => {
  if (!searchQuery.value) return apis.value.dataCenter
  const query = searchQuery.value.toLowerCase()
  return apis.value.dataCenter.filter(api => 
    api.path.toLowerCase().includes(query) || 
    api.description.toLowerCase().includes(query) ||
    api.method.toLowerCase().includes(query)
  )
})

const responseList = computed(() => {
  if (!selectedAPI.value || !selectedAPI.value.responses) return []
  return Object.entries(selectedAPI.value.responses).map(([code, info]) => ({
    code,
    description: info.description
  }))
})

// 方法
const selectAPI = (api) => {
  selectedAPI.value = api
  showAPIDetail.value = true
}

const useAPI = () => {
  // 触发事件通知父组件使用选中的API
  emit('useAPI', selectedAPI.value)
  showAPIDetail.value = false
}

const loadAPIDocs = () => {
  // 这里可以实现从Swagger/OpenAPI文档加载API定义的逻辑
  console.log('加载API文档...')
  ElMessage.success('API文档加载完成')
}

// 定义emit
const emit = defineEmits(['useAPI'])

// 导入Element Plus消息组件
import { ElMessage } from 'element-plus'
</script>

<style scoped lang="scss">
.api-knowledge-base {
  padding: 16px;
  
  .knowledge-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      color: #ffd700;
      font-size: 16px;
    }
  }
  
  .knowledge-content {
    .api-group {
      padding: 8px 0;
    }
    
    .api-card {
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
      
      .api-header {
        display: flex;
        gap: 8px;
        align-items: center;
        
        .method-get {
          background: #409eff;
          color: white;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: bold;
        }
        
        .method-post {
          background: #67c23a;
          color: white;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: bold;
        }
        
        .method-put {
          background: #e6a23c;
          color: white;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: bold;
        }
        
        .method-delete {
          background: #f56c6c;
          color: white;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          font-weight: bold;
        }
        
        .api-path {
          color: #e5e5e5;
          font-family: monospace;
          font-size: 14px;
        }
      }
      
      .api-description {
        margin-top: 8px;
        color: #8e8e93;
        font-size: 13px;
      }
    }
  }
  
  .api-detail {
    h4 {
      color: #ffd700;
      margin-bottom: 16px;
    }
    
    h5 {
      color: #409eff;
      margin: 16px 0 8px 0;
    }
    
    p {
      color: #e5e5e5;
      margin: 8px 0;
    }
  }
}
</style>