<template>
  <div class="api-caller">
    <div class="caller-header">
      <h3>API 调用</h3>
    </div>
    
    <div class="caller-content">
      <el-form :model="apiForm" label-width="80px" class="api-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="方法">
              <el-select v-model="apiForm.method" placeholder="选择方法" class="method-select">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="URL">
              <el-input v-model="apiForm.url" placeholder="输入API地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="Headers">
          <el-input
            v-model="apiForm.headersStr"
            type="textarea"
            :rows="2"
            placeholder='例如: {"Content-Type": "application/json", "Authorization": "Bearer token"}'
          />
        </el-form-item>
        
        <el-form-item label="Params" v-if="apiForm.method === 'GET'">
          <el-input
            v-model="apiForm.paramsStr"
            type="textarea"
            :rows="2"
            placeholder='例如: {"id": 1, "name": "test"}'
          />
        </el-form-item>
        
        <el-form-item label="Body" v-if="['POST', 'PUT'].includes(apiForm.method)">
          <el-input
            v-model="apiForm.bodyStr"
            type="textarea"
            :rows="4"
            placeholder='例如: {"name": "新项目", "description": "项目描述"}'
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="executeAPI" 
            :loading="isCallingAPI"
            :disabled="!apiForm.url"
          >
            <el-icon><CaretRight /></el-icon>
            执行API
          </el-button>
          <el-button @click="formatJSON">格式化JSON</el-button>
          <el-button @click="clearAll">清空</el-button>
          <el-button @click="fillSampleData">填充示例数据</el-button>
        </el-form-item>
      </el-form>
      
      <div class="api-result" v-if="apiResult">
        <h4>响应结果</h4>
        <div class="result-status">
          <el-tag :type="getResultType(apiResult.status)">
            状态码: {{ apiResult.status }}
          </el-tag>
          <el-tag type="info">耗时: {{ apiResult.duration }}ms</el-tag>
          <el-button size="small" @click="copyResponse">复制响应</el-button>
        </div>
        <pre class="result-body">{{ formatResponse(apiResult.data) }}</pre>
      </div>
      
      <div class="api-history" v-if="callHistory.length > 0">
        <h4>调用历史</h4>
        <el-table :data="callHistory" style="width: 100%" size="small">
          <el-table-column prop="method" label="方法" width="80">
            <template #default="{ row }">
              <el-tag :type="getMethodType(row.method)" size="small">
                {{ row.method }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="url" label="URL" show-overflow-tooltip></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="getResultType(row.status)" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="耗时(ms)" width="100"></el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" type="text" @click="replayAPI(row)">重放</el-button>
              <el-button size="small" type="text" @click="fillWithHistory(row)">填充</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { CaretRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const isCallingAPI = ref(false)
const apiResult = ref(null)
const callHistory = ref([])

// API表单数据
const apiForm = reactive({
  method: 'GET',
  url: '',
  headersStr: '{"Content-Type": "application/json"}',
  paramsStr: '{}',
  bodyStr: '{}'
})

// 方法
const executeAPI = async () => {
  if (!apiForm.url) {
    ElMessage.warning('请输入API地址')
    return
  }

  isCallingAPI.value = true
  const startTime = Date.now()
  
  try {
    // 解析headers, params, body
    let headers = {}
    let params = {}
    let body = null
    
    try {
      headers = apiForm.headersStr ? JSON.parse(apiForm.headersStr) : {}
    } catch (e) {
      ElMessage.error('Headers JSON格式错误')
      return
    }
    
    if (apiForm.method === 'GET') {
      try {
        params = apiForm.paramsStr ? JSON.parse(apiForm.paramsStr) : {}
      } catch (e) {
        ElMessage.error('Params JSON格式错误')
        return
      }
    }
    
    if (['POST', 'PUT'].includes(apiForm.method)) {
      try {
        body = apiForm.bodyStr ? JSON.parse(apiForm.bodyStr) : {}
      } catch (e) {
        ElMessage.error('Body JSON格式错误')
        return
      }
    }
    
    // 构建请求URL（如果方法是GET且有参数）
    let requestUrl = apiForm.url
    if (apiForm.method === 'GET' && Object.keys(params).length > 0) {
      const queryString = new URLSearchParams(params).toString()
      requestUrl += (requestUrl.includes('?') ? '&' : '?') + queryString
    }
    
    // 发送请求
    const response = await callAPI(requestUrl, {
      method: apiForm.method,
      headers: headers,
      body: ['POST', 'PUT'].includes(apiForm.method) ? JSON.stringify(body) : undefined
    })
    
    const duration = Date.now() - startTime
    
    // 处理响应
    let responseData;
    try {
      responseData = await response.json();
    } catch (e) {
      // 如果不是JSON响应，则获取文本
      responseData = { text: await response.text(), status: response.status };
    }
    
    const result = {
      status: response.status,
      data: responseData,
      duration: duration
    }
    
    apiResult.value = result
    
    // 添加到历史记录
    callHistory.value.unshift({
      method: apiForm.method,
      url: apiForm.url,
      status: result.status,
      duration: duration,
      request: {
        headers,
        params: apiForm.method === 'GET' ? params : undefined,
        body: ['POST', 'PUT'].includes(apiForm.method) ? body : undefined
      },
      response: result.data
    })
    
    // 限制历史记录数量
    if (callHistory.value.length > 20) {
      callHistory.value = callHistory.value.slice(0, 20)
    }
    
    ElMessage.success(`API调用成功，状态码: ${response.status}`)
    
  } catch (error) {
    console.error('API调用错误:', error)
    const duration = Date.now() - startTime
    apiResult.value = {
      status: 'ERROR',
      data: { error: error.message },
      duration: duration
    }
    ElMessage.error(`API调用失败: ${error.message}`)
  } finally {
    isCallingAPI.value = false
  }
}

// 模拟API调用函数（实际项目中应替换为真实的API调用）
const callAPI = async (url, options) => {
  // 检查是否是本地API，如果是则添加基础路径
  if (url.startsWith('/api/')) {
    url = 'http://localhost:8080' + url
  }
  
  return fetch(url, options)
}

const formatJSON = () => {
  try {
    if (apiForm.headersStr) {
      const parsed = JSON.parse(apiForm.headersStr)
      apiForm.headersStr = JSON.stringify(parsed, null, 2)
    }
    
    if (apiForm.paramsStr && apiForm.method === 'GET') {
      const parsed = JSON.parse(apiForm.paramsStr)
      apiForm.paramsStr = JSON.stringify(parsed, null, 2)
    }
    
    if (apiForm.bodyStr && ['POST', 'PUT'].includes(apiForm.method)) {
      const parsed = JSON.parse(apiForm.bodyStr)
      apiForm.bodyStr = JSON.stringify(parsed, null, 2)
    }
    
    ElMessage.success('JSON格式化完成')
  } catch (e) {
    ElMessage.error('JSON格式错误')
  }
}

const clearAll = () => {
  apiForm.method = 'GET'
  apiForm.url = ''
  apiForm.headersStr = '{"Content-Type": "application/json"}'
  apiForm.paramsStr = '{}'
  apiForm.bodyStr = '{}'
  apiResult.value = null
  ElMessage.info('已清空所有数据')
}

const fillSampleData = () => {
  if (apiForm.url.includes('/project')) {
    if (apiForm.method === 'POST') {
      apiForm.bodyStr = JSON.stringify({
        name: '新项目',
        description: '通过AI助手创建的项目',
        status: 'developing',
        tech_stack: 'SpringBoot3,Vue3,MySQL'
      }, null, 2)
    } else if (apiForm.method === 'PUT') {
      apiForm.bodyStr = JSON.stringify({
        id: 1,
        name: '更新后的项目',
        description: '更新后的项目描述'
      }, null, 2)
    }
  } else if (apiForm.url.includes('/task')) {
    if (apiForm.method === 'POST') {
      apiForm.bodyStr = JSON.stringify({
        projectId: 1,
        title: '新任务',
        description: '任务描述',
        assignee: '开发者',
        status: 'todo'
      }, null, 2)
    }
  } else {
    // 默认示例数据
    if (apiForm.method === 'POST' || apiForm.method === 'PUT') {
      apiForm.bodyStr = JSON.stringify({
        name: '示例名称',
        description: '示例描述'
      }, null, 2)
    }
  }
  
  ElMessage.success('已填充示例数据')
}

const replayAPI = (record) => {
  apiForm.method = record.method
  apiForm.url = record.url
  
  // 重新填充表单数据
  apiForm.headersStr = JSON.stringify(record.request.headers, null, 2)
  
  if (record.method === 'GET') {
    apiForm.paramsStr = JSON.stringify(record.request.params || {}, null, 2)
  }
  
  if (['POST', 'PUT'].includes(record.method)) {
    apiForm.bodyStr = JSON.stringify(record.request.body || {}, null, 2)
  }
  
  // 自动执行
  setTimeout(() => {
    executeAPI()
  }, 100)
}

const fillWithHistory = (record) => {
  apiForm.method = record.method
  apiForm.url = record.url
  
  // 重新填充表单数据
  apiForm.headersStr = JSON.stringify(record.request.headers, null, 2)
  
  if (record.method === 'GET') {
    apiForm.paramsStr = JSON.stringify(record.request.params || {}, null, 2)
  }
  
  if (['POST', 'PUT'].includes(record.method)) {
    apiForm.bodyStr = JSON.stringify(record.request.body || {}, null, 2)
  }
  
  ElMessage.success('已用历史记录填充表单')
}

const copyResponse = async () => {
  if (apiResult.value) {
    try {
      await navigator.clipboard.writeText(JSON.stringify(apiResult.value.data, null, 2))
      ElMessage.success('响应内容已复制到剪贴板')
    } catch (err) {
      console.error('复制失败:', err)
      ElMessage.error('复制失败')
    }
  }
}

const getResultType = (status) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  if (status === 'ERROR') return 'danger'
  return 'info'
}

const getMethodType = (method) => {
  switch (method) {
    case 'GET': return 'primary'
    case 'POST': return 'success'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return 'info'
  }
}

const formatResponse = (data) => {
  return JSON.stringify(data, null, 2)
}

// 从外部接收API信息的函数
const setAPIInfo = (apiInfo) => {
  apiForm.url = apiInfo.path
  apiForm.method = apiInfo.method
  
  if (apiInfo.method === 'GET') {
    // 对于GET请求，根据参数构建params
    const paramsObj = {}
    if (apiInfo.parameters) {
      apiInfo.parameters.forEach(param => {
        if (param.required) {
          paramsObj[param.name] = param.type === 'integer' ? 1 : 'value'
        }
      })
    }
    apiForm.paramsStr = JSON.stringify(paramsObj, null, 2)
  } else if (['POST', 'PUT'].includes(apiInfo.method)) {
    // 对于POST/PUT请求，根据参数构建body
    const bodyObj = {}
    if (apiInfo.parameters) {
      apiInfo.parameters.forEach(param => {
        if (param.required) {
          bodyObj[param.name] = param.type === 'integer' ? 1 : 'value'
        }
      })
    }
    apiForm.bodyStr = JSON.stringify(bodyObj, null, 2)
  }
}

// 定义emit
const emit = defineEmits(['apiCallSuccess', 'apiCallError'])

// 暴露方法给父组件
defineExpose({
  setAPIInfo
})
</script>

<style scoped lang="scss">
.api-caller {
  padding: 16px;
  
  .caller-header {
    h3 {
      margin: 0 0 16px 0;
      color: #ffd700;
      font-size: 16px;
    }
  }
  
  .caller-content {
    .api-form {
      .method-select {
        width: 100%;
      }
      
      .el-form-item {
        margin-bottom: 16px;
      }
    }
    
    .api-result {
      margin-top: 24px;
      padding-top: 16px;
      border-top: 1px solid #2c2d31;
      
      h4 {
        color: #409eff;
        margin: 0 0 12px 0;
      }
      
      .result-status {
        display: flex;
        gap: 12px;
        margin-bottom: 12px;
      }
      
      .result-body {
        background: #2c2d31;
        padding: 12px;
        border-radius: 4px;
        color: #e5e5e5;
        font-size: 13px;
        max-height: 300px;
        overflow-y: auto;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }
    
    .api-history {
      margin-top: 24px;
      padding-top: 16px;
      border-top: 1px solid #2c2d31;
      
      h4 {
        color: #409eff;
        margin: 0 0 12px 0;
      }
    }
  }
}
</style>