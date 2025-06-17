<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'">
        <div class="logo">运维工单系统</div>
        <el-menu :default-active="$route.path" class="el-menu-vertical" :collapse="isCollapse"
          background-color="#304156" text-color="#fff" active-text-color="#409EFF" router unique-opened>
          <template v-for="item in menuItems" :key="item.path">
            <el-sub-menu v-if="item.children" :index="item.path">
              <template #title>
                <el-icon>
                  <component :is="item.meta.icon" />
                </el-icon>
                <span>{{ item.meta.title }}</span>
              </template>
              <el-menu-item v-for="child in item.children" :key="child.path"
                :index="'/' + item.path + '/' + child.path">
                <span>{{ child.meta.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="'/' + item.path">
              <el-icon>
                <component :is="item.meta.icon" />
              </el-icon>
              <span>{{ item.meta.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-container class="right-container">
        <el-header>
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleCollapse">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index">
                {{ item.meta?.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-badge :is-dot="hasNotification" class="notification">
              <el-icon @click="showNotification">
                <Bell />
              </el-icon>
            </el-badge>
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo.avatar">
                  {{ userStore.userInfo.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo.username }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
                  <el-dropdown-item @click="handlePassword">修改密码</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main>
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <!-- 个人信息对话框 -->
    <el-dialog v-model="profileDialogVisible" title="个人信息" width="500px">
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="profileForm.managerName" disabled />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="profileForm.staffNumber" disabled />
        </el-form-item>
        <el-form-item label="公司">
          <el-input v-model="profileForm.companyName" disabled />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="profileForm.departmentName" disabled />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="profileForm.position" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { updateUserInfo } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)
const hasNotification = ref(true)

// 个人信息对话框
const profileDialogVisible = ref(false)
const profileForm = ref({
  staffNumber: '',
  companyCode: '',
  companyName: '',
  departmentCode: '',
  departmentName: '',
  position: '',
  managerNumber: '',
  managerName: '',
  role: '',
  createTime: '',
  updateTime: ''
})

// 修改密码对话框
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息
const fetchUserData = async () => {
  const res = await userStore.fetchUserInfo()
  if (res) {
    profileForm.value = { ...res }
  }
}

// 获取菜单项（排除工单详情）
const menuItems = computed(() => {
  const rootRoute = router.options.routes.find(route => route.path === '/')
  if (!rootRoute || !rootRoute.children) return []

  // 递归过滤掉 detail 页
  const filterRoutes = (routes) => {
    return routes
      .filter(r => !(r.name === 'WorkOrderDetail' || (r.path && r.path.includes('detail'))))
      .map(r => ({
        ...r,
        children: r.children ? filterRoutes(r.children) : undefined
      }))
  }

  return filterRoutes(rootRoute.children)
})

// 获取面包屑
const breadcrumbList = computed(() => {
  return route.matched.slice(1)
})

// 切换菜单折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 显示通知
const showNotification = () => {
  // TODO: 实现通知功能
}

// 处理个人信息
const handleProfile = () => {
  profileDialogVisible.value = true
}

// 处理修改密码
const handlePassword = () => {
  passwordDialogVisible.value = true
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 提交修改密码
const submitPassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updateUserInfo({
          id: userStore.userInfo.id,
          password: passwordForm.value.newPassword,
          phone: userStore.userInfo.phone
        })

        if (res.code === 1) {
          passwordDialogVisible.value = false
          ElMessage.success('密码修改成功，请重新登录')
          localStorage.removeItem('token')
          userStore.clearUserInfo()
          router.push('/login')
        } else {
          ElMessage.error(res.msg || '修改失败')
        }
      } catch (error) {
        console.error('修改密码失败：', error)
        ElMessage.error('修改失败，请稍后重试')
      }
    }
  })
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
    '确认退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.clearUserInfo()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => { })
}

onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.el-container {
  height: 100%;
  width: 100%;
}

.right-container {
  flex: 1;
  width: 0;
}

.el-aside {
  background-color: #304156;
  color: white;
  transition: width 0.3s;
  overflow: hidden;
  flex-shrink: 0;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: white;
  background-color: #2b2f3a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  padding: 10px;
}

.breadcrumb {
  margin-left: 10px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification {
  cursor: pointer;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.username {
  font-size: 14px;
  color: #606266;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100vh - 60px);
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  width: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media screen and (max-width: 768px) {
  .el-main {
    padding: 10px;
  }

  .el-header {
    padding: 0 10px;
  }

  .username {
    display: none;
  }

  .breadcrumb {
    display: none;
  }
}
</style>