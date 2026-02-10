<template>
  <div class="code-review-container">
    <div class="code-review-header">
      <h1 class="code-review-title">代码评审</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增评审
      </el-button>
    </div>

    <div class="code-review-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="文件路径">
            <el-input
              v-model="searchForm.file"
              placeholder="请输入文件路径"
              clearable
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="评审状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
            >
              <el-option label="待评审" value="pending" />
              <el-option label="已通过" value="passed" />
              <el-option label="需修改" value="rejected" />
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
          class="code-review-table"
        >
          <el-table-column prop="file" label="文件路径" min-width="200" show-overflow-tooltip />
          <el-table-column prop="commitId" label="提交ID" min-width="150" show-overflow-tooltip />
          <el-table-column prop="reviewer" label="评审人" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="评分" width="100">
            <template #default="{ row }">
              <div class="score-container">
                <el-rate
                  v-model="row.score"
                  disabled
                  :max="10"
                  :show-score="false"
                  class="score-rate"
                />
                <span class="score-text">{{ row.score }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="comments" label="评审意见" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
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
      width="600px"
      class="code-review-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="文件路径" prop="file">
          <el-input v-model="formData.file" placeholder="请输入文件路径" />
        </el-form-item>
        <el-form-item label="提交ID" prop="commitId">
          <el-input v-model="formData.commitId" placeholder="请输入提交ID" />
        </el-form-item>
        <el-form-item label="评审人" prop="reviewer">
          <el-input v-model="formData.reviewer" placeholder="请输入评审人" />
        </el-form-item>
        <el-form-item label="评审状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待评审" value="pending" />
            <el-option label="已通过" value="passed" />
            <el-option label="需修改" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-rate
            v-model="formData.score"
            :max="10"
            :show-score="true"
            score-template="{value}"
          />
        </el-form-item>
        <el-form-item label="评审意见" prop="comments">
          <el-input
            v-model="formData.comments"
            type="textarea"
            :rows="4"
            placeholder="请输入评审意见"
          />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Delete } from '@element-plus/icons-vue'
import { codeReviewApi } from '@/api/code-review.api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增评审')
const formRef = ref(null)

const searchForm = reactive({
  file: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  projectId: 1,
  file: '',
  commitId: '',
  reviewer: '',
  status: 'pending',
  score: 0,
  comments: ''
})

const formRules = {
  file: [
    { required: true, message: '请输入文件路径', trigger: 'blur' }
  ],
  commitId: [
    { required: true, message: '请输入提交ID', trigger: 'blur' }
  ],
  reviewer: [
    { required: true, message: '请输入评审人', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择评审状态', trigger: 'change' }
  ]
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    passed: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待评审',
    passed: '已通过',
    rejected: '需修改'
  }
  return textMap[status] || status
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await codeReviewApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: formData.projectId,
      file: searchForm.file || undefined,
      status: searchForm.status || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取评审列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.file = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增评审'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑评审'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该评审记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await codeReviewApi.delete(row.id)
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
    
    if (formData.id) {
      await codeReviewApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await codeReviewApi.add(formData)
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
  formData.file = ''
  formData.commitId = ''
  formData.reviewer = ''
  formData.status = 'pending'
  formData.score = 0
  formData.comments = ''
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
.code-review-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.code-review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.code-review-title {
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

.code-review-content {
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

.code-review-table {
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

  .score-container {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .score-rate {
    color: #ffd700;
  }

  .score-text {
    min-width: 20px;
    text-align: right;
    color: #ffd700;
    font-weight: bold;
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

.code-review-dialog {
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

      &:hover {
        border-color: #ffd700;
      }

      &:focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }

    .el-rate {
      color: #ffd700;

      &:deep(.el-rate__text) {
        color: #ffd700;
        font-weight: bold;
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