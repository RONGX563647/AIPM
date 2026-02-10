<template>
  <div class="deploy-container">
    <div class="deploy-header">
      <h1 class="deploy-title">部署记录</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增部署
      </el-button>
    </div>

    <div class="deploy-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="关联项目">
            <el-select
              v-model="searchForm.projectId"
              placeholder="请选择项目"
              clearable
              class="search-select"
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.name"
                :value="project.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="部署环境">
            <el-select
              v-model="searchForm.env"
              placeholder="请选择环境"
              clearable
              class="search-select"
            >
              <el-option label="开发" value="dev" />
              <el-option label="测试" value="test" />
              <el-option label="生产" value="prod" />
            </el-select>
          </el-form-item>
          <el-form-item label="部署状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
            >
              <el-option label="成功" value="success" />
              <el-option label="失败" value="fail" />
            </el-select>
          </el-form-item>
          <el-form-item label="版本号">
            <el-input
              v-model="searchForm.version"
              placeholder="请输入版本号"
              clearable
              class="search-input"
            />
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
          class="deploy-table"
        >
          <el-table-column prop="version" label="版本号" min-width="120" show-overflow-tooltip />
          <el-table-column prop="env" label="部署环境" width="100">
            <template #default="{ row }">
              <el-tag :type="getEnvType(row.env)" size="small">
                {{ getEnvText(row.env) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="部署状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="部署内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="部署时间" width="180">
            <template #default="{ row }">
              <span v-if="row.createTime">{{ formatDate(row.createTime) }}</span>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                @click="handleView(row)"
                class="action-button"
              >
                <el-icon><View /></el-icon>
                查看详情
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
      width="600px"
      class="deploy-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="关联项目" prop="projectId">
          <el-select v-model="formData.projectId" placeholder="请选择项目" style="width: 100%" :disabled="isView">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="formData.version" placeholder="请输入版本号" :disabled="isView" />
        </el-form-item>
        <el-form-item label="部署环境" prop="env">
          <el-select v-model="formData.env" placeholder="请选择环境" style="width: 100%" :disabled="isView">
            <el-option label="开发" value="dev" />
            <el-option label="测试" value="test" />
            <el-option label="生产" value="prod" />
          </el-select>
        </el-form-item>
        <el-form-item label="部署状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%" :disabled="isView">
            <el-option label="成功" value="success" />
            <el-option label="失败" value="fail" />
          </el-select>
        </el-form-item>
        <el-form-item label="部署内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入部署内容"
            :disabled="isView"
          />
        </el-form-item>
      </el-form>
      <template #footer v-if="!isView">
        <el-button @click="dialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="gold-button">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, View, Delete } from '@element-plus/icons-vue'
import { deployApi } from '@/api/deploy.api'
import { projectApi } from '@/api/project.api'

const loading = ref(false)
const tableData = ref([])
const projects = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)

const searchForm = reactive({
  projectId: null,
  env: '',
  status: '',
  version: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  projectId: null,
  version: '',
  env: '',
  status: '',
  content: ''
})

const formRules = {
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' }
  ],
  env: [
    { required: true, message: '请选择部署环境', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择部署状态', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  return isView.value ? '查看详情' : '新增部署'
})

const getEnvType = (env) => {
  const typeMap = {
    dev: 'info',
    test: 'warning',
    prod: 'danger'
  }
  return typeMap[env] || 'info'
}

const getEnvText = (env) => {
  const textMap = {
    dev: '开发',
    test: '测试',
    prod: '生产'
  }
  return textMap[env] || env
}

const getStatusType = (status) => {
  const typeMap = {
    success: 'success',
    fail: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    success: '成功',
    fail: '失败'
  }
  return textMap[status] || status
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const fetchProjects = async () => {
  try {
    const res = await projectApi.page({ pageNum: 1, pageSize: 100 })
    if (res.code === 0) {
      projects.value = res.data.records
    }
  } catch (error) {
    console.error('获取项目列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await deployApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: searchForm.projectId || undefined,
      env: searchForm.env || undefined,
      status: searchForm.status || undefined,
      version: searchForm.version || undefined
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取部署记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.projectId = null
  searchForm.env = ''
  searchForm.status = ''
  searchForm.version = ''
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  dialogVisible.value = true
}

const handleView = (row) => {
  formData.id = row.id
  formData.projectId = row.projectId
  formData.version = row.version
  formData.env = row.env
  formData.status = row.status
  formData.content = row.content
  isView.value = true
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该部署记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await deployApi.delete(row.id)
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
    
    const res = await deployApi.add(formData)
    if (res.code === 0) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('新增失败')
    }
  }
}

const resetForm = () => {
  formData.id = null
  formData.projectId = null
  formData.version = ''
  formData.env = ''
  formData.status = ''
  formData.content = ''
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
  fetchProjects()
  fetchData()
})
</script>

<style scoped lang="scss">
.deploy-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.deploy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.deploy-title {
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

.deploy-content {
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

  .search-select,
  .search-input {
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

.deploy-table {
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
      color: #e5e5e5;
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

.deploy-dialog {
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

    .el-input__wrapper,
    .el-textarea__inner {
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
</style>
