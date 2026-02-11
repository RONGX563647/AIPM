<template>
  <div class="notification-container">
    <el-card class="notification-card">
      <template #header>
        <div class="card-header">
          <span>通知消息管理</span>
          <div class="header-actions">
            <el-badge :value="unreadCount" :max="99" class="unread-badge">
              <el-button type="primary" @click="loadUnreadNotifications">未读通知</el-button>
            </el-badge>
            <el-button @click="markAllAsRead">全部标记已读</el-button>
          </div>
        </div>
      </template>
      
      <!-- 通知筛选 -->
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
              <el-option label="未读" value="unread" />
              <el-option label="已读" value="read" />
              <el-option label="已归档" value="archived" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="请选择类型" clearable>
              <el-option label="系统通知" value="system" />
              <el-option label="项目通知" value="project" />
              <el-option label="任务通知" value="task" />
              <el-option label="警告通知" value="alert" />
              <el-option label="信息通知" value="info" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="优先级">
            <el-select v-model="filterForm.priority" placeholder="请选择优先级" clearable>
              <el-option label="低" value="low" />
              <el-option label="中" value="medium" />
              <el-option label="高" value="high" />
              <el-option label="紧急" value="urgent" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="loadNotifications">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 通知列表 -->
      <el-table 
        :data="notifications" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="scope">
            <span :class="{ 'unread-title': scope.row.status === 'unread' }">
              {{ scope.row.title }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getNotificationType(scope.row.type)">
              {{ getTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="scope">
            <el-tag :type="getPriorityType(scope.row.priority)" size="small">
              {{ getPriorityLabel(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="viewNotification(scope.row)"
              :disabled="scope.row.status === 'read'"
            >
              {{ scope.row.status === 'read' ? '已读' : '标记已读' }}
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteNotification(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentNotification?.title"
      width="600px"
    >
      <div v-if="currentNotification" class="notification-detail">
        <div class="notification-meta">
          <el-tag :type="getNotificationType(currentNotification.type)" class="meta-tag">
            {{ getTypeLabel(currentNotification.type) }}
          </el-tag>
          <el-tag :type="getPriorityType(currentNotification.priority)" class="meta-tag">
            {{ getPriorityLabel(currentNotification.priority) }}
          </el-tag>
          <span class="create-time">
            {{ formatDate(currentNotification.createTime) }}
          </span>
        </div>
        
        <div class="notification-content" v-html="currentNotification.content"></div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentNotification?.status === 'unread'"
            type="primary" 
            @click="markAsRead(currentNotification)"
          >
            标记已读
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { notificationApi } from '@/api/notification.api'

const loading = ref(false)
const dialogVisible = ref(false)
const currentNotification = ref(null)
const unreadCount = ref(0)
const notifications = ref([])
const selectedNotifications = ref([])

const filterForm = reactive({
  status: '',
  type: '',
  priority: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...filterForm
    }
    
    const response = await notificationApi.getUserNotificationsPage(1, params)
    if (response.data.code === 0) {
      notifications.value = response.data.data.records
      pagination.total = response.data.data.total
    }
  } catch (error) {
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

// 获取未读通知数量
const loadUnreadCount = async () => {
  try {
    const response = await notificationApi.getUnreadCount(1)
    if (response.data.code === 0) {
      unreadCount.value = response.data.data
    }
  } catch (error) {
    // 静默处理
  }
}

// 查看通知详情
const viewNotification = async (notification) => {
  currentNotification.value = notification
  dialogVisible.value = true
  
  // 如果是未读状态，自动标记为已读
  if (notification.status === 'unread') {
    await markAsRead(notification)
  }
}

// 标记单个通知为已读
const markAsRead = async (notification) => {
  try {
    const response = await notificationApi.markAsRead(notification.id)
    if (response.data.code === 0) {
      notification.status = 'read'
      loadUnreadCount()
      if (currentNotification.value?.id === notification.id) {
        currentNotification.value.status = 'read'
      }
    }
  } catch (error) {
    ElMessage.error('标记已读失败')
  }
}

// 批量标记已读
const markAllAsRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有未读通知标记为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 使用批量标记已读
    const unreadIds = notifications.value
      .filter(n => n.status === 'unread')
      .map(n => n.id)
    if (unreadIds.length > 0) {
      const response = await notificationApi.markAsReadBatch(unreadIds)
      if (response.data.code === 0) {
        ElMessage.success('全部标记已读成功')
        loadNotifications()
        loadUnreadCount()
      }
    } else {
      ElMessage.info('没有未读通知')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量标记已读失败')
    }
  }
}

// 删除通知
const deleteNotification = async (notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await notificationApi.deleteNotification(notification.id)
    if (response.data.code === 0) {
      ElMessage.success('通知删除成功')
      loadNotifications()
      loadUnreadCount()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('通知删除失败')
    }
  }
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.status = ''
  filterForm.type = ''
  filterForm.priority = ''
  loadNotifications()
}

// 处理分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadNotifications()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadNotifications()
}

// 处理多选
const handleSelectionChange = (val) => {
  selectedNotifications.value = val
}

// 工具函数
const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}

const getNotificationType = (type) => {
  const typeMap = {
    system: 'success',
    project: 'primary',
    task: 'warning',
    alert: 'danger',
    info: 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeLabel = (type) => {
  const labelMap = {
    system: '系统',
    project: '项目',
    task: '任务',
    alert: '警告',
    info: '信息'
  }
  return labelMap[type] || type
}

const getPriorityType = (priority) => {
  const priorityMap = {
    low: 'info',
    medium: '',
    high: 'warning',
    urgent: 'danger'
  }
  return priorityMap[priority] || ''
}

const getPriorityLabel = (priority) => {
  const labelMap = {
    low: '低',
    medium: '中',
    high: '高',
    urgent: '紧急'
  }
  return labelMap[priority] || priority
}

const getStatusType = (status) => {
  const statusMap = {
    unread: 'warning',
    read: 'success',
    archived: 'info'
  }
  return statusMap[status] || ''
}

const getStatusLabel = (status) => {
  const labelMap = {
    unread: '未读',
    read: '已读',
    archived: '已归档'
  }
  return labelMap[status] || status
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})
</script>

<style scoped>
.notification-container {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 120px);
}

.notification-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.unread-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

.filter-form {
  margin-bottom: 0;
}

.unread-title {
  font-weight: bold;
  color: var(--el-color-primary);
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.notification-detail {
  line-height: 1.6;
}

.notification-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.meta-tag {
  margin: 0;
}

.create-time {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.notification-content {
  padding: 15px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  min-height: 100px;
}
</style>