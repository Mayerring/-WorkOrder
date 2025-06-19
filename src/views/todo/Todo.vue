<template>
  <div class="todo-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="未处理" name="pending">
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
          <el-table-column label="操作" fixed="right" width="180">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleWorkOrderAction(row)">处理</el-button>
              <el-button type="info" link @click="getWorkOrderDetailAction(row)">查看</el-button>
              <el-button type="danger" link @click="handleDelete(row)"
                :disabled="row.status === 500 || row.status === 600">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已处理" name="finished">
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
              <el-button type="info" link @click="getWorkOrderDetailAction(row)">查看</el-button>
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
import { useRouter } from 'vue-router'
import { getWorkOrderPage, handleWorkOrder, deleteWorkOrder } from '@/api/workorder'
import { getUserInfo } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 标签页激活状态
const activeTab = ref('pending')

// 表格数据
const loading = ref(false)
const pendingList = ref([])
const finishedList = ref([])
const userId = ref(null)

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取当前用户信息
const getCurrentUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 1) {
      userId.value = Number(res.data.userId)
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
    ElMessage.error('获取用户信息失败')
  }
}

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
      return 'success' // 已处理
    case 600:
      return 'success' // 已验收
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
      handlerInfo: {
        userId: userId.value,
        finished: activeTab.value === 'pending' ? false : true // 未处理：false，已处理：true
      }
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data
      // 筛选当前用户需要处理的工单
      const userWorkOrders = records.filter(order =>
        order.handlerInfo?.some(handler =>
          Number(handler.userId) === userId.value
        )
      )
      // 根据 finished 状态分类
      if (activeTab.value === 'pending') {
        pendingList.value = userWorkOrders.filter(order =>
          order.handlerInfo.some(handler =>
            Number(handler.userId) === userId.value && !handler.finished
          )
        )
      } else {
        finishedList.value = userWorkOrders.filter(order =>
          order.handlerInfo.some(handler =>
            Number(handler.userId) === userId.value && handler.finished
          )
        )
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
const handleWorkOrderAction = async (row) => {
  try {
    // 这里可以根据工单状态决定处理类型
    // 例如：如果工单是处理中状态，可以选择完成操作
    const handleType = 4 // 4: 完成
    const params = {
      id: row.id,
      code: row.code,
      handleType: handleType,
      remark: '工单处理完成'
    }

    const res = await handleWorkOrder(params)
    if (res.code === 1) {
      ElMessage.success('工单处理成功')
      loadWorkOrderList() // 重新加载列表
    } else {
      ElMessage.error(res.msg || '工单处理失败')
    }
  } catch (error) {
    console.error('工单处理失败：', error)
    ElMessage.error('工单处理失败')
  }
}

// 查看工单详情
const getWorkOrderDetailAction = async (row) => {
  // 直接跳转到详情页面
  router.push(`/workorder/detail/${row.id}`)
}

// 删除工单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该工单吗？', '警告', { type: 'warning' })
    const res = await deleteWorkOrder({ id: row.id })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      loadWorkOrderList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch { }
}

// 监听标签页切换
watch(activeTab, () => {
  pagination.pageNum = 1
  loadWorkOrderList()
})

// 初始化
onMounted(async () => {
  await getCurrentUserInfo()
  loadWorkOrderList()
})
</script>

<style scoped>
.todo-container {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  height: 100%;
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