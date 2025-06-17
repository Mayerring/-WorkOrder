<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { login } from '@/api/user';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = reactive({
  phone: '',
  password: ''
});

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^[1-9]\d{10}$/, message: '请输入11位数字且以1开头的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 清除旧的token
        localStorage.removeItem('token');
        userStore.clearUserInfo();

        console.log('正在发送登录请求...');
        const res = await login({
          phone: loginForm.phone,
          password: loginForm.password
        });

        console.log('登录响应数据：', res);

        if (res.code === 1) { // 后端返回的成功状态码是1
          console.log('登录成功，存储token...');
          // 存储JWT token
          localStorage.setItem('token', res.data);
          userStore.setToken(res.data);

          ElMessage.success('登录成功');
          console.log('准备跳转到仪表盘...');

          // 直接执行路由跳转
          await router.push('/dashboard');
          console.log('路由跳转已执行');
        } else {
          ElMessage.error(res.msg || '登录失败');
        }
      } catch (error) {
        console.error('登录错误详情：', error);
        ElMessage.error('登录失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<template>
  <div class="login-page">
    <header class="header">
      <h1>运维工单管理系统</h1>
    </header>

    <div class="login-container">
      <div class="login-left">
        <img src="/Login_image.png" alt="登录图片" class="login-image" />
      </div>
      <div class="login-right">
        <div class="login-form">
          <h2 class="login-title">用户登录</h2>
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
            <el-form-item prop="phone">
              <el-input v-model="loginForm.phone" placeholder="请输入手机号" prefix-icon="Iphone" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
                show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleLogin" class="login-button">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <footer class="footer">
      <p>帮助中心 | 服务支持 | 广告服务 | 服务网点 | 线下门店 | 申请商家 | 加入我们 | 关于我们 | 友情链接</p>
      <p>Copyright © 商城名称 2020–2050</p>
    </footer>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  padding: 1rem 2rem;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: left;
}

.header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.login-container {
  display: flex;
  flex: 1;
  padding: 2rem 5rem;
  gap: 2rem;
  max-width: 100vw;
  width: 100%;
  box-sizing: border-box;
  justify-content: space-between;
}

.login-left {
  flex: 0 0 auto;
  display: flex;
  max-width: 70%;
  align-items: center;
  justify-content: center;
}

.login-image {
  max-width: 90%;
  max-height: 600px;
  object-fit: contain;
}

.login-right {
  flex: 0 0 auto;
  display: flex;
  max-width: 30%;
  align-items: center;
  justify-content: center;
}

.login-form {
  max-width: 800px;
  background: #fff;
  padding: 2.5rem 3rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.login-title {
  margin-bottom: 1.5rem;
  text-align: center;
}

.form-item {
  margin-bottom: 2.5rem;
}

input {
  width: 100%;
  padding: 20px;
  border: 1.5px solid #6b6a6a;
  border-radius: 6px;
  font-size: 16px;
}

.login-button {
  width: 50%;
  padding: 12px;
  background-color: #3083ea;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  justify-content: center;
  margin: 0 auto;
}

.login-button:hover {
  background-color: rgb(135, 199, 227);
}

.footer {
  text-align: center;
  padding: 0.5rem;
  background-color: #333;
  color: #fff;
  font-size: 14px;
}

.footer p {
  margin: 0.3rem 0;
}

.footer a {
  color: #ffffff;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}
</style>
