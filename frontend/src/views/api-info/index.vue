<template>
  <div class="api-container">
    <div class="api-header">
      <h1 class="api-title">接口中心</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增接口
      </el-button>
    </div>

    <div class="api-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="接口路径">
            <el-input
              v-model="searchForm.path"
              placeholder="请输入接口路径"
              clearable
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="请求方法">
            <el-select
              v-model="searchForm.method"
              placeholder="请选择方法"
              clearable
              class="search-select"
            >
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="DELETE" value="DELETE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" class="gold-button">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleReset" class="reset-button">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="table-card">
        <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          class="api-table"
        >
          <el-table-column prop="path" label="接口路径" min-width="200" show-overflow-tooltip />
          <el-table-column prop="method" label="请求方法" width="100">
            <template #default="{ row }">
              <el-tag :type="getMethodType(row.method)" size="small">
                {{ row.method }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="120">
            <template #default="{ row }">
              <span v-if="row.createTime">{{ formatDate(row.createTime) }}</span>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                @click="handleDebug(row)"
                class="action-button"
              >
                <el-icon><VideoPlay /></el-icon>
                调试
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleEdit(row)"
                class="action-button"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
                class="action-button"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
        />
      </el-card>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      class="api-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="接口路径" prop="path">
          <el-input v-model="formData.path" placeholder="请输入接口路径，如：/sys/user/login" />
        </el-form-item>
        <el-form-item label="请求方法" prop="method">
          <el-select v-model="formData.method" placeholder="请选择请求方法" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="请求参数" prop="params">
          <el-input
            v-model="formData.params"
            type="textarea"
            :rows="6"
            placeholder='请输入请求参数（JSON格式），如：{"username": "string", "password": "string"}'
          />
        </el-form-item>
        <el-form-item label="请求头" prop="header">
          <el-input
            v-model="formData.header"
            type="textarea"
            :rows="4"
            placeholder='请输入请求头（JSON格式），如：{"Authorization": "Bearer token"}'
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入接口备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="gold-button">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="debugDialogVisible"
      title="在线调试"
      width="900px"
      class="debug-dialog"
    >
      <el-form
        ref="debugFormRef"
        :model="debugForm"
        label-width="100px"
      >
        <el-form-item label="接口路径">
          <el-input v-model="debugForm.path" placeholder="请输入接口路径" />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select v-model="debugForm.method" placeholder="请选择请求方法" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="请求参数">
          <el-input
            v-model="debugForm.params"
            type="textarea"
            :rows="6"
            placeholder='请输入请求参数（JSON格式）'
          />
        </el-form-item>
        <el-form-item label="请求头">
          <el-input
            v-model="debugForm.header"
            type="textarea"
            :rows="4"
            placeholder='请输入请求头（JSON格式）'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="debugDialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSendDebug" class="gold-button" :loading="debugLoading">
          <el-icon><VideoPlay /></el-icon>
          发送请求
        </el-button>
      </template>

      <div v-if="debugResult" class="debug-result">
        <div class="result-header">
          <span class="result-title">响应结果</span>
          <el-tag :type="debugResult.success ? 'success' : 'danger'" size="small">
            {{ debugResult.success ? '成功' : '失败' }}
          </el-tag>
        </div>
        <div v-if="debugResult.statusCode" class="result-item">
          <span class="result-label">状态码：</span>
          <span class="result-value">{{ debugResult.statusCode }}</span>
        </div>
        <div v-if="debugResult.body" class="result-item">
          <span class="result-label">响应体：</span>
          <pre class="result-content">{{ debugResult.body }}</pre>
        </div>
        <div v-if="debugResult.error" class="result-item">
          <span class="result-label">错误信息：</span>
          <pre class="result-content error">{{ debugResult.error }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Delete, VideoPlay } from '@element-plus/icons-vue'
import { apiInfoApi } from '@/api/api-info.api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增接口')
const formRef = ref(null)

const debugDialogVisible = ref(false)
const debugLoading = ref(false)
const debugResult = ref(null)
const debugFormRef = ref(null)

const searchForm = reactive({
  path: '',
  method: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  projectId: 1,
  path: '',
  method: 'GET',
  params: '',
  header: '',
  remark: ''
})

const debugForm = reactive({
  path: '',
  method: 'GET',
  params: '',
  header: ''
})

const formRules = {
  path: [
    { required: true, message: '请输入接口路径', trigger: 'blur' }
  ],
  method: [
    { required: true, message: '请选择请求方法', trigger: 'change' }
  ]
}

const getMethodType = (method) => {
  const typeMap = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return typeMap[method] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const handleDebug = (row) => {
  debugForm.path = row.path
  debugForm.method = row.method
  debugForm.params = row.params || ''
  debugForm.header = row.header || ''
  debugResult.value = null
  debugDialogVisible.value = true
}

const handleSendDebug = async () => {
  debugLoading.value = true
  try {
    const res = await apiInfoApi.debug({
      path: debugForm.path,
      method: debugForm.method,
      params: debugForm.params,
      header: debugForm.header
    })
    if (res.code === 0) {
      debugResult.value = res.data
    }
  } catch (error) {
    ElMessage.error('调试请求失败')
  } finally {
    debugLoading.value = false
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await apiInfoApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: formData.projectId,
      path: searchForm.path || undefined,
      method: searchForm.method || undefined
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取接口列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.path = ''
  searchForm.method = ''
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增接口'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑接口'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该接口吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await apiInfoApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.params) {
      try {
        JSON.parse(formData.params)
      } catch (e) {
        ElMessage.error('请求参数格式不正确，请输入有效的JSON格式')
        return
      }
    }
    
    if (formData.id) {
      await apiInfoApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await apiInfoApi.add(formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(formData.id ? '更新失败' : '新增失败')
    }
  }
}

const resetForm = () => {
  formData.id = null
  formData.path = ''
  formData.method = 'GET'
  formData.params = ''
  formRef.value?.clearValidate()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.api-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.api-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.api-title {
  font-size: 28px;
  font-weight: bold;
  color: #ffd700;
  margin: 0;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.gold-button {
  background: linear-gradient(135deg, #ffd700 0%, #b38b2d 100%);
  border: none;
  color: #111113;
  font-weight: bold;

  &:hover {
    background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
    box-shadow: 0 4px 16px rgba(255, 215, 0, 0.4);
  }
}

.reset-button {
  background: #2c2d31;
  border: 1px solid #4a4b4f;
  color: #e5e5e5;

  &:hover {
    background: #3a3b3f;
    border-color: #ffd700;
  }
}

.api-content {
  .search-card,
  .table-card {
    background: rgba(26, 27, 30, 0.8);
    border: 1px solid rgba(255, 215, 0, 0.2);
    backdrop-filter: blur(10px);
    margin-bottom: 24px;

    &:deep(.el-card__body) {
      padding: 24px;
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .search-input,
  .search-select {
    width: 200px;

    &:deep(.el-input__wrapper) {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;

      &:hover {
        border-color: #ffd700;
      }

      &.is-focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }
  }
}

.api-table {
  background: transparent;

  &:deep(.el-table__header-wrapper) {
    th {
      background: #1a1b1e;
      color: #ffd700;
      font-weight: bold;
      border-bottom: 1px solid rgba(255, 215, 0, 0.3);
    }
  }

  &:deep(.el-table__body-wrapper) {
    td {
      background: transparent;
      border-bottom: 1px solid #2c2d31;
      color: #000000;
    }

    tr:hover > td {
      background: rgba(255, 215, 0, 0.05);
    }
  }

  .empty-text {
    color: #8e8e93;
  }

  .action-button {
    color: #ffd700;

    &:hover {
      color: #ffed4e;
    }
  }
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;

  &:deep(.el-pagination) {
    .el-pager li {
      background: #2c2d31;
      color: #e5e5e5;
      border: 1px solid #4a4b4f;

      &.is-active {
        background: #ffd700;
        color: #111113;
        border-color: #ffd700;
      }

      &:hover {
        border-color: #ffd700;
      }
    }

    .btn-prev,
    .btn-next {
      background: #2c2d31;
      color: #e5e5e5;
      border: 1px solid #4a4b4f;

      &:hover {
        border-color: #ffd700;
      }
    }

    .el-pagination__total,
    .el-pagination__sizes,
    .el-pagination__jump {
      color: #8e8e93;
    }
  }
}

.api-dialog {
  &:deep(.el-dialog) {
    background: rgba(26, 27, 30, 0.95);
    border: 1px solid rgba(255, 215, 0, 0.3);
    backdrop-filter: blur(10px);
  }

  &:deep(.el-dialog__header) {
    .el-dialog__title {
      color: #ffd700;
      font-size: 20px;
      font-weight: bold;
    }

    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #8e8e93;

        &:hover {
          color: #ffd700;
        }
      }
    }
  }

  &:deep(.el-dialog__body) {
    .el-form-item__label {
      color: #e5e5e5;
    }

    .el-input__wrapper {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;

      &:hover {
        border-color: #ffd700;
      }

      &.is-focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }

    .el-textarea__inner {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;
      font-family: 'Courier New', monospace;
      font-size: 13px;

      &:hover {
        border-color: #ffd700;
      }

      &:focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }
  }

  .cancel-button {
    background: #2c2d31;
    border: 1px solid #4a4b4f;
    color: #e5e5e5;

    &:hover {
      background: #3a3b3f;
      border-color: #ffd700;
    }
  }
}

.debug-dialog {
  &:deep(.el-dialog) {
    background: rgba(26, 27, 30, 0.95);
    border: 1px solid rgba(255, 215, 0, 0.3);
    backdrop-filter: blur(10px);
  }

  &:deep(.el-dialog__header) {
    .el-dialog__title {
      color: #ffd700;
      font-size: 20px;
      font-weight: bold;
    }

    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #8e8e93;

        &:hover {
          color: #ffd700;
        }
      }
    }
  }

  &:deep(.el-dialog__body) {
    .el-form-item__label {
      color: #e5e5e5;
    }

    .el-input__wrapper {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;

      &:hover {
        border-color: #ffd700;
      }

      &.is-focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }

    .el-textarea__inner {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;
      font-family: 'Courier New', monospace;
      font-size: 13px;

      &:hover {
        border-color: #ffd700;
      }

      &:focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }
  }

  .debug-result {
    margin-top: 20px;
    padding: 16px;
    background: rgba(44, 45, 49, 0.6);
    border: 1px solid rgba(255, 215, 0, 0.2);
    border-radius: 8px;

    .result-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .result-title {
        font-size: 16px;
        font-weight: bold;
        color: #ffd700;
      }
    }

    .result-item {
      margin-bottom: 12px;

      .result-label {
        font-weight: bold;
        color: #e5e5e5;
        margin-right: 8px;
      }

      .result-value {
        color: #34c759;
        font-weight: bold;
      }

      .result-content {
        background: #1a1b1e;
        padding: 12px;
        border-radius: 4px;
        border: 1px solid #2c2d31;
        color: #e5e5e5;
        font-family: 'Courier New', monospace;
        font-size: 12px;
        max-height: 300px;
        overflow-y: auto;
        margin-top: 8px;

        &.error {
          color: #ff3b30;
          border-color: rgba(255, 59, 48, 0.3);
        }
      }
    }
  }
}
</style>