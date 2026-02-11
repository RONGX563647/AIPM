<template>
  <div class="sync-container">
    <el-card class="sync-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">数据同步管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" class="sync-tabs">
        <el-tab-pane label="同步配置" name="config">
          <div class="config-section">
            <el-form :model="syncForm" label-width="120px">
              <el-form-item label="GitHub用户名">
                <el-input v-model="syncForm.githubUsername" placeholder="请输入GitHub用户名" />
              </el-form-item>
              <el-form-item label="GitHub Token">
                <el-input v-model="syncForm.githubToken" type="password" placeholder="请输入GitHub访问令牌" show-password />
              </el-form-item>
              <el-form-item label="Gitee用户名">
                <el-input v-model="syncForm.giteeUsername" placeholder="请输入Gitee用户名" />
              </el-form-item>
              <el-form-item label="Gitee Token">
                <el-input v-model="syncForm.giteeToken" type="password" placeholder="请输入Gitee访问令牌" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="syncGitHub" :loading="syncing.github">
                  <el-icon><Refresh /></el-icon>
                  同步GitHub
                </el-button>
                <el-button type="success" @click="syncGitee" :loading="syncing.gitee">
                  <el-icon><Refresh /></el-icon>
                  同步Gitee
                </el-button>
                <el-button type="warning" @click="syncAll" :loading="syncing.all">
                  <el-icon><Refresh /></el-icon>
                  全量同步
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="GitHub仓库" name="github">
          <div class="repo-section">
            <el-table :data="githubRepos" style="width: 100%" v-loading="loading.github">
              <el-table-column prop="repoName" label="仓库名称" min-width="200" />
              <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
              <el-table-column prop="language" label="语言" width="100" />
              <el-table-column prop="starsCount" label="星标" width="80" />
              <el-table-column prop="forksCount" label="分支" width="80" />
              <el-table-column prop="openIssuesCount" label="问题" width="80" />
              <el-table-column prop="updatedAt" label="更新时间" width="180" />
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button type="danger" size="small" @click="deleteGitHubRepo(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-model:current-page="githubPagination.pageNum"
              v-model:page-size="githubPagination.pageSize"
              :total="githubPagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleGitHubSizeChange"
              @current-change="handleGitHubPageChange"
              class="pagination"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="Gitee仓库" name="gitee">
          <div class="repo-section">
            <el-table :data="giteeRepos" style="width: 100%" v-loading="loading.gitee">
              <el-table-column prop="repoName" label="仓库名称" min-width="200" />
              <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
              <el-table-column prop="language" label="语言" width="100" />
              <el-table-column prop="starsCount" label="星标" width="80" />
              <el-table-column prop="forksCount" label="分支" width="80" />
              <el-table-column prop="openIssuesCount" label="问题" width="80" />
              <el-table-column prop="updatedAt" label="更新时间" width="180" />
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button type="danger" size="small" @click="deleteGiteeRepo(row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-model:current-page="giteePagination.pageNum"
              v-model:page-size="giteePagination.pageSize"
              :total="giteePagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleGiteeSizeChange"
              @current-change="handleGiteePageChange"
              class="pagination"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="同步记录" name="records">
          <div class="record-section">
            <el-table :data="syncRecords" style="width: 100%" v-loading="loading.records">
              <el-table-column prop="source" label="数据源" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.source === 'github' ? 'primary' : 'success'">
                    {{ row.source === 'github' ? 'GitHub' : 'Gitee' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="repoName" label="仓库名称" width="200" />
              <el-table-column prop="syncStatus" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.syncStatus)">
                    {{ getStatusText(row.syncStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="dataCount" label="数据量" width="100" />
              <el-table-column prop="syncTime" label="同步时间" width="180" />
              <el-table-column prop="errorMessage" label="错误信息" min-width="300" show-overflow-tooltip />
            </el-table>
            <el-pagination
              v-model:current-page="recordPagination.pageNum"
              v-model:page-size="recordPagination.pageSize"
              :total="recordPagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleRecordSizeChange"
              @current-change="handleRecordPageChange"
              class="pagination"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { syncApi, type GitHubRepo, type GiteeRepo, type SyncRecord } from '@/api/sync.api'

const activeTab = ref('config')

const syncForm = ref({
  githubUsername: '',
  githubToken: '',
  giteeUsername: '',
  giteeToken: ''
})

const syncing = ref({
  github: false,
  gitee: false,
  all: false
})

const loading = ref({
  github: false,
  gitee: false,
  records: false
})

const githubRepos = ref<GitHubRepo[]>([])
const giteeRepos = ref<GiteeRepo[]>([])
const syncRecords = ref<SyncRecord[]>([])

const githubPagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const giteePagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const recordPagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const syncGitHub = async () => {
  if (!syncForm.value.githubUsername) {
    ElMessage.warning('请输入GitHub用户名')
    return
  }
  
  syncing.value.github = true
  try {
    await syncApi.syncGitHub(syncForm.value.githubUsername, syncForm.value.githubToken)
    ElMessage.success('GitHub同步成功')
    loadGitHubRepos()
    loadSyncRecords()
  } catch (error: any) {
    ElMessage.error('GitHub同步失败：' + (error.message || '未知错误'))
  } finally {
    syncing.value.github = false
  }
}

const syncGitee = async () => {
  if (!syncForm.value.giteeUsername) {
    ElMessage.warning('请输入Gitee用户名')
    return
  }
  
  syncing.value.gitee = true
  try {
    await syncApi.syncGitee(syncForm.value.giteeUsername, syncForm.value.giteeToken)
    ElMessage.success('Gitee同步成功')
    loadGiteeRepos()
    loadSyncRecords()
  } catch (error: any) {
    ElMessage.error('Gitee同步失败：' + (error.message || '未知错误'))
  } finally {
    syncing.value.gitee = false
  }
}

const syncAll = async () => {
  syncing.value.all = true
  try {
    await syncApi.syncAll()
    ElMessage.success('全量同步成功')
    loadGitHubRepos()
    loadGiteeRepos()
    loadSyncRecords()
  } catch (error: any) {
    ElMessage.error('全量同步失败：' + (error.message || '未知错误'))
  } finally {
    syncing.value.all = false
  }
}

const loadGitHubRepos = async () => {
  loading.value.github = true
  try {
    const data = await syncApi.getGitHubRepos({
      pageNum: githubPagination.value.pageNum,
      pageSize: githubPagination.value.pageSize
    })
    githubRepos.value = data || []
  } catch (error: any) {
    ElMessage.error('加载GitHub仓库失败：' + (error.message || '未知错误'))
  } finally {
    loading.value.github = false
  }
}

const loadGiteeRepos = async () => {
  loading.value.gitee = true
  try {
    const data = await syncApi.getGiteeRepos({
      pageNum: giteePagination.value.pageNum,
      pageSize: giteePagination.value.pageSize
    })
    giteeRepos.value = data || []
  } catch (error: any) {
    ElMessage.error('加载Gitee仓库失败：' + (error.message || '未知错误'))
  } finally {
    loading.value.gitee = false
  }
}

const loadSyncRecords = async () => {
  loading.value.records = true
  try {
    const data = await syncApi.getSyncRecords({
      pageNum: recordPagination.value.pageNum,
      pageSize: recordPagination.value.pageSize
    })
    syncRecords.value = data || []
  } catch (error: any) {
    ElMessage.error('加载同步记录失败：' + (error.message || '未知错误'))
  } finally {
    loading.value.records = false
  }
}

const deleteGitHubRepo = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个GitHub仓库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await syncApi.deleteGitHubRepo(id)
    ElMessage.success('删除成功')
    loadGitHubRepos()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

const deleteGiteeRepo = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个Gitee仓库吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await syncApi.deleteGiteeRepo(id)
    ElMessage.success('删除成功')
    loadGiteeRepos()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

const handleGitHubSizeChange = (size: number) => {
  githubPagination.value.pageSize = size
  loadGitHubRepos()
}

const handleGitHubPageChange = (page: number) => {
  githubPagination.value.pageNum = page
  loadGitHubRepos()
}

const handleGiteeSizeChange = (size: number) => {
  giteePagination.value.pageSize = size
  loadGiteeRepos()
}

const handleGiteePageChange = (page: number) => {
  giteePagination.value.pageNum = page
  loadGiteeRepos()
}

const handleRecordSizeChange = (size: number) => {
  recordPagination.value.pageSize = size
  loadSyncRecords()
}

const handleRecordPageChange = (page: number) => {
  recordPagination.value.pageNum = page
  loadSyncRecords()
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'success': return 'success'
    case 'failed': return 'danger'
    case 'pending': return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'success': return '成功'
    case 'failed': return '失败'
    case 'pending': return '进行中'
    default: return status
  }
}

onMounted(() => {
  loadGitHubRepos()
  loadGiteeRepos()
  loadSyncRecords()
})
</script>

<style scoped lang="scss">
.sync-container {
  padding: 20px;
  
  .sync-card {
    background: var(--color-secondary-black);
    border: 1px solid var(--color-border);
    
    .card-header {
      display: flex;
      align-items: center;
      
      .card-title {
        font-size: 18px;
        font-weight: bold;
        color: var(--color-gold-primary);
      }
    }
  }
  
  .sync-tabs {
    :deep(.el-tabs__header) {
      background: var(--color-tertiary-black);
      border-bottom: 1px solid var(--color-border);
      
      .el-tabs__item {
        color: var(--color-light-gray);
        
        &.is-active {
          color: var(--color-gold-primary);
        }
        
        &:hover {
          color: var(--color-gold-primary);
        }
      }
    }
    
    :deep(.el-tabs__content) {
      padding: 20px;
    }
  }
  
  .config-section {
    max-width: 600px;
    margin: 0 auto;
  }
  
  .repo-section,
  .record-section {
    .el-table {
      font-family: 'SimHei', '黑体', sans-serif;
      
      th {
        background: var(--color-tertiary-black);
        color: var(--color-gold-primary);
        font-weight: bold;
      }
      
      td {
        color: #000000;
      }
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
