<template>
  <div class="system-container">
    <el-card class="system-card">
      <el-tabs v-model="activeTab" class="system-tabs" @tab-click="handleTabClick">
        <el-tab-pane label="用户管理" name="users">
          <Users v-if="activeTab === 'users'" />
        </el-tab-pane>
        <el-tab-pane label="角色权限" name="roles">
          <Roles v-if="activeTab === 'roles'" />
        </el-tab-pane>
        <el-tab-pane label="组织架构" name="organization">
          <Organization v-if="activeTab === 'organization'" />
        </el-tab-pane>
        <el-tab-pane label="流程配置" name="workflow">
          <Workflow v-if="activeTab === 'workflow'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Users from './Users.vue'
import Roles from './Roles.vue'
import Organization from './Organization.vue'
import Workflow from './Workflow.vue'

const router = useRouter()
const route = useRoute()

const activeTab = ref('users')

const handleTabClick = (tab) => {
  const { name } = tab.props
  if (name !== activeTab.value) {
    activeTab.value = name
    router.push({ name: name.charAt(0).toUpperCase() + name.slice(1) })
  }
}

// 根据路由初始化激活的标签
const initActiveTab = () => {
  const routeName = route.name
  if (routeName) {
    const tabName = routeName.toLowerCase()
    if (['users', 'roles', 'organization', 'workflow'].includes(tabName)) {
      activeTab.value = tabName
    }
  }
}

initActiveTab()
</script>

<style scoped>
.system-container {
  padding: 10px;
  height: 100%;
}

.system-card {
  height: 100%;
}

.system-tabs {
  height: 100%;
}

:deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow-y: auto;
}

:deep(.el-tab-pane) {
  height: 100%;
}

.developing {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-empty) {
  padding: 40px 0;
}
</style>