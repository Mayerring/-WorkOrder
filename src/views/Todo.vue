<script setup>
import { ref, onMounted } from 'vue'

const todoList = ref([])
const loading = ref(false)

// 模拟待办数据
const mockTodoData = [
  {
    id: 1,
    title: '待处理工单 #1001',
    description: '服务器维护请求',
    priority: 'high',
    deadline: '2025-05-20',
    status: 'pending'
  },
  {
    id: 2,
    title: '待审批工单 #1002',
    description: '数据库备份申请',
    priority: 'medium',
    deadline: '2025-03-21',
    status: 'pending'
  },
  {
    id: 3,
    title: '待确认工单 #1003',
    description: '网络配置变更',
    priority: 'low',
    deadline: '2025-05-22',
    status: 'processing'
  }
]

const fetchTodoList = () => {
  loading.value = true
  // 模拟API请求
  setTimeout(() => {
    todoList.value = mockTodoData
    loading.value = false
  }, 500)
}

const getPriorityType = (priority) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

const handleComplete = (item) => {
  item.status = 'completed'
  // 这里可以调用API更新状态
}

onMounted(() => {
  fetchTodoList()
})
</script>

<template>
  <div class="app-container">
    <el-card class="todo-card">
      <template #header>
        <div class="card-header">
          <span>待办事项</span>
          <el-button type="primary" @click="fetchTodoList">刷新</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="todoList" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="todo-title">
              {{ row.title }}
              <el-tag :type="getPriorityType(row.priority)" size="small" class="priority-tag">
                {{ row.priority }}
              </el-tag>
            </div>
            <div class="todo-description">{{ row.description }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="deadline" label="截止日期" width="120" />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'completed'" link type="primary" @click="handleComplete(row)">
              完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
}

.todo-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-title {
  font-weight: bold;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.priority-tag {
  margin-left: 10px;
}

.todo-description {
  color: #666;
  font-size: 13px;
}
</style>