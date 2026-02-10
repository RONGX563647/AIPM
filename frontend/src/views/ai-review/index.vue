<template>
  <div class="ai-review-container">
    <div class="ai-review-header">
      <h1 class="ai-review-title">AI代码评审</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增评审
      </el-button>
    </div>

    <div class="ai-review-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="关联项目">
            <el-select
              v-model="searchForm.projectId"
              placeholder="请选择项目"
              clearable
              class="search-select"
              @change="handleProjectChange"
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.name"
                :value="project.id"
              />
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
          class="ai-review-table"
        >
          <el-table-column prop="codeContent" label="代码内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="score" label="评审分数" width="120">
            <template #default="{ row }">
              <el-tag :type="getScoreType(row.score)" size="large">
                {{ row.score }}分
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="suggestion" label="优化建议" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="评审时间" width="180">
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
      width="900px"
      class="ai-review-dialog"
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
        <el-form-item label="代码内容" prop="codeContent">
          <el-input
            v-model="formData.codeContent"
            type="textarea"
            :rows="12"
            placeholder="请输入待评审的代码内容"
            class="code-editor"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="gold-button" :loading="submitting">
          <el-icon><VideoPlay /></el-icon>
          提交评审
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="resultDialogVisible"
      title="评审结果"
      width="900px"
      class="result-dialog"
    >
      <div v-if="reviewResult" class="result-content">
        <div class="result-section">
          <div class="result-section-title">
            <el-icon><Trophy /></el-icon>
            评审分数
          </div>
          <div class="result-section-content">
            <el-tag :type="getScoreType(reviewResult.score)" size="large" class="score-tag">
              {{ reviewResult.score }}分
            </el-tag>
          </div>
        </div>

        <div class="result-section">
          <div class="result-section-title">
            <el-icon><ChatLineSquare /></el-icon>
            优化建议
          </div>
          <div class="result-section-content">
            <pre class="result-text">{{ reviewResult.suggestion }}</pre>
          </div>
        </div>

        <div class="result-section">
          <div class="result-section-title">
            <el-icon><DocumentCopy /></el-icon>
            优化后的代码
            <el-button
              type="primary"
              link
              @click="handleCopyCode"
              class="copy-button"
            >
              <el-icon><CopyDocument /></el-icon>
              复制代码
            </el-button>
          </div>
          <div class="result-section-content">
            <pre class="result-code">{{ reviewResult.improvedCode }}</pre>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="resultDialogVisible = false" class="cancel-button">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  RefreshLeft,
  View,
  Delete,
  VideoPlay,
  Trophy,
  ChatLineSquare,
  DocumentCopy,
  CopyDocument
} from '@element-plus/icons-vue'
import { aiReviewApi } from '@/api/ai-review.api'
import { projectApi } from '@/api/project.api'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const projects = ref([])
const dialogVisible = ref(false)
const resultDialogVisible = ref(false)
const dialogTitle = ref('新增评审')
const formRef = ref(null)
const reviewResult = ref(null)

const searchForm = reactive({
  projectId: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  projectId: null,
  codeContent: ''
})

const formRules = {
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  codeContent: [
    { required: true, message: '请输入代码内容', trigger: 'blur' }
  ]
}

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'warning'
  return 'danger'
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
    const res = await aiReviewApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: searchForm.projectId || undefined
    })
    if (res.code === 0) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取评审记录失败')
  } finally {
    loading.value = false
  }
}

const handleProjectChange = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.projectId = null
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增评审'
  resetForm()
  dialogVisible.value = true
}

const handleView = (row) => {
  reviewResult.value = row
  resultDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该评审记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await aiReviewApi.delete(row.id)
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
    
    submitting.value = true
    const res = await aiReviewApi.submit({
      projectId: formData.projectId,
      codeContent: formData.codeContent
    })
    
    if (res.code === 0) {
      ElMessage.success('评审完成')
      dialogVisible.value = false
      reviewResult.value = res.data
      resultDialogVisible.value = true
      fetchData()
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('评审失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleCopyCode = () => {
  if (reviewResult.value?.improvedCode) {
    navigator.clipboard.writeText(reviewResult.value.improvedCode)
    ElMessage.success('复制成功')
  }
}

const resetForm = () => {
  formData.projectId = null
  formData.codeContent = ''
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
.ai-review-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.ai-review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.ai-review-title {
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

.ai-review-content {
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

.ai-review-table {
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

.ai-review-dialog {
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
      font-size: 14px;

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

.result-dialog {
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

  .result-content {
    .result-section {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .result-section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #ffd700;
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 12px;

      .el-icon {
        font-size: 20px;
      }

      .copy-button {
        margin-left: auto;
        color: #ffd700;

        &:hover {
          color: #ffed4e;
        }
      }
    }

    .result-section-content {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      border-radius: 8px;
      padding: 16px;

      .score-tag {
        font-size: 24px;
        font-weight: bold;
      }

      .result-text,
      .result-code {
        color: #e5e5e5;
        font-size: 14px;
        line-height: 1.6;
        margin: 0;
        white-space: pre-wrap;
        word-wrap: break-word;
      }

      .result-code {
        font-family: 'Courier New', monospace;
        background: #1a1b1e;
        padding: 12px;
        border-radius: 4px;
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
