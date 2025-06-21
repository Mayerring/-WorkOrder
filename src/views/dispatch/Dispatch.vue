<template>
  <div class="dispatch-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="工单编号">
            <el-input v-model="searchForm.code" placeholder="请输入工单编号" clearable />
          </el-form-item>
          <el-form-item label="工单类型">
            <el-select v-model="searchForm.type" placeholder="请选择工单类型" clearable>
              <el-option label="故障报修" :value="0" />
              <el-option label="需求申请" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="searchForm.status" placeholder="请选择处理状态" clearable>
              <el-option label="待分派" :value="300" />
              <el-option label="已分派" :value="400" />
              <el-option label="处理中" :value="410" />
              <el-option label="已完成" :value="500" />
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="创建时间">
            <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" value-format="YYYY-MM-DD" />
          </el-form-item> -->
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="code" label="工单编号" width="180" />
        <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="工单类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitterInfo.userName" label="申请人" width="120" />
        <el-table-column prop="submitterInfo.departmentName" label="申请部门" width="150" />
        <el-table-column prop="status" label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerInfo" label="处理人" width="120">
          <template #default="{ row }">
            <span v-if="row.handlerInfo && row.handlerInfo.length > 0">
              {{row.handlerInfo.map(handler => handler.userName).join(', ')}}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 300 && isUserDistributer(row)" type="primary" link
              @click="handleAssign(row)">分派</el-button>
            <el-button v-if="row.status === 400 && isUserHandler(row)" type="warning" link
              @click="handleReassign(row)">转派</el-button>
            <el-button type="info" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)"
              :disabled="row.status === 500 || row.status === 600">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 分派对话框 -->
    <el-dialog v-model="assignDialogVisible" :title="isReassign ? '工单转派' : '工单分派'" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assignedUserId" placeholder="请选择处理人" clearable filterable>
            <el-option v-for="user in userList" :key="user.id" :label="user.name" :value="user.id" />
          </el-select>
          <div v-if="userList.length === 0" style="color: #f56c6c; font-size: 12px; margin-top: 5px;">
            暂无可用员工，请检查权限或联系管理员
          </div>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="assignForm.remark" type="textarea" rows="4" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign" :loading="submitLoading"
            :disabled="userList.length === 0">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWorkOrderPage, handleWorkOrder, deleteWorkOrder } from '@/api/workorder'
import { getUserInfo, getAllStaff } from '@/api/user'


const router = useRouter()

// 当前用户ID
const userId = ref(null)

// 搜索表单
const searchForm = reactive({
  code: '',
  type: '',
  status: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 分派对话框
const assignDialogVisible = ref(false)
const submitLoading = ref(false)
const isReassign = ref(false)
const assignForm = reactive({
  id: null,
  code: '',
  assignedUserId: null,
  remark: ''
})
const currentRow = ref(null)

// 员工列表
const userList = ref([])

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

// 获取所有员工
const loadAllStaff = async () => {
  try {
    const res = await getAllStaff()
    if (res.code === 1) {
      userList.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取员工列表失败')
      // 如果获取失败，使用空列表
      userList.value = []
    }
  } catch (error) {
    console.error('获取员工列表失败：', error)
    ElMessage.warning('获取员工列表失败，请检查权限或联系管理员')
    // 如果获取失败，使用空列表
    userList.value = []
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
      return 'info' // 未分派
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

// 判断用户是否为分派者
const isUserDistributer = (row) => {
  return row.distributerInfo?.userId === userId.value
}

// 判断用户是否为处理人
const isUserHandler = (row) => {
  return row.handlerInfo?.some(handler => handler.userId === userId.value)
}

// 加载派单列表
const loadDispatchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      distributerInfo: {
        userId: userId.value,
        finished: false // 未分派：finished为false
      },
      handlerInfo: {
        userId: userId.value,
        finished: false // 未处理：finished为false
      }
    }

    // 只有当搜索条件有值时才添加到参数中
    if (searchForm.code) {
      params.code = searchForm.code
    }
    if (searchForm.type !== '') {
      params.type = searchForm.type
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.dateRange = searchForm.dateRange
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data
      tableData.value = records
      pagination.total = total
      pagination.pageNum = current
      pagination.pageSize = size
    } else {
      ElMessage.error(res.msg || '获取派单列表失败')
    }
  } catch (error) {
    console.error('获取派单列表失败：', error)
    ElMessage.error('获取派单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadDispatchList()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  pagination.pageNum = 1
  loadDispatchList()
}

// 查看详情
const handleView = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

// 分派操作
const handleAssign = (row) => {
  currentRow.value = row
  isReassign.value = false
  assignForm.id = row.id
  assignForm.code = row.code
  assignForm.assignedUserId = null
  assignForm.remark = ''
  assignDialogVisible.value = true
}

// 转派操作
const handleReassign = (row) => {
  currentRow.value = row
  isReassign.value = true
  assignForm.id = row.id
  assignForm.code = row.code
  assignForm.assignedUserId = null
  assignForm.remark = ''
  assignDialogVisible.value = true
}

// 提交分派
const submitAssign = async () => {
  if (!assignForm.assignedUserId) {
    ElMessage.warning('请选择处理人')
    return
  }

  if (!assignForm.remark.trim()) {
    ElMessage.warning('请输入处理说明')
    return
  }

  submitLoading.value = true
  try {
    // 构建请求参数
    const params = {
      id: assignForm.id,
      code: assignForm.code,
      handleType: isReassign.value ? 2 : 1, // 1:分配，2:请求协助(转派)
      assignedUserId: assignForm.assignedUserId, // 新处理人的userId
      remark: assignForm.remark
    }

    const res = await handleWorkOrder(params)
    if (res.code === 1) {
      ElMessage.success(isReassign.value ? '转派成功' : '分派成功')
      assignDialogVisible.value = false
      loadDispatchList() // 重新加载列表
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('操作失败：', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 分页操作
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  loadDispatchList()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadDispatchList()
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该工单吗？', '警告', { type: 'warning' })
    const res = await deleteWorkOrder({ id: row.id })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      loadDispatchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch { }
}

// 初始化
onMounted(async () => {
  await getCurrentUserInfo()
  await loadAllStaff()
  loadDispatchList()
})
</script>

<style scoped>
.dispatch-container {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  height: 100%;
}

/* 为工单类型选择框设置最小宽度 */
.search-form :deep(.el-form-item) {
  margin-right: 20px;
}

.search-form :deep(.el-select) {
  min-width: 150px;
}


.search-area {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media screen and (max-width: 768px) {
  .dispatch-container {
    padding: 10px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>