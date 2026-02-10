<template>
  <div class="project-container">
    <div class="project-header">
      <h1 class="project-title">项目管理</h1>
      <el-button type="primary" @click="handleAdd" class="gold-button">
        <el-icon><Plus /></el-icon>
        新增项目
      </el-button>
    </div>

    <div class="project-content">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="项目名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入项目名称"
              clearable
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="项目状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
            >
              <el-option label="开发中" value="developing" />
              <el-option label="测试中" value="testing" />
              <el-option label="已上线" value="online" />
              <el-option label="停滞" value="stop" />
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
          class="project-table"
        >
          <el-table-column prop="name" label="项目名称" min-width="150" />
          <el-table-column prop="description" label="项目描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="techStack" label="技术栈" min-width="150" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="onlineUrl" label="线上地址" min-width="150" show-overflow-tooltip />
          <el-table-column prop="docUrl" label="文档地址" min-width="150" show-overflow-tooltip />
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
      class="project-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="Git地址" prop="gitUrl">
          <el-input v-model="formData.gitUrl" placeholder="请输入Git仓库地址" />
        </el-form-item>
        <el-form-item label="技术栈" prop="techStack">
          <el-input v-model="formData.techStack" placeholder="请输入技术栈，如：SpringBoot3,Vue3,MySQL" />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="开发中" value="developing" />
            <el-option label="测试中" value="testing" />
            <el-option label="已上线" value="online" />
            <el-option label="停滞" value="stop" />
          </el-select>
        </el-form-item>
        <el-form-item label="线上地址" prop="onlineUrl">
          <el-input v-model="formData.onlineUrl" placeholder="请输入线上访问地址" />
        </el-form-item>
        <el-form-item label="文档地址" prop="docUrl">
          <el-input v-model="formData.docUrl" placeholder="请输入文档地址" />
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
import { projectApi } from '@/api/project.api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增项目')
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  id: null,
  name: '',
  description: '',
  gitUrl: '',
  techStack: '',
  status: 'developing',
  onlineUrl: '',
  docUrl: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ]
}

const getStatusType = (status) => {
  const typeMap = {
    developing: 'warning',
    testing: 'primary',
    online: 'success',
    stop: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    developing: '开发中',
    testing: '测试中',
    online: '已上线',
    stop: '停滞'
  }
  return textMap[status] || status
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await projectApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增项目'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑项目'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该项目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await projectApi.delete(row.id)
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
      await projectApi.update(formData)
      ElMessage.success('更新成功')
    } else {
      await projectApi.add(formData)
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
  formData.name = ''
  formData.description = ''
  formData.gitUrl = ''
  formData.techStack = ''
  formData.status = 'developing'
  formData.onlineUrl = ''
  formData.docUrl = ''
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
.project-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  padding: 24px;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.project-title {
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

.project-content {
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

.project-table {
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

.project-dialog {
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