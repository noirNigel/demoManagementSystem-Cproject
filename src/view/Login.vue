<template>
  <div class="login-container">
    <div class="login-box">
      <h2>后台管理系统</h2>

      <!-- 登录表单 -->
      <el-form
          v-if="!showRegister"
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="login"
          />
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="login"
              style="width: 100%"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
              type="text"
              @click="showRegister = true"
              style="width: 100%; text-align: center;"
          >
            没有账号？立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form
          v-else
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="login-form"
      >
        <el-form-item prop="username">
          <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              size="large"
              prefix-icon="User"
              @blur="checkUsername"
              :suffix-icon="usernameStatus.icon"
              :class="usernameStatus.class"
          >
            <template #suffix>
              <el-icon v-if="usernameStatus.icon === 'Loading'">
                <Loading />
              </el-icon>
              <el-icon v-else-if="usernameStatus.icon">
                <component :is="usernameStatus.icon" />
              </el-icon>
            </template>
          </el-input>
          <div v-if="usernameStatus.message" :class="['username-tip', usernameStatus.class]">
            {{ usernameStatus.message }}
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="register"
          />
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="register"
              style="width: 100%"
              :disabled="usernameStatus.exists"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
              type="text"
              @click="showRegister = false"
              style="width: 100%; text-align: center;"
          >
            已有账号？立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import api from '@/utils/request'

const router = useRouter()

// 登录相关
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册相关
const registerFormRef = ref()
const showRegister = ref(false)
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const usernameStatus = reactive({
  icon: '',
  message: '',
  class: '',
  exists: false,
  checking: false
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
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
  ]
}

// 监听用户名变化，实时检查
let checkTimeout = null
watch(() => registerForm.username, (newVal) => {
  if (newVal && newVal.length >= 3) {
    // 防抖处理，避免频繁请求
    clearTimeout(checkTimeout)
    checkTimeout = setTimeout(() => {
      checkUsername()
    }, 500)
  } else {
    resetUsernameStatus()
  }
})

// 检查用户名是否存在
const checkUsername = async () => {
  if (!registerForm.username || registerForm.username.length < 3) {
    resetUsernameStatus()
    return
  }

  usernameStatus.checking = true
  usernameStatus.icon = 'Loading'
  usernameStatus.message = '检查中...'
  usernameStatus.class = 'checking'

  try {
    const response = await api.get(`/api/auth/check-username?username=${encodeURIComponent(registerForm.username)}`)

    if (response.exists) {
      usernameStatus.icon = 'CircleClose'
      usernameStatus.message = '用户名已存在'
      usernameStatus.class = 'error'
      usernameStatus.exists = true
    } else {
      usernameStatus.icon = 'CircleCheck'
      usernameStatus.message = '用户名可用'
      usernameStatus.class = 'success'
      usernameStatus.exists = false
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
    usernameStatus.icon = 'CircleClose'
    usernameStatus.message = '检查失败，请重试'
    usernameStatus.class = 'error'
    usernameStatus.exists = false
  } finally {
    usernameStatus.checking = false
  }
}

// 重置用户名状态
const resetUsernameStatus = () => {
  usernameStatus.icon = ''
  usernameStatus.message = ''
  usernameStatus.class = ''
  usernameStatus.exists = false
  usernameStatus.checking = false
}

// 公共状态
const loading = ref(false)

// 登录方法
const login = async () => {
  try {
    await loginFormRef.value.validate()

    loading.value = true

    const res = await api.post('/api/auth/login', loginForm)

    if (res.code === 200) {
      localStorage.setItem('token', res.token)
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}

// 注册方法
const register = async () => {
  try {
    await registerFormRef.value.validate()

    // 检查用户名是否已存在
    if (usernameStatus.exists) {
      ElMessage.error('用户名已存在，请修改后重试')
      return
    }

    loading.value = true

    const res = await api.post('/api/auth/register', registerForm)

    if (res.code === 200) {
      localStorage.setItem('token', res.token)
      ElMessage.success('注册成功')
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.errors) {
      // 表单验证错误
      return
    }
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('注册失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-form {
  margin-top: 20px;
}

.username-tip {
  font-size: 12px;
  margin-top: 4px;
  padding: 4px 8px;
  border-radius: 4px;
}

.username-tip.checking {
  color: #909399;
  background-color: #f4f4f5;
}

.username-tip.success {
  color: #67c23a;
  background-color: #f0f9e8;
}

.username-tip.error {
  color: #f56c6c;
  background-color: #fef0f0;
}

:deep(.el-input.success .el-input__wrapper) {
  box-shadow: 0 0 0 1px #67c23a inset !important;
}

:deep(.el-input.error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset !important;
}
</style>