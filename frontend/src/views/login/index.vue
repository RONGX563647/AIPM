<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h1 class="login-title">AI全自动研发大脑</h1>
        <p class="login-subtitle">智能开发平台登录</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            :class="['login-input']"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            size="large"
            :class="['login-input']"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="isLoading"
            @click="handleLogin"
            class="login-button"
            size="large"
            :disabled="isLoading"
          >
            {{ isLoading ? "登录中..." : "登录" }}
          </el-button>
        </el-form-item>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- 分割线 -->
        <div class="divider">
          <span>或</span>
        </div>

        <!-- GitHub登录按钮 -->
        <el-form-item>
          <el-button
            type="default"
            @click="handleGithubLogin"
            class="github-login-button"
            size="large"
          >
            <el-icon class="github-icon"
              ><svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M15 22v-4a4.8 4.8 0 0 0-1-3.5c3 0 6-2 6-5.5.08-1.25-.27-2.48-1-3.5.28-1.15.28-2.35 0-3.5 0 0-1 0-3 1.5-2.64-.5-5.36-.5-8 0C6 2 5 2 5 2c-.3 1.15-.3 2.35 0 3.5A5.403 5.403 0 0 0 4 9c0 3.5 3 5.5 6 5.5-.39.49-.68 1.05-.85 1.65-.17.6-.22 1.23-.15 1.85v4"
                ></path>
                <path d="M9 18c-4.51 2-5-2-7-2"></path></svg
            ></el-icon>
            GitHub登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p class="login-tip">默认账号：admin / 密码：123456</p>
        <div class="login-links">
          <el-button type="text" @click="navigateToRegister" class="footer-link"
            >注册账号</el-button
          >
          <span class="link-divider">|</span>
          <el-button
            type="text"
            @click="navigateToForgotPassword"
            class="footer-link"
            >找回密码</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { authApi } from "@/api/auth.api";
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref(null);
const isLoading = ref(false);
const errorMessage = ref("");

const loginForm = reactive({
  username: "admin",
  password: "123456",
});

const loginRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "用户名长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      min: 6,
      max: 100,
      message: "密码长度在 6 到 100 个字符",
      trigger: "blur",
    },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  try {
    await loginFormRef.value.validate();
    isLoading.value = true;
    errorMessage.value = "";

    // 使用封装的authApi登录
    const response = await authApi.login({
      username: loginForm.username,
      password: loginForm.password,
    });

    if (response.code === 0 && response.data) {
      // 使用store管理token
      userStore.login(response.data);
      ElMessage.success("登录成功");
      // 跳转到首页
      router.push("/");
    } else {
      throw new Error(response.msg || "登录失败");
    }
  } catch (error) {
    errorMessage.value = error.message || "登录失败，请稍后重试";
    ElMessage.error(errorMessage.value);
  } finally {
    isLoading.value = false;
  }
};

const handleGithubLogin = async () => {
  try {
    const res = await authApi.getGithubAuthorizeUrl();
    if (res && res.code === 0 && res.data) {
      window.location.href = res.data;
    } else if (res && res.data) {
      // some cases backend may return url with code 200
      window.location.href = res.data;
    } else {
      ElMessage.error("无法获取 GitHub 授权地址");
    }
  } catch (err) {
    ElMessage.error("无法获取 GitHub 授权地址，请稍后重试");
  }
};

const navigateToRegister = () => {
  router.push("/register");
};

const navigateToForgotPassword = () => {
  router.push("/forgot-password");
};
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #111113 0%, #1a1b1e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-form-wrapper {
  background: rgba(26, 27, 30, 0.8);
  border-radius: 16px;
  padding: 48px;
  width: 100%;
  max-width: 480px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 215, 0, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.login-form-wrapper:hover {
  box-shadow: 0 8px 40px rgba(255, 215, 0, 0.15);
  border-color: rgba(255, 215, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: bold;
  color: #ffd700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 16px;
  color: #8e8e93;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-input {
  background-color: #2c2d31;
  border-color: #4a4b4f;
  color: #e5e5e5;
  border-radius: 8px;

  &:focus {
    border-color: #ffd700;
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
  }
}

.login-button {
  width: 100%;
  background: linear-gradient(135deg, #ffd700 0%, #b38b2d 100%);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  padding: 14px;
  margin-top: 16px;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
    box-shadow: 0 4px 16px rgba(255, 215, 0, 0.4);
  }

  &:active:not(:disabled) {
    transform: translateY(2px);
  }
}

.error-message {
  color: #ff3b30;
  font-size: 14px;
  margin-top: 16px;
  text-align: center;
  padding: 12px;
  background: rgba(255, 59, 48, 0.1);
  border: 1px solid rgba(255, 59, 48, 0.3);
  border-radius: 6px;
}

.login-footer {
  margin-top: 32px;
  text-align: center;
}

.login-tip {
  font-size: 14px;
  color: #8e8e93;
  margin: 0 0 16px 0;
}

.login-links {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
}

.footer-link {
  color: #ffd700;
  font-size: 14px;
  margin: 0 8px;

  &:hover {
    color: #ffed4e;
  }
}

.link-divider {
  color: #4a4b4f;
  margin: 0 8px;
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;

  &::before,
  &::after {
    content: "";
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, transparent, #4a4b4f, transparent);
  }

  span {
    margin: 0 16px;
    color: #8e8e93;
    font-size: 14px;
  }
}

.github-login-button {
  width: 100%;
  background: #24292e;
  border: 1px solid #4a4b4f;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  padding: 14px;
  transition: all 0.3s ease;
  color: #e5e5e5;

  &:hover {
    background: #2f3640;
    border-color: #ffd700;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
  }

  &:active {
    transform: translateY(2px);
  }
}

.github-icon {
  margin-right: 8px;
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form-wrapper {
    padding: 32px;
    margin: 0 20px;
  }

  .login-title {
    font-size: 24px;
  }

  .login-subtitle {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .login-form-wrapper {
    padding: 24px;
    margin: 0 16px;
  }

  .login-title {
    font-size: 20px;
  }
}
</style>
