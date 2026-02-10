<template>
  <div class="monitoring-container">
    <div class="monitoring-header">
      <h1 class="monitoring-title">系统监控</h1>
      <el-button type="primary" @click="refreshData" class="gold-button">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <div class="monitoring-content">
      <div class="stats-grid">
        <el-card class="stat-card normal">
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
        <el-card class="stat-card danger">
          <div class="stat-content">
            <div class="stat-value">{{ stats.danger }}</div>
            <div class="stat-label">危险</div>
          </div>
        </el-card>
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-value">{{ stats.normal }}</div>
            <div class="stat-label">正常</div>
          </div>
        </el-card>
      </div>

      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="监控指标">
            <el-input
              v-model="searchForm.metric"
              placeholder="请输入监控指标"
              clearable
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
            >
              <el-option label="正常" value="normal" />
              <el-option label="警告" value="warning" />
              <el-option label="危险" value="danger" />
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
          class="monitoring-table"
        >
          <el-table-column prop="metric" label="监控指标" min-width="150" />
          <el-table-column prop="value" label="当前值" width="120" />
          <el-table-column prop="threshold" label="阈值" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="timestamp" label="时间戳" width="200" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search, RefreshLeft } from '@element-plus/icons-vue'
import { monitoringApi } from '@/api/monitoring.api'

const loading = ref(false)
const tableData = ref([])

const stats = reactive({
  total: 0,
  warning: 0,
  danger: 0,
  normal: 0
})

const searchForm = reactive({
  metric: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const typeMap = {
    normal: 'success',
    warning: 'warning',
    danger: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    normal: '正常',
    warning: '警告',
    danger: '危险'
  }
  return textMap[status] || status
}

const fetchStats = async () => {
  try {
    const res = await monitoringApi.getStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取监控统计失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await monitoringApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      metric: searchForm.metric || undefined,
      status: searchForm.status || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取监控数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = async () => {
  await fetchStats()
  await fetchData()
  ElMessage.success('数据刷新成功')
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.metric = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
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
  fetchStats()
  fetchData()
})
</script>

<style scoped lang="scss">
.monitoring-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.monitoring-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.monitoring-title {
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

    &.normal {
      border-left: 4px solid #34c759;
    }

    &.warning {
      border-left: 4px solid #ff9500;
    }

    &.danger {
      border-left: 4px solid #ff3b30;
    }

    &.success {
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

.monitoring-content {
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

.monitoring-table {
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
</style>