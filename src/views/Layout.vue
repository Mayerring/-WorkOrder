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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)
const hasNotification = ref(true)

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
  // TODO: 跳转到个人信息页面
}

// 处理修改密码
const handlePassword = () => {
  // TODO: 显示修改密码对话框
}

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
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