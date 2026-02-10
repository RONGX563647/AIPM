<template>
  <div class="task-container">
    <div class="task-header">
      <h1 class="task-title">任务管理</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增任务
      </el-button>
    </div>

    <div class="task-content">
      <el-card class="progress-card">
        <div class="progress-info">
          <div class="progress-label">项目进度</div>
          <div class="progress-value">{{ projectProgress }}%</div>
        </div>
        <el-progress
          :percentage="projectProgress"
          :stroke-width="20"
          :color="progressColor"
          class="task-progress"
        />
      </el-card>

      <el-card class="table-card">
        <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          class="task-table"
        >
          <el-table-column prop="title" label="任务标题" min-width="150" />
          <el-table-column prop="content" label="任务内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-select
                v-model="row.status"
                @change="handleStatusChange(row)"
                size="small"
                class="status-select"
              >
                <el-option label="待办" value="todo" />
                <el-option label="进行中" value="doing" />
                <el-option label="已完成" value="done" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)" size="small">
                {{ getPriorityText(row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="progress" label="进度" width="200">
            <template #default="{ row }">
              <div class="progress-cell">
                <el-slider
                  v-model="row.progress"
                  :min="0"
                  :max="100"
                  @change="handleProgressChange(row)"
                  class="task-slider"
                />
                <span class="progress-text">{{ row.progress }}%</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
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
      class="task-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="任务内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入任务内容"
          />
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待办" value="todo" />
            <el-option label="进行中" value="doing" />
            <el-option label="已完成" value="done" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="formData.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度" prop="progress">
          <el-slider
            v-model="formData.progress"
            :min="0"
            :max="100"
            :marks="{ 0: '0%', 50: '50%', 100: '100%' }"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { taskApi } from '@/api/task.api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增任务')
const formRef = ref(null)
const projectProgress = ref(0)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  projectId: 1,
  title: '',
  content: '',
  status: 'todo',
  priority: 'medium',
  progress: 0
})

const formRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择任务状态', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

const progressColor = computed(() => {
  if (projectProgress.value < 30) return '#ff3b30'
  if (projectProgress.value < 70) return '#ffd700'
  return '#34c759'
})

const getPriorityType = (priority) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return typeMap[priority] || 'info'
}

const getPriorityText = (priority) => {
  const textMap = {
    high: '高',
    medium: '中',
    low: '低'
  }
  return textMap[priority] || priority
}

const fetchProgress = async () => {
  try {
    const res = await taskApi.getProgress(formData.projectId)
    if (res.code === 200) {
      projectProgress.value = res.data.progress
    }
  } catch (error) {
    console.error('获取项目进度失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await taskApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      projectId: formData.projectId
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增任务'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑任务'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await taskApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchData()
      fetchProgress()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleStatusChange = async (row) => {
  try {
    await taskApi.update(row)
    ElMessage.success('状态更新成功')
    fetchProgress()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const handleProgressChange = async (row) => {
  try {
    await taskApi.update(row)
    ElMessage.success('进度更新成功')
    fetchProgress()
  } catch (error) {
    ElMessage.error('进度更新失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      await taskApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await taskApi.add(formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
    fetchProgress()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(formData.id ? '更新失败' : '新增失败')
    }
  }
}

const resetForm = () => {
  formData.id = null
  formData.title = ''
  formData.content = ''
  formData.status = 'todo'
  formData.priority = 'medium'
  formData.progress = 0
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
  fetchProgress()
})
</script>

<style scoped lang="scss">
.task-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.task-title {
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

.task-content {
  .progress-card,
  .table-card {
    background: rgba(26, 27, 30, 0.8);
    border: 1px solid rgba(255, 215, 0, 0.2);
    backdrop-filter: blur(10px);
    margin-bottom: 24px;

    &:deep(.el-card__body) {
      padding: 24px;
    }
  }

  .progress-card {
    margin-bottom: 16px;
  }

  .progress-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .progress-label {
    font-size: 16px;
    color: #e5e5e5;
    font-weight: bold;
  }

  .progress-value {
    font-size: 32px;
    color: #ffd700;
    font-weight: bold;
  }

  .task-progress {
    &:deep(.el-progress__text) {
      color: #ffd700;
      font-weight: bold;
    }
  }
}

.task-table {
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

  .status-select {
    width: 100px;

    &:deep(.el-input__wrapper) {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
      color: #e5e5e5;
    }
  }

  .progress-cell {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .progress-text {
    min-width: 40px;
    text-align: right;
    color: #ffd700;
    font-weight: bold;
  }

  .task-slider {
    flex: 1;

    &:deep(.el-slider__runway) {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
    }

    &:deep(.el-slider__bar) {
      background: linear-gradient(90deg, #ffd700 0%, #b38b2d 100%);
    }

    &:deep(.el-slider__button) {
      border: 2px solid #ffd700;
      background: #111113;
    }
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

.task-dialog {
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

    .el-slider__runway {
      background: #2c2d31;
      border: 1px solid #4a4b4f;
    }

    .el-slider__bar {
      background: linear-gradient(90deg, #ffd700 0%, #b38b2d 100%);
    }

    .el-slider__button {
      border: 2px solid #ffd700;
      background: #111113;
    }

    .el-slider__marks-text {
      color: #8e8e93;
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