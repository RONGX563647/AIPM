<template>
  <div class="setting-container">
    <el-card class="setting-card">
      <template #header>
        <div class="card-header">
          <span>用户设置管理</span>
          <el-button type="primary" @click="resetDefaults">重置默认设置</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" class="setting-tabs">
        <!-- 通用设置 -->
        <el-tab-pane label="通用设置" name="general">
          <el-form :model="settings" label-width="120px" class="setting-form">
            <el-form-item label="自动保存">
              <el-switch
                v-model="settings.auto_save"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
            
            <el-form-item label="每页显示条数">
              <el-select v-model="settings.items_per_page" placeholder="请选择">
                <el-option label="10条" :value="10" />
                <el-option label="20条" :value="20" />
                <el-option label="50条" :value="50" />
                <el-option label="100条" :value="100" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="时区设置">
              <el-select v-model="settings.timezone" placeholder="请选择时区">
                <el-option label="北京时间 (UTC+8)" value="Asia/Shanghai" />
                <el-option label="纽约时间 (UTC-5)" value="America/New_York" />
                <el-option label="伦敦时间 (UTC+0)" value="Europe/London" />
                <el-option label="东京时间 (UTC+9)" value="Asia/Tokyo" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 外观设置 -->
        <el-tab-pane label="外观设置" name="appearance">
          <el-form :model="settings" label-width="120px" class="setting-form">
            <el-form-item label="主题模式">
              <el-select v-model="settings.theme" placeholder="请选择主题">
                <el-option label="浅色主题" value="light" />
                <el-option label="深色主题" value="dark" />
                <el-option label="自动切换" value="auto" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="语言设置">
              <el-select v-model="settings.language" placeholder="请选择语言">
                <el-option label="中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="侧边栏状态">
              <el-switch
                v-model="settings.sidebar_collapsed"
                active-text="收起"
                inactive-text="展开"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notification">
          <el-form :model="settings" label-width="120px" class="setting-form">
            <el-form-item label="启用通知">
              <el-switch
                v-model="settings['notifications.enabled']"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
            
            <el-form-item label="邮件通知">
              <el-switch
                v-model="settings['notifications.email']"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
            
            <el-form-item label="桌面通知">
              <el-switch
                v-model="settings['notifications.desktop']"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="setting-actions">
        <el-button type="primary" @click="saveSettings">保存设置</el-button>
        <el-button @click="cancelChanges">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { settingApi, SettingType, SettingCategory } from '@/api/setting.api'

const activeTab = ref('general')
const settings = reactive({
  theme: 'light',
  language: 'zh-CN',
  'notifications.enabled': true,
  'notifications.email': true,
  'notifications.desktop': false,
  auto_save: true,
  sidebar_collapsed: false,
  items_per_page: 10,
  timezone: 'Asia/Shanghai'
})

const originalSettings = ref({})

// 获取用户设置
const loadSettings = async () => {
  try {
    const response = await settingApi.getUserSettings(1)
    if (response.data && response.data.code === 200) {
      // 将返回的设置数组转换为对象
      const settingsMap = {}
      response.data.data.forEach(setting => {
        settingsMap[setting.settingKey] = setting.settingValue
      })
      Object.assign(settings, settingsMap)
      originalSettings.value = { ...settingsMap }
    }
  } catch (error) {
    ElMessage.error('获取设置失败')
  }
}

// 保存设置
const saveSettings = async () => {
  try {
    // 将设置对象转换为数组格式
    const settingsArray = Object.keys(settings).map(key => ({
      userId: 1,
      settingKey: key,
      settingValue: settings[key].toString(),
      settingType: typeof settings[key] === 'boolean' ? SettingType.BOOLEAN : 
                  typeof settings[key] === 'number' ? SettingType.INTEGER : SettingType.STRING,
      category: 'GENERAL' // 简化处理，实际应该根据key判断分类
    }))
    
    const response = await settingApi.saveUserSettings(1, settingsArray)
    if (response.data && response.data.code === 200) {
      ElMessage.success('设置保存成功')
      originalSettings.value = { ...settings }
    } else {
      ElMessage.error(response.data?.message || '设置保存失败')
    }
  } catch (error) {
    ElMessage.error('设置保存失败')
  }
}

// 重置默认设置
const resetDefaults = async () => {
  try {
    await ElMessageBox.confirm('确定要重置为默认设置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await settingApi.resetUserSettings(1)
    if (response.data && response.data.code === 200) {
      ElMessage.success('默认设置重置成功')
      await loadSettings()
    } else {
      ElMessage.error(response.data?.message || '重置默认设置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置默认设置失败')
    }
  }
}

// 取消更改
const cancelChanges = () => {
  Object.assign(settings, originalSettings.value)
  ElMessage.info('已取消更改')
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.setting-container {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 120px);
}

.setting-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.setting-tabs {
  margin-bottom: 30px;
}

.setting-form {
  max-width: 500px;
}

.setting-form .el-form-item {
  margin-bottom: 25px;
}

.setting-actions {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-light);
}

.setting-actions .el-button {
  margin: 0 10px;
}
</style>