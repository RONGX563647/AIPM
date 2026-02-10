<template>
  <div class="forgot-container">
    <div class="forgot-card">
      <div class="forgot-header">
        <h2 class="forgot-title">
          <el-icon class="title-icon"><Key /></el-icon>
          找回密码
        </h2>
        <p class="forgot-subtitle">重置您的账号密码</p>
      </div>

      <el-form
        v-if="!resetToken"
        ref="forgotFormRef"
        :model="forgotForm"
        :rules="forgotRules"
        class="forgot-form"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="forgotForm.username"
            placeholder="请输入您的用户名"
            prefix-icon="User"
            size="large"
            :class="['forgot-input']"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="isLoading"
            @click="handleForgotPassword"
            size="large"
            :class="['forgot-button']"
          >
            生成重置链接
          </el-button>
        </el-form-item>
      </el-form>

      <el-form
        v-else
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        class="forgot-form"
        label-position="top"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            prefix-icon="Lock"
            show-password
            size="large"
            :class="['forgot-input']"
          />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            prefix-icon="Check"
            show-password
            size="large"
            :class="['forgot-input']"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="isLoading"
            @click="handleResetPassword"
            size="large"
            :class="['forgot-button']"
          >
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <div class="forgot-footer">
        <span>想起密码了？</span>
        <el-button
          type="text"
          @click="navigateToLogin"
          :class="['login-link']"
        >
          立即登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Key } from '@element-plus/icons-vue'
import { forgotPassword, resetPassword } from '@/api/auth.api'

const router = useRouter()
const forgotFormRef = ref(null)
const resetFormRef = ref(null)
const isLoading = ref(false)
const resetToken = ref('')

const forgotForm = reactive({
  username: ''
})

const resetForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const forgotRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ]
}

const resetRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleForgotPassword = async () => {
  if (!forgotFormRef.value) return

  try {
    await forgotFormRef.value.validate()
    isLoading.value = true

    const response = await forgotPassword(forgotForm.username)
    if (response.code === 200) {
      resetToken.value = response.data
      ElMessage.success('重置链接已生成，请使用生成的令牌进行密码重置')
      console.log('重置令牌:', response.data)
    } else {
      ElMessage.error(response.msg || '生成重置链接失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '生成重置链接失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const handleResetPassword = async () => {
  if (!resetFormRef.value) return

  try {
    await resetFormRef.value.validate()
    isLoading.value = true

    const response = await resetPassword(resetToken.value, resetForm.newPassword)
    if (response.code === 200) {
      ElMessage.success('密码重置成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response.msg || '密码重置失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '密码重置失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const navigateToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.forgot-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-black) 0%, var(--color-dark-gray) 100%);
  padding: 20px;
}

.forgot-card {
  width: 100%;
  max-width: 480px;
  background: var(--color-secondary-black);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  padding: 40px;
  border: var(--border-gold-sm);
  backdrop-filter: var(--backdrop-blur-md);

  @media (max-width: 768px) {
    padding: 30px 24px;
  }
}

.forgot-header {
  text-align: center;
  margin-bottom: 32px;
}

.forgot-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-gold-primary);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
}

.forgot-subtitle {
  font-size: 16px;
  color: var(--color-light-gray);
  margin: 0;
}

.forgot-form {
  width: 100%;
}

.forgot-input {
  width: 100%;
  background: var(--color-dark-gray);
  border: 1px solid var(--color-medium-gray);
  border-radius: var(--radius-md);
  color: var(--color-ultra-light-gray);
  height: 52px;
  font-size: 16px;

  &:hover {
    border-color: var(--color-gold-primary);
  }

  &:focus {
    border-color: var(--color-gold-primary);
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
  }
}

.forgot-button {
  width: 100%;
  height: 52px;
  font-size: 18px;
  font-weight: 600;
  background: var(--gradient-gold);
  border: none;
  border-radius: var(--radius-md);
  color: var(--color-primary-black);
  transition: all var(--duration-fast) var(--ease-in-out);

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-gold);
  }

  &:active {
    transform: translateY(0);
  }
}

.forgot-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--color-light-gray);
}

.login-link {
  color: var(--color-gold-primary);
  font-weight: 500;
  margin-left: 4px;

  &:hover {
    color: var(--color-gold-light);
  }
}

// 动画效果
.forgot-card {
  animation: slide-up var(--duration-slow) var(--ease-out);
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式调整
@media (max-width: 480px) {
  .forgot-card {
    margin: 0 16px;
  }

  .forgot-title {
    font-size: 24px;
  }

  .title-icon {
    font-size: 28px;
  }
}
</style>