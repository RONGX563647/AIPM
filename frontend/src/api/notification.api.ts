import api from './auth.api'

/**
 * 通知消息相关API接口
 */

// 通知类型枚举
export const NotificationType = {
  SYSTEM: 'SYSTEM',
  USER: 'USER',
  ALERT: 'ALERT'
}

// 通知状态枚举
export const NotificationStatus = {
  UNREAD: 'UNREAD',
  READ: 'READ',
  ARCHIVED: 'ARCHIVED'
}

// 通知优先级枚举
export const NotificationPriority = {
  LOW: 'LOW',
  MEDIUM: 'MEDIUM',
  HIGH: 'HIGH',
  URGENT: 'URGENT'
}

/**
 * 通知消息实体
 */
export interface Notification {
  id?: number
  userId: number
  title: string
  content: string
  type: string
  status: string
  priority: string
  isRead?: boolean
  readAt?: string
  createdAt?: string
  updatedAt?: string
}

/**
 * 通知统计信息
 */
export interface NotificationStats {
  total: number
  unread: number
  read: number
  archived: number
}

/**
 * 分页查询参数
 */
export interface PageQuery {
  current: number
  size: number
  type?: string
  status?: string
  priority?: string
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

export const notificationApi = {
  /**
   * 获取用户通知分页列表
   * @param userId 用户ID
   * @param query 查询参数
   * @returns 通知分页列表
   */
  getUserNotificationsPage: (userId: number, query: PageQuery) => {
    return api.get(`/notification/user/${userId}/page`, { params: query })
  },

  /**
   * 获取用户通知统计信息
   * @param userId 用户ID
   * @returns 通知统计信息
   */
  getNotificationStats: (userId: number) => {
    return api.get(`/notification/user/${userId}/stats`)
  },

  /**
   * 发送通知
   * @param notification 通知对象
   * @returns 发送的通知识
   */
  sendNotification: (notification: Notification) => {
    return api.post('/notification/send', notification)
  },

  /**
   * 标记通知为已读
   * @param notificationId 通知ID
   * @returns 操作结果
   */
  markAsRead: (notificationId: number) => {
    return api.put(`/notification/mark-read/${notificationId}`)
  },

  /**
   * 批量标记通知为已读
   * @param notificationIds 通知ID列表
   * @returns 操作结果
   */
  markAsReadBatch: (notificationIds: number[]) => {
    return api.put('/notification/mark-read/batch', notificationIds)
  },

  /**
   * 删除通知
   * @param notificationId 通知ID
   * @returns 操作结果
   */
  deleteNotification: (notificationId: number) => {
    return api.delete(`/notification/${notificationId}`)
  },

  /**
   * 批量删除通知
   * @param notificationIds 通知ID列表
   * @returns 操作结果
   */
  deleteNotificationsBatch: (notificationIds: number[]) => {
    return api.delete('/notification/batch', { data: notificationIds })
  },

  /**
   * 归档通知
   * @param notificationId 通知ID
   * @returns 操作结果
   */
  archiveNotification: (notificationId: number) => {
    return api.put(`/notification/archive/${notificationId}`)
  },

  /**
   * 获取未读通知数量
   * @param userId 用户ID
   * @returns 未读通知数量
   */
  getUnreadCount: (userId: number) => {
    return api.get(`/notification/user/${userId}/unread-count`)
  }
}