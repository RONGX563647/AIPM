<template>
  <div class="data-center-container">
    <div class="data-center-header">
      <h1 class="data-center-title">数据中心</h1>
      <el-button type="primary" @click="refreshData" class="gold-button">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </div>

    <div class="data-center-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="指标名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入指标名称"
              clearable
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="指标类别">
            <el-select
              v-model="searchForm.category"
              placeholder="请选择类别"
              clearable
              class="search-select"
            >
              <el-option label="系统" value="system" />
              <el-option label="业务" value="business" />
              <el-option label="性能" value="performance" />
              <el-option label="其他" value="other" />
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

      <div class="category-stats">
        <h2 class="section-title">类别统计</h2>
        <el-table
          :data="categoryStats"
          v-loading="loadingStats"
          border
          stripe
          class="stats-table"
        >
          <el-table-column prop="category" label="类别" width="120" />
          <el-table-column prop="count" label="指标数" width="100" />
          <el-table-column prop="avgValue" label="平均值" width="120" />
          <el-table-column prop="maxValue" label="最大值" width="120" />
          <el-table-column prop="minValue" label="最小值" width="120" />
        </el-table>
      </div>

      <div class="metrics-section">
        <h2 class="section-title">指标列表</h2>
        <el-card class="table-card">
          <el-table
            :data="tableData"
            v-loading="loadingMetrics"
            border
            stripe
            class="metrics-table"
          >
            <el-table-column prop="name" label="指标名称" min-width="150" />
            <el-table-column prop="value" label="数值" width="120" />
            <el-table-column prop="unit" label="单位" width="100" />
            <el-table-column prop="category" label="类别" width="120" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search, RefreshLeft } from '@element-plus/icons-vue'
import { dataCenterApi } from '@/api/data-center.api'

const loadingStats = ref(false)
const loadingMetrics = ref(false)
const tableData = ref([])
const categoryStats = ref([])

const searchForm = reactive({
  name: '',
  category: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const fetchCategoryStats = async () => {
  loadingStats.value = true
  try {
    const res = await dataCenterApi.getCategoryStats()
    if (res.code === 0) {
      categoryStats.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取类别统计失败')
  } finally {
    loadingStats.value = false
  }
}

const fetchMetrics = async () => {
  loadingMetrics.value = true
  try {
    const res = await dataCenterApi.getMetrics({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      category: searchForm.category || undefined,
      name: searchForm.name || undefined
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取指标数据失败')
  } finally {
    loadingMetrics.value = false
  }
}

const refreshData = async () => {
  await fetchCategoryStats()
  await fetchMetrics()
  ElMessage.success('数据刷新成功')
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchMetrics()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.category = ''
  pagination.pageNum = 1
  fetchMetrics()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchMetrics()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchMetrics()
}

onMounted(() => {
  fetchCategoryStats()
  fetchMetrics()
})
</script>

<style scoped lang="scss">
.data-center-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.data-center-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.data-center-title {
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

.data-center-content {
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

  .section-title {
    font-size: 20px;
    font-weight: bold;
    color: #ffd700;
    margin-bottom: 16px;
  }

  .category-stats {
    margin-bottom: 32px;
  }

  .stats-table {
    background: transparent;

    &:deep(.el-table__header-wrapper) {
      th {
        background: #1a1b1e;
        color: #ffd700;
        font-weight: bold;
        border-bottom: 1px solid rgba(255, 215, 0, 0.3);
        font-family: 'SimHei', '黑体', sans-serif;
      }
    }

    &:deep(.el-table__body-wrapper) {
      td {
        background: transparent;
        border-bottom: 1px solid #2c2d31;
        color: #000000;
        font-family: 'SimHei', '黑体', sans-serif;
      }

      tr:hover > td {
        background: rgba(255, 215, 0, 0.05);
      }
    }
  }

  .metrics-section {
    .metrics-table {
      background: transparent;

      &:deep(.el-table__header-wrapper) {
        th {
          background: #1a1b1e;
          color: #ffd700;
          font-weight: bold;
          border-bottom: 1px solid rgba(255, 215, 0, 0.3);
          font-family: 'SimHei', '黑体', sans-serif;
        }
      }

      &:deep(.el-table__body-wrapper) {
        td {
          background: transparent;
          border-bottom: 1px solid #2c2d31;
          color: #000000;
          font-family: 'SimHei', '黑体', sans-serif;
        }

        tr:hover > td {
          background: rgba(255, 215, 0, 0.05);
        }
      }
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