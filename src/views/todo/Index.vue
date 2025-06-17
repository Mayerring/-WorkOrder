<template>
  <div class="todo-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="未处理工单" name="pending">
        <el-table :data="pendingList" style="width: 100%" v-loading="loading">
          <el-table-column prop="code" label="工单编号" width="120" />
          <el-table-column prop="title" label="工单标题" width="200" show-overflow-tooltip />
          <el-table-column prop="typeDesc" label="工单类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priorityLevelDesc" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priorityLevel)">{{ row.priorityLevelDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="statusDesc" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.statusDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterInfo.userName" label="提交人" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="deadlineTime" label="截止时间" width="180" />
          <el-table-column label="操作" fixed="right" width="150">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleWorkOrder(row)">处理</el-button>
              <el-button type="info" link @click="viewDetail(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已处理工单" name="finished">
        <el-table :data="finishedList" style="width: 100%" v-loading="loading">
          <el-table-column prop="code" label="工单编号" width="120" />
          <el-table-column prop="title" label="工单标题" width="200" show-overflow-tooltip />
          <el-table-column prop="typeDesc" label="工单类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priorityLevelDesc" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priorityLevel)">{{ row.priorityLevelDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="statusDesc" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.statusDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterInfo.userName" label="提交人" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="deadlineTime" label="截止时间" width="180" />
          <el-table-column label="操作" fixed="right" width="100">
            <template #default="{ row }">
              <el-button type="info" link @click="viewDetail(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getWorkOrderPage } from '@/api/workorder'
import { ElMessage } from 'element-plus'

// 标签页激活状态
const activeTab = ref('pending')

// 表格数据
const loading = ref(false)
const pendingList = ref([])
const finishedList = ref([])

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取优先级标签类型
const getPriorityType = (level) => {
  switch (level) {
    case 0:
      return 'danger'
    case 1:
      return 'warning'
    case 2:
      return 'info'
    default:
      return 'info'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 100:
      return 'info' // 未审核
    case 200:
      return 'warning' // 审核中
    case 270:
      return 'danger' // 审核失败
    case 300:
      return 'info' // 未派单
    case 400:
      return 'warning' // 处理中
    case 410:
      return 'danger' // 已超时
    case 500:
      return 'success' // 已完成
    case 600:
      return 'success' // 已确认完成
    case 670:
      return 'danger' // 确认失败
    case 700:
      return 'info' // 已取消
    default:
      return 'info'
  }
}

// 加载工单列表
const loadWorkOrderList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      finished: activeTab.value === 'pending' ? 0 : 1 // 使用finished字段区分待处理和已处理
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data
      if (activeTab.value === 'pending') {
        pendingList.value = records
      } else {
        finishedList.value = records
      }
      pagination.total = total
      pagination.pageNum = current
      pagination.pageSize = size
    } else {
      ElMessage.error(res.msg || '获取工单列表失败')
    }
  } catch (error) {
    console.error('获取工单列表失败：', error)
    ElMessage.error('获取工单列表失败')
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadWorkOrderList()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadWorkOrderList()
}

// 处理工单
const handleWorkOrder = (row) => {
  // TODO: 实现工单处理逻辑
  console.log('处理工单：', row)
}

// 查看工单详情
const viewDetail = (row) => {
  // TODO: 实现查看详情逻辑
  console.log('查看工单详情：', row)
}

// 监听标签页切换
watch(activeTab, () => {
  pagination.pageNum = 1
  loadWorkOrderList()
})

// 初始化
onMounted(() => {
  loadWorkOrderList()
})
</script>

<style scoped>
.todo-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-tag) {
  margin-right: 5px;
}
</style>