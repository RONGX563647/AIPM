<template>
  <div class="test-container">
    <div class="test-header">
      <h1 class="test-title">自动化接口测试</h1>
      <div class="header-actions">
        <el-select
          v-model="selectedApiId"
          placeholder="请选择接口"
          clearable
          class="api-select"
          @change="handleApiChange"
        >
          <el-option
            v-for="api in apiList"
            :key="api.id"
            :label="`${api.method} ${api.path}`"
            :value="api.id"
          />
        </el-select>
        <el-button type="primary" @click="handleAddCase" class="gold-button">
          <el-icon><Plus /></el-icon>
          新增用例
        </el-button>
      </div>
    </div>

    <div class="test-content">
      <el-card class="table-card">
        <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          class="test-table"
        >
          <el-table-column prop="caseName" label="用例名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="params" label="测试参数" min-width="200" show-overflow-tooltip />
          <el-table-column prop="expectedResult" label="预期结果" min-width="200" show-overflow-tooltip />
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
                @click="handleRunCase(row)"
                class="action-button"
                :loading="runningCaseId === row.id"
              >
                <el-icon><VideoPlay /></el-icon>
                执行
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleEditCase(row)"
                class="action-button"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDeleteCase(row)"
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

      <el-card class="report-card" v-if="showReport">
        <div class="report-header">
          <h2 class="report-title">测试报告</h2>
          <el-button type="primary" link @click="showReport = false" class="close-button">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <el-table
          :data="reportData"
          v-loading="reportLoading"
          border
          stripe
          class="report-table"
        >
          <el-table-column prop="caseId" label="用例ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small">
                {{ row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="testTime" label="测试时间" width="120">
            <template #default="{ row }">
              <span v-if="row.testTime">{{ formatDateTime(row.testTime) }}</span>
              <span v-else class="empty-text">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleViewReport(row)" class="action-button">
                <el-icon><View /></el-icon>
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-dialog
      v-model="caseDialogVisible"
      :title="caseDialogTitle"
      width="700px"
      class="case-dialog"
    >
      <el-form
        ref="caseFormRef"
        :model="caseFormData"
        :rules="caseFormRules"
        label-width="100px"
      >
        <el-form-item label="用例名称" prop="caseName">
          <el-input v-model="caseFormData.caseName" placeholder="请输入用例名称" />
        </el-form-item>
        <el-form-item label="测试参数" prop="params">
          <el-input
            v-model="caseFormData.params"
            type="textarea"
            :rows="6"
            placeholder='请输入测试参数（JSON格式）'
          />
        </el-form-item>
        <el-form-item label="预期结果" prop="expectedResult">
          <el-input
            v-model="caseFormData.expectedResult"
            type="textarea"
            :rows="6"
            placeholder='请输入预期结果（JSON格式）'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="caseDialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmitCase" class="gold-button">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="reportDetailVisible"
      title="报告详情"
      width="900px"
      class="report-detail-dialog"
    >
      <div v-if="currentReport" class="report-detail">
        <div class="detail-item">
          <span class="detail-label">用例ID：</span>
          <span class="detail-value">{{ currentReport.caseId }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">测试状态：</span>
          <el-tag :type="currentReport.status === 'success' ? 'success' : 'danger'" size="small">
            {{ currentReport.status === 'success' ? '成功' : '失败' }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="detail-label">测试时间：</span>
          <span class="detail-value">{{ formatDateTime(currentReport.testTime) }}</span>
        </div>
        <div class="detail-section">
          <h3 class="section-title">预期结果</h3>
          <pre class="result-content">{{ currentReport.expectedResult }}</pre>
        </div>
        <div class="detail-section">
          <h3 class="section-title">实际结果</h3>
          <pre class="result-content">{{ currentReport.actualResult }}</pre>
        </div>
        <div v-if="currentReport.remark" class="detail-section">
          <h3 class="section-title">失败备注</h3>
          <pre class="result-content error">{{ currentReport.remark }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Delete, VideoPlay, Close, View } from '@element-plus/icons-vue'
import { testApi } from '@/api/test.api'
import { apiInfoApi } from '@/api/api-info.api'

const loading = ref(false)
const reportLoading = ref(false)
const tableData = ref([])
const reportData = ref([])
const apiList = ref([])
const showReport = ref(false)
const caseDialogVisible = ref(false)
const reportDetailVisible = ref(false)
const caseDialogTitle = ref('新增测试用例')
const caseFormRef = ref(null)
const runningCaseId = ref(null)
const currentReport = ref(null)

const selectedApiId = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const caseFormData = reactive({
  id: null,
  apiId: null,
  caseName: '',
  params: '',
  expectedResult: ''
})

const caseFormRules = {
  caseName: [
    { required: true, message: '请输入用例名称', trigger: 'blur' }
  ],
  params: [
    { required: true, message: '请输入测试参数', trigger: 'blur' }
  ],
  expectedResult: [
    { required: true, message: '请输入预期结果', trigger: 'blur' }
  ]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const fetchApiList = async () => {
  try {
    const res = await apiInfoApi.page({
      pageNum: 1,
      pageSize: 1000
    })
    if (res.code === 0) {
      apiList.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取接口列表失败')
  }
}

const fetchCaseData = async () => {
  loading.value = true
  try {
    const res = await testApi.getCasePage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      apiId: selectedApiId.value
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取测试用例列表失败')
  } finally {
    loading.value = false
  }
}

const fetchReportData = async () => {
  reportLoading.value = true
  try {
    const res = await testApi.getReportPage({
      pageNum: 1,
      pageSize: 100,
      apiId: selectedApiId.value
    })
    if (res.code === 0) {
      reportData.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取测试报告失败')
  } finally {
    reportLoading.value = false
  }
}

const handleApiChange = () => {
  pagination.pageNum = 1
  fetchCaseData()
  fetchReportData()
}

const handleAddCase = () => {
  if (!selectedApiId.value) {
    ElMessage.warning('请先选择接口')
    return
  }
  caseDialogTitle.value = '新增测试用例'
  resetCaseForm()
  caseDialogVisible.value = true
}

const handleEditCase = (row) => {
  caseDialogTitle.value = '编辑测试用例'
  Object.assign(caseFormData, row)
  caseDialogVisible.value = true
}

const handleDeleteCase = (row) => {
  ElMessageBox.confirm('确定要删除该测试用例吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await testApi.deleteCase(row.id)
      ElMessage.success('删除成功')
      fetchCaseData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleRunCase = async (row) => {
  runningCaseId.value = row.id
  try {
    const res = await testApi.runCase(row.id)
    if (res.code === 0) {
      ElMessage.success('执行成功')
      showReport.value = true
      fetchReportData()
    } else {
      ElMessage.error(res.data.error || '执行失败')
    }
  } catch (error) {
    ElMessage.error('执行失败')
  } finally {
    runningCaseId.value = null
  }
}

const handleViewReport = (row) => {
  currentReport.value = row
  reportDetailVisible.value = true
}

const handleSubmitCase = async () => {
  if (!caseFormRef.value) return
  
  try {
    await caseFormRef.value.validate()
    
    if (caseFormData.params) {
      try {
        JSON.parse(caseFormData.params)
      } catch (e) {
        ElMessage.error('测试参数格式不正确，请输入有效的JSON格式')
        return
      }
    }
    
    if (caseFormData.expectedResult) {
      try {
        JSON.parse(caseFormData.expectedResult)
      } catch (e) {
        ElMessage.error('预期结果格式不正确，请输入有效的JSON格式')
        return
      }
    }
    
    if (caseFormData.id) {
      await testApi.updateCase(caseFormData)
      ElMessage.success('更新成功')
    } else {
      caseFormData.apiId = selectedApiId.value
      await testApi.addCase(caseFormData)
      ElMessage.success('新增成功')
    }
    
    caseDialogVisible.value = false
    fetchCaseData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(caseFormData.id ? '更新失败' : '新增失败')
    }
  }
}

const resetCaseForm = () => {
  caseFormData.id = null
  caseFormData.apiId = null
  caseFormData.caseName = ''
  caseFormData.params = ''
  caseFormData.expectedResult = ''
  caseFormRef.value?.clearValidate()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchCaseData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchCaseData()
}

onMounted(() => {
  fetchApiList()
})
</script>

<style scoped lang="scss">
.test-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.test-title {
  font-size: 28px;
  font-weight: bold;
  color: #ffd700;
  margin: 0;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.api-select {
  width: 300px;

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

.test-content {
  .table-card,
  .report-card {
    background: rgba(26, 27, 30, 0.8);
    border: 1px solid rgba(255, 215, 0, 0.2);
    backdrop-filter: blur(10px);
    margin-bottom: 24px;

    &:deep(.el-card__body) {
      padding: 24px;
    }
  }

  .report-card {
    .report-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .report-title {
        font-size: 20px;
        font-weight: bold;
        color: #ffd700;
        margin: 0;
      }

      .close-button {
        color: #8e8e93;

        &:hover {
          color: #ffd700;
        }
      }
    }
  }
}

.test-table,
.report-table {
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

.case-dialog,
.report-detail-dialog {
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

.report-detail {
  .detail-item {
    display: flex;
    align-items: center;
    margin-bottom: 16px;

    .detail-label {
      font-weight: bold;
      color: #e5e5e5;
      margin-right: 8px;
      min-width: 100px;
    }

    .detail-value {
      color: #e5e5e5;
    }
  }

  .detail-section {
    margin-bottom: 24px;

    .section-title {
      font-size: 16px;
      font-weight: bold;
      color: #ffd700;
      margin-bottom: 12px;
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
</style>
