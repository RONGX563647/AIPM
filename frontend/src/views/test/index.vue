<template>
  <div class="test-container">
    <div class="test-header">
      <h1 class="test-title">自动化接口测试</h1>
      <div class="header-actions">
        <el-select
          v-model="selectedApiId"
          placeholder="请选择接口"
          class="api-select"
          clearable
          @change="handleApiChange"
        >
          <el-option
            v-for="api in apiList"
            :key="api.id"
            :label="`${api.path} (${api.method})`"
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
      <!-- 用例管理 -->
      <el-card class="case-card">
        <template #header>
          <div class="card-header">
            <span>测试用例管理</span>
            <span class="case-count">共 {{ caseList.length }} 个用例</span>
          </div>
        </template>

        <el-table
          :data="caseList"
          v-loading="loading"
          border
          stripe
          class="case-table"
        >
          <el-table-column prop="caseName" label="用例名称" min-width="150" />
          <el-table-column prop="params" label="测试参数" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="formatJson(row.params)" placement="top">
                <div class="json-preview">{{ truncateJson(row.params) }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="expectedResult" label="预期结果" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="formatJson(row.expectedResult)" placement="top">
                <div class="json-preview">{{ truncateJson(row.expectedResult) }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
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
                type="success"
                link
                @click="handleRunCase(row)"
                class="action-button"
              >
                <el-icon><Check /></el-icon>
                执行
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
      </el-card>

      <!-- 测试报告 -->
      <el-card class="report-card">
        <template #header>
          <div class="card-header">
            <span>测试报告</span>
            <span class="report-count">共 {{ reportList.length }} 条报告</span>
          </div>
        </template>

        <el-table
          :data="reportList"
          v-loading="reportLoading"
          border
          stripe
          class="report-table"
        >
          <el-table-column prop="caseId" label="用例ID" width="80" />
          <el-table-column prop="status" label="测试状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
                {{ row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="testTime" label="测试时间" width="180" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                @click="handleViewReport(row)"
                class="action-button"
              >
                <el-icon><View /></el-icon>
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 新增/编辑用例弹窗 -->
    <el-dialog
      v-model="caseDialogVisible"
      :title="caseDialogTitle"
      width="700px"
      class="test-dialog"
    >
      <el-form
        ref="caseFormRef"
        :model="caseForm"
        :rules="caseRules"
        label-width="100px"
      >
        <el-form-item label="用例名称" prop="caseName">
          <el-input
            v-model="caseForm.caseName"
            placeholder="请输入用例名称"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="测试参数" prop="params">
          <el-input
            v-model="caseForm.params"
            type="textarea"
            :rows="4"
            placeholder="请输入JSON格式的测试参数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预期结果" prop="expectedResult">
          <el-input
            v-model="caseForm.expectedResult"
            type="textarea"
            :rows="4"
            placeholder="请输入JSON格式的预期结果"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="caseDialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmitCase" class="gold-button">确定</el-button>
      </template>
    </el-dialog>

    <!-- 执行用例弹窗 -->
    <el-dialog
      v-model="runDialogVisible"
      title="执行测试用例"
      width="500px"
      class="test-dialog"
    >
      <div class="run-content" v-loading="runLoading">
        <div class="run-status" v-if="runStatus">
          <el-icon :class="['status-icon', runStatus === 'success' ? 'success' : 'danger']">
            <component :is="runStatus === 'success' ? Check : Close" />
          </el-icon>
          <div class="status-text">{{ runStatus === 'success' ? '测试成功' : '测试失败' }}</div>
        </div>
        <div class="run-result" v-if="runResult">
          <h4 class="result-title">测试结果</h4>
          <div class="result-item">
            <span class="result-label">实际结果：</span>
            <el-tooltip :content="formatJson(runResult.actualResult)" placement="top">
              <div class="json-preview">{{ truncateJson(runResult.actualResult) }}</div>
            </el-tooltip>
          </div>
          <div class="result-item" v-if="runResult.remark">
            <span class="result-label">失败原因：</span>
            <span class="result-text">{{ runResult.remark }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="runDialogVisible = false" class="gold-button">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 报告详情弹窗 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="报告详情"
      width="800px"
      class="test-dialog"
    >
      <div class="report-detail" v-if="selectedReport">
        <div class="report-header">
          <div class="report-info">
            <span class="info-item">
              <strong>用例ID：</strong>{{ selectedReport.caseId }}
            </span>
            <span class="info-item">
              <strong>测试时间：</strong>{{ selectedReport.testTime }}
            </span>
            <span class="info-item">
              <strong>测试状态：</strong>
              <el-tag :type="selectedReport.status === 'success' ? 'success' : 'danger'">
                {{ selectedReport.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </span>
          </div>
        </div>

        <div class="result-comparison">
          <div class="comparison-item">
            <h4 class="comparison-title">预期结果</h4>
            <pre class="json-code">{{ formatJson(selectedReport.expectedResult) }}</pre>
          </div>
          <div class="comparison-item">
            <h4 class="comparison-title">实际结果</h4>
            <pre class="json-code">{{ formatJson(selectedReport.actualResult) }}</pre>
          </div>
        </div>

        <div class="report-remark" v-if="selectedReport.remark">
          <h4 class="remark-title">失败原因</h4>
          <p class="remark-text">{{ selectedReport.remark }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="reportDialogVisible = false" class="gold-button">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, Close, View } from '@element-plus/icons-vue'
import { testApi } from '@/api/test.api'
import { apiInfoApi } from '@/api/api-info.api'

const loading = ref(false)
const reportLoading = ref(false)
const runLoading = ref(false)
const selectedApiId = ref('')
const caseList = ref([])
const reportList = ref([])
const apiList = ref([])

const caseDialogVisible = ref(false)
const caseDialogTitle = ref('新增用例')
const caseFormRef = ref(null)
const caseForm = reactive({
  id: null,
  apiId: '',
  caseName: '',
  params: '',
  expectedResult: ''
})

const runDialogVisible = ref(false)
const runStatus = ref('')
const runResult = ref(null)

const reportDialogVisible = ref(false)
const selectedReport = ref(null)

const caseRules = {
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

const handleApiChange = () => {
  fetchCaseList()
  fetchReportList()
}

const handleAddCase = () => {
  if (!selectedApiId.value) {
    ElMessage.warning('请先选择接口')
    return
  }
  caseDialogTitle.value = '新增用例'
  resetCaseForm()
  caseForm.apiId = selectedApiId.value
  caseDialogVisible.value = true
}

const handleEditCase = (row) => {
  caseDialogTitle.value = '编辑用例'
  Object.assign(caseForm, row)
  caseDialogVisible.value = true
}

const handleDeleteCase = (row) => {
  ElMessageBox.confirm('确定要删除该测试用例吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await testApi.deleteCase(row.id)
      ElMessage.success('删除成功')
      fetchCaseList()
    } catch (error) {
      ElMessage.error('删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

const handleRunCase = async (row) => {
  runLoading.value = true
  runStatus.value = ''
  runResult.value = null
  runDialogVisible.value = true

  try {
    const res = await testApi.runCase(row.id)
    if (res.code === 200) {
      runStatus.value = res.data.status
      runResult.value = res.data
      ElMessage.success('测试用例执行成功')
      fetchReportList()
    }
  } catch (error) {
    ElMessage.error('测试用例执行失败')
  } finally {
    runLoading.value = false
  }
}

const handleViewReport = async (row) => {
  try {
    const res = await testApi.getReport(row.id)
    if (res.code === 200) {
      // 获取用例信息，包含预期结果
      const caseRes = await testApi.getCase(row.caseId)
      if (caseRes.code === 200) {
        selectedReport.value = {
          ...res.data,
          expectedResult: caseRes.data.expectedResult
        }
        reportDialogVisible.value = true
      }
    }
  } catch (error) {
    ElMessage.error('获取报告详情失败')
  }
}

const handleSubmitCase = async () => {
  if (!caseFormRef.value) return

  try {
    await caseFormRef.value.validate()

    if (caseForm.id) {
      await testApi.updateCase(caseForm)
      ElMessage.success('更新用例成功')
    } else {
      await testApi.addCase(caseForm)
      ElMessage.success('新增用例成功')
    }

    caseDialogVisible.value = false
    fetchCaseList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(caseForm.id ? '更新用例失败' : '新增用例失败')
    }
  }
}

const resetCaseForm = () => {
  caseForm.id = null
  caseForm.caseName = ''
  caseForm.params = ''
  caseForm.expectedResult = ''
  caseFormRef.value?.clearValidate()
}

const fetchApiList = async () => {
  try {
    const res = await apiInfoApi.page({ pageSize: 100 })
    if (res.code === 200) {
      apiList.value = res.data.records
    }
  } catch (error) {
    console.error('获取接口列表失败', error)
  }
}

const fetchCaseList = async () => {
  loading.value = true
  try {
    const res = await testApi.getCasePage({
      apiId: selectedApiId.value || undefined
    })
    if (res.code === 200) {
      caseList.value = res.data.records
    }
  } catch (error) {
    console.error('获取用例列表失败', error)
  } finally {
    loading.value = false
  }
}

const fetchReportList = async () => {
  reportLoading.value = true
  try {
    const res = await testApi.getReportPage({
      apiId: selectedApiId.value || undefined
    })
    if (res.code === 200) {
      reportList.value = res.data.records
    }
  } catch (error) {
    console.error('获取报告列表失败', error)
  } finally {
    reportLoading.value = false
  }
}

const formatJson = (jsonString) => {
  try {
    const obj = JSON.parse(jsonString)
    return JSON.stringify(obj, null, 2)
  } catch {
    return jsonString
  }
}

const truncateJson = (jsonString) => {
  try {
    const obj = JSON.parse(jsonString)
    const str = JSON.stringify(obj)
    return str.length > 100 ? str.substring(0, 100) + '...' : str
  } catch {
    return jsonString.length > 100 ? jsonString.substring(0, 100) + '...' : jsonString
  }
}

onMounted(() => {
  fetchApiList()
  fetchCaseList()
  fetchReportList()
})
</script>

<style scoped lang="scss">
.test-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1A1B1E 100%);
  color: #E5E5E5;
  padding: 32px;
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .test-title {
    font-size: 28px;
    font-weight: bold;
    color: #FFD700;
    background: linear-gradient(135deg, #FFD700 0%, #FFED4E 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
  }

  .header-actions {
    display: flex;
    gap: 12px;
    align-items: center;

    .api-select {
      width: 300px;

      &:deep(.el-input__wrapper) {
        background: #2C2D31;
        border: 1px solid #4A4B4F;
        color: #E5E5E5;

        &:hover {
          border-color: #FFD700;
        }

        &.is-focus {
          border-color: #FFD700;
          box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
        }
      }
    }

    .gold-button {
      background: linear-gradient(135deg, #FFD700 0%, #B38B2D 100%);
      border: none;
      color: #111113;
      font-weight: bold;

      &:hover {
        background: linear-gradient(135deg, #FFED4E 0%, #FFD700 100%);
        box-shadow: 0 4px 16px rgba(255, 215, 0, 0.4);
      }
    }
  }
}

.test-content {
  .case-card,
  .report-card {
    background: rgba(26, 27, 30, 0.8);
    border: 1px solid rgba(255, 215, 0, 0.2);
    backdrop-filter: blur(10px);
    margin-bottom: 24px;

    &:deep(.el-card__header) {
      background: rgba(44, 45, 49, 0.6);
      border-bottom: 1px solid rgba(255, 215, 0, 0.1);
      color: #E5E5E5;
    }

    &:deep(.el-card__body) {
      padding: 24px;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .case-count,
    .report-count {
      font-size: 14px;
      color: #8E8E93;
    }
  }

  .case-table,
  .report-table {
    background: transparent;

    &:deep(.el-table__header-wrapper) {
      th {
        background: #1A1B1E;
        color: #FFD700;
        font-weight: bold;
        border-bottom: 1px solid rgba(255, 215, 0, 0.3);
      }
    }

    &:deep(.el-table__body-wrapper) {
      td {
        background: transparent;
        border-bottom: 1px solid #2C2D31;
        color: #E5E5E5;
      }

      tr:hover > td {
        background: rgba(255, 215, 0, 0.05);
      }
    }

    .action-button {
      color: #FFD700;

      &:hover {
        color: #FFED4E;
      }
    }
  }

  .json-preview {
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    font-size: 12px;
    color: #8E8E93;
    word-break: break-all;
  }
}

.test-dialog {
  &:deep(.el-dialog) {
    background: rgba(26, 27, 30, 0.95);
    border: 1px solid rgba(255, 215, 0, 0.3);
    backdrop-filter: blur(10px);
  }

  &:deep(.el-dialog__header) {
    .el-dialog__title {
      color: #FFD700;
      font-size: 20px;
      font-weight: bold;
    }

    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #8E8E93;

        &:hover {
          color: #FFD700;
        }
      }
    }
  }

  &:deep(.el-dialog__body) {
    .el-form-item__label {
      color: #E5E5E5;
    }

    .el-input__wrapper,
    .el-textarea__inner {
      background: #2C2D31;
      border: 1px solid #4A4B4F;
      color: #E5E5E5;

      &:hover {
        border-color: #FFD700;
      }

      &.is-focus {
        border-color: #FFD700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }
  }

  .cancel-button {
    background: #2C2D31;
    border: 1px solid #4A4B4F;
    color: #E5E5E5;

    &:hover {
      background: #3A3B3F;
      border-color: #FFD700;
    }
  }

  .gold-button {
    background: linear-gradient(135deg, #FFD700 0%, #B38B2D 100%);
    border: none;
    color: #111113;
    font-weight: bold;

    &:hover {
      background: linear-gradient(135deg, #FFED4E 0%, #FFD700 100%);
      box-shadow: 0 4px 16px rgba(255, 215, 0, 0.4);
    }
  }
}

.run-content {
  padding: 20px 0;

  .run-status {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    padding: 16px;
    background: rgba(44, 45, 49, 0.6);
    border-radius: 8px;

    .status-icon {
      font-size: 32px;

      &.success {
        color: #34C759;
      }

      &.danger {
        color: #FF3B30;
      }
    }

    .status-text {
      font-size: 18px;
      font-weight: bold;
    }
  }

  .run-result {
    .result-title {
      font-size: 16px;
      font-weight: bold;
      color: #FFD700;
      margin-bottom: 16px;
    }

    .result-item {
      margin-bottom: 12px;
      padding: 8px 0;

      .result-label {
        font-weight: bold;
        color: #8E8E93;
        margin-right: 8px;
      }

      .result-text {
        color: #E5E5E5;
      }

      .json-preview {
        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        font-size: 12px;
        color: #8E8E93;
        word-break: break-all;
      }
    }
  }
}

.report-detail {
  .report-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(255, 215, 0, 0.1);

    .report-info {
      display: flex;
      gap: 24px;

      .info-item {
        color: #E5E5E5;

        strong {
          color: #FFD700;
        }
      }
    }
  }

  .result-comparison {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 24px;

    .comparison-item {
      background: rgba(44, 45, 49, 0.6);
      border-radius: 8px;
      padding: 16px;
      border: 1px solid rgba(255, 215, 0, 0.1);

      .comparison-title {
        font-size: 14px;
        font-weight: bold;
        color: #FFD700;
        margin-bottom: 12px;
      }

      .json-code {
        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        font-size: 12px;
        color: #8E8E93;
        background: #1A1B1E;
        padding: 12px;
        border-radius: 4px;
        border: 1px solid rgba(255, 215, 0, 0.1);
        max-height: 300px;
        overflow-y: auto;
        white-space: pre-wrap;
      }
    }
  }

  .report-remark {
    background: rgba(44, 45, 49, 0.6);
    border-radius: 8px;
    padding: 16px;
    border: 1px solid rgba(255, 215, 0, 0.1);

    .remark-title {
      font-size: 14px;
      font-weight: bold;
      color: #FFD700;
      margin-bottom: 12px;
    }

    .remark-text {
      color: #E5E5E5;
      line-height: 1.5;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-container {
    padding: 16px;
  }

  .test-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;

    .header-actions {
      flex-direction: column;
      align-items: stretch;

      .api-select {
        width: 100%;
      }
    }
  }

  .test-content {
    .result-comparison {
      grid-template-columns: 1fr;
    }
  }
}
</style>