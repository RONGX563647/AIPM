<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2 class="register-title">
          <el-icon class="title-icon"><UserFilled /></el-icon>
          注册账号
        </h2>
        <p class="register-subtitle">创建新账号，开启智能研发之旅</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            :class="['register-input']"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            prefix-icon="Lock"
            show-password
            size="large"
            :class="['register-input']"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Check"
            show-password
            size="large"
            :class="['register-input']"
          />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称（选填）"
            prefix-icon="Avatar"
            size="large"
            :class="['register-input']"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="isLoading"
            @click="handleRegister"
            size="large"
            :class="['register-button']"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-button
            type="text"
            @click="navigateToLogin"
            :class="['login-link']"
          >
            立即登录
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { register } from '@/api/auth.api'

const router = useRouter()
const registerFormRef = ref(null)
const isLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度应在3-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    isLoading.value = true

    const response = await register(registerForm)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const navigateToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-black) 0%, var(--color-dark-gray) 100%);
  padding: 20px;
}

.register-card {
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

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-title {
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

.register-subtitle {
  font-size: 16px;
  color: var(--color-light-gray);
  margin: 0;
}

.register-form {
  width: 100%;
}

.register-input {
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

.register-button {
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

.register-footer {
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
.register-card {
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
  .register-card {
    margin: 0 16px;
  }

  .register-title {
    font-size: 24px;
  }

  .title-icon {
    font-size: 28px;
  }
}
</style>