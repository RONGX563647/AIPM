<template>
  <div class="monitor-container">
    <div class="monitor-header">
      <h1 class="monitor-title">系统监控</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增监控
      </el-button>
    </div>

    <div class="monitor-content">
      <div class="stats-grid">
        <el-card class="stat-card total">
          <div class="stat-content">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总监控项</div>
          </div>
        </el-card>
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-value">{{ stats.warning }}</div>
            <div class="stat-label">警告</div>
          </div>
        </el-card>
        <el-card class="stat-card critical">
          <div class="stat-content">
            <div class="stat-value">{{ stats.critical }}</div>
            <div class="stat-label">严重</div>
          </div>
        </el-card>
        <el-card class="stat-card normal">
          <div class="stat-content">
            <div class="stat-value">{{ stats.normal }}</div>
            <div class="stat-label">正常</div>
          </div>
        </el-card>
      </div>

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
          <el-form-item label="监控状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
            >
              <el-option label="正常" value="normal" />
              <el-option label="警告" value="warning" />
              <el-option label="严重" value="critical" />
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
          class="monitor-table"
        >
          <el-table-column prop="metricName" label="监控指标" min-width="150" show-overflow-tooltip />
          <el-table-column prop="metricValue" label="指标值" width="120" />
          <el-table-column prop="threshold" label="阈值" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertMessage" label="告警信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="监控时间" width="180">
            <template #default="{ row }">
              <span v-if="row.createTime">{{ formatDate(row.createTime) }}</span>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
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
      title="新增监控"
      width="600px"
      class="monitor-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="关联项目" prop="projectId">
          <el-select v-model="formData.projectId" placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="监控指标" prop="metricName">
          <el-input v-model="formData.metricName" placeholder="请输入监控指标名称" />
        </el-form-item>
        <el-form-item label="指标值" prop="metricValue">
          <el-input-number v-model="formData.metricValue" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="告警阈值" prop="threshold">
          <el-input-number v-model="formData.threshold" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="gold-button">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Delete } from '@element-plus/icons-vue'
import { monitorApi } from '@/api/monitor.api'
import { projectApi } from '@/api/project.api'

const loading = ref(false)
const tableData = ref([])
const projects = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  projectId: null,
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  projectId: null,
  metricName: '',
  metricValue: 0,
  threshold: 0
})

const formRules = {
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  metricName: [
    { required: true, message: '请输入监控指标名称', trigger: 'blur' }
  ],
  metricValue: [
    { required: true, message: '请输入指标值', trigger: 'blur' }
  ],
  threshold: [
    { required: true, message: '请输入告警阈值', trigger: 'blur' }
  ]
}

const stats = computed(() => {
  const total = tableData.value.length
  const normal = tableData.value.filter(item => item.status === 'normal').length
  const warning = tableData.value.filter(item => item.status === 'warning').length
  const critical = tableData.value.filter(item => item.status === 'critical').length
  return { total, normal, warning, critical }
})

const getStatusType = (status) => {
  const typeMap = {
    normal: 'success',
    warning: 'warning',
    critical: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    normal: '正常',
    warning: '警告',
    critical: '严重'
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
    const res = await monitorApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: searchForm.projectId || undefined,
      status: searchForm.status || undefined
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取监控数据失败')
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
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该监控记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await monitorApi.delete(row.id)
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
    
    const res = await monitorApi.add(formData)
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
  formData.projectId = null
  formData.metricName = ''
  formData.metricValue = 0
  formData.threshold = 0
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
.monitor-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.monitor-title {
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

.monitor-content {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      background: rgba(26, 27, 30, 0.8);
      border: 1px solid rgba(255, 215, 0, 0.2);
      backdrop-filter: blur(10px);
      height: 120px;

      &:deep(.el-card__body) {
        padding: 0;
      }

      &.total {
        border-left: 4px solid #ffd700;
      }

      &.warning {
        border-left: 4px solid #ff9500;
      }

      &.critical {
        border-left: 4px solid #ff3b30;
      }

      &.normal {
        border-left: 4px solid #34c759;
      }

      .stat-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
      }

      .stat-value {
        font-size: 32px;
        font-weight: bold;
        color: #ffd700;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #8e8e93;
      }
    }
  }

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

.monitor-table {
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

.monitor-dialog {
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
