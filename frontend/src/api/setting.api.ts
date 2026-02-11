import api from './auth.api'

/**
 * 用户设置相关API接口
 */

// 设置类型枚举
export const SettingType = {
  STRING: 'STRING',
  INTEGER: 'INTEGER',
  BOOLEAN: 'BOOLEAN',
  JSON: 'JSON'
}

// 设置分类枚举
export const SettingCategory = {
  GENERAL: 'GENERAL',
  NOTIFICATION: 'NOTIFICATION',
  PRIVACY: 'PRIVACY',
  DISPLAY: 'DISPLAY'
}

/**
 * 用户设置实体
 */
export interface UserSetting {
  id?: number
  userId: number
  settingKey: string
  settingValue: string
  settingType: string
  category: string
  description?: string
  createdAt?: string
  updatedAt?: string
}

export const settingApi = {
  /**
   * 获取用户所有设置
   * @param userId 用户ID
   * @returns 用户设置列表
   */
  getUserSettings: (userId: number) => {
    return api.get(`/setting/user/${userId}`)
  },

  /**
   * 根据分类获取用户设置
   * @param userId 用户ID
   * @param category 设置分类
   * @returns 用户设置列表
   */
  getUserSettingsByCategory: (userId: number, category: string) => {
    return api.get(`/setting/user/${userId}/category/${category}`)
  },

  /**
   * 获取用户特定设置
   * @param userId 用户ID
   * @param settingKey 设置键
   * @returns 用户设置
   */
  getUserSetting: (userId: number, settingKey: string) => {
    return api.get(`/setting/user/${userId}/key/${settingKey}`)
  },

  /**
   * 保存用户设置
   * @param userId 用户ID
   * @param setting 设置对象
   * @returns 保存结果
   */
  saveUserSetting: (userId: number, setting: UserSetting) => {
    return api.post(`/setting/user/${userId}`, setting)
  },

  /**
   * 批量保存用户设置
   * @param userId 用户ID
   * @param settings 设置列表
   * @returns 保存结果
   */
  saveUserSettings: (userId: number, settings: UserSetting[]) => {
    return api.post(`/setting/user/${userId}/batch`, settings)
  },

  /**
   * 删除用户设置
   * @param userId 用户ID
   * @param settingKey 设置键
   * @returns 删除结果
   */
  deleteUserSetting: (userId: number, settingKey: string) => {
    return api.delete(`/setting/user/${userId}/key/${settingKey}`)
  },

  /**
   * 重置用户设置为默认值
   * @param userId 用户ID
   * @param category 设置分类
   * @returns 重置结果
   */
  resetUserSettings: (userId: number, category?: string) => {
    return api.post(`/setting/user/${userId}/reset`, {}, {
      params: category ? { category } : {}
    })
  }
}