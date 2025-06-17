<template>
  <div class="approval-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待审批" name="pending">
        <el-card class="box-card">
          <!-- 搜索区域 -->
          <div class="search-area">
            <el-form :inline="true" :model="pendingSearchForm" class="search-form">
              <el-form-item label="工单编号">
                <el-input v-model="pendingSearchForm.code" placeholder="请输入工单编号" clearable />
              </el-form-item>
              <el-form-item label="工单类型">
                <el-select v-model="pendingSearchForm.type" placeholder="请选择工单类型" clearable>
                  <el-option label="故障报修" :value="0" />
                  <el-option label="需求申请" :value="1" />
                </el-select>
              </el-form-item>
              <el-form-item label="提交时间">
                <el-date-picker v-model="pendingSearchForm.dateRange" type="daterange" range-separator="至"
                  start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handlePendingSearch">搜索</el-button>
                <el-button @click="resetPendingSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 表格区域 -->
          <el-table :data="pendingTableData" style="width: 100%" v-loading="pendingLoading">
            <el-table-column prop="code" label="工单编号" width="180" />
            <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="type" label="工单类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submitterInfo.userName" label="申请人" width="120" />
            <el-table-column prop="submitterInfo.departmentName" label="申请部门" width="150" />
            <el-table-column prop="createTime" label="提交时间" width="180" />
            <el-table-column prop="priorityLevel" label="优先级" width="120">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priorityLevel)">{{ row.priorityLevelDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleApprove(row)">审批</el-button>
                <el-button type="info" link @click="handleView(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination v-model:current-page="pendingPagination.pageNum"
              v-model:page-size="pendingPagination.pageSize" :page-sizes="[10, 20, 50, 100]"
              :total="pendingPagination.total" layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePendingSizeChange" @current-change="handlePendingCurrentChange" />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="已审批" name="processed">
        <el-card class="box-card">
          <!-- 搜索区域 -->
          <div class="search-area">
            <el-form :inline="true" :model="processedSearchForm" class="search-form">
              <el-form-item label="工单编号">
                <el-input v-model="processedSearchForm.code" placeholder="请输入工单编号" clearable />
              </el-form-item>
              <el-form-item label="工单类型">
                <el-select v-model="processedSearchForm.type" placeholder="请选择工单类型" clearable>
                  <el-option label="故障报修" :value="0" />
                  <el-option label="需求申请" :value="1" />
                </el-select>
              </el-form-item>
              <el-form-item label="审批结果">
                <el-select v-model="processedSearchForm.approvalResult" placeholder="请选择审批结果" clearable>
                  <el-option label="同意" :value="true" />
                  <el-option label="驳回" :value="false" />
                </el-select>
              </el-form-item>
              <el-form-item label="处理时间">
                <el-date-picker v-model="processedSearchForm.dateRange" type="daterange" range-separator="至"
                  start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleProcessedSearch">搜索</el-button>
                <el-button @click="resetProcessedSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 表格区域 -->
          <el-table :data="processedTableData" style="width: 100%" v-loading="processedLoading">
            <el-table-column prop="code" label="工单编号" width="180" />
            <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="type" label="工单类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submitterInfo.userName" label="申请人" width="120" />
            <el-table-column prop="submitterInfo.departmentName" label="申请部门" width="150" />
            <el-table-column prop="auditorInfo" label="审批结果" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.auditorInfo && row.auditorInfo.length > 0"
                  :type="getApprovalResultType(row.auditorInfo[0].finished)">
                  {{ getApprovalResultText(row.auditorInfo[0].finished) }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="auditorInfo" label="处理时间" width="180">
              <template #default="{ row }">
                <span v-if="row.auditorInfo && row.auditorInfo.length > 0">
                  {{ row.auditorInfo[0].handleTime || '-' }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="info" link @click="handleView(row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination v-model:current-page="processedPagination.pageNum"
              v-model:page-size="processedPagination.pageSize" :page-sizes="[10, 20, 50, 100]"
              :total="processedPagination.total" layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleProcessedSizeChange" @current-change="handleProcessedCurrentChange" />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 审批对话框 -->
    <el-dialog v-model="approvalDialogVisible" title="工单审批" width="500px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approvalForm.isApproved">
            <el-radio :value="true">同意</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approvalForm.remark" type="textarea" rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approvalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderPage, approvalWorkOrder } from '@/api/workorder'
import { getUserInfo } from '@/api/user'

const router = useRouter()

// 当前激活的标签页
const activeTab = ref('pending')

// 当前用户ID
const userId = ref(null)

// 待审批相关数据
const pendingLoading = ref(false)
const pendingTableData = ref([])
const pendingSearchForm = reactive({
  code: '',
  type: '',
  dateRange: []
})
const pendingPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 已处理相关数据
const processedLoading = ref(false)
const processedTableData = ref([])
const processedSearchForm = reactive({
  code: '',
  type: '',
  approvalResult: '',
  dateRange: []
})
const processedPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 审批对话框
const approvalDialogVisible = ref(false)
const submitLoading = ref(false)
const approvalForm = reactive({
  id: null,
  code: '',
  isApproved: true,
  remark: ''
})
const currentRow = ref(null)

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

// 获取审批结果类型
const getApprovalResultType = (finished) => {
  return finished ? 'success' : 'danger'
}

// 获取审批结果文本
const getApprovalResultText = (finished) => {
  return finished ? '同意' : '驳回'
}

// 加载待审批工单列表
const loadPendingList = async () => {
  pendingLoading.value = true
  try {
    const params = {
      pageNum: pendingPagination.pageNum,
      pageSize: pendingPagination.pageSize,
      auditorInfo: {
        userId: userId.value,
        finished: false
      }
    }

    // 只有当搜索条件有值时才添加到参数中
    if (pendingSearchForm.code) {
      params.code = pendingSearchForm.code
    }
    if (pendingSearchForm.type !== '') {
      params.type = pendingSearchForm.type
    }
    if (pendingSearchForm.dateRange && pendingSearchForm.dateRange.length === 2) {
      params.dateRange = pendingSearchForm.dateRange
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data

      // 筛选当前用户需要审批且未完成的工单
      const pendingOrders = records.filter(order =>
        order.auditorInfo?.some(auditor =>
          auditor.userId === userId.value && !auditor.finished
        )
      )

      pendingTableData.value = pendingOrders
      pendingPagination.total = total
      pendingPagination.pageNum = current
      pendingPagination.pageSize = size
    } else {
      ElMessage.error(res.msg || '获取待审批工单列表失败')
    }
  } catch (error) {
    console.error('获取待审批工单列表失败：', error)
    ElMessage.error('获取待审批工单列表失败')
  } finally {
    pendingLoading.value = false
  }
}

// 加载已处理工单列表
const loadProcessedList = async () => {
  processedLoading.value = true
  try {
    const params = {
      pageNum: processedPagination.pageNum,
      pageSize: processedPagination.pageSize,
      auditorInfo: {
        userId: userId.value,
        finished: true
      }
    }

    // 只有当搜索条件有值时才添加到参数中
    if (processedSearchForm.code) {
      params.code = processedSearchForm.code
    }
    if (processedSearchForm.type !== '') {
      params.type = processedSearchForm.type
    }
    if (processedSearchForm.approvalResult !== '') {
      params.approvalResult = processedSearchForm.approvalResult
    }
    if (processedSearchForm.dateRange && processedSearchForm.dateRange.length === 2) {
      params.dateRange = processedSearchForm.dateRange
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data

      // 筛选当前用户已审批完成的工单
      const processedOrders = records.filter(order =>
        order.auditorInfo?.some(auditor =>
          auditor.userId === userId.value && auditor.finished
        )
      )

      processedTableData.value = processedOrders
      processedPagination.total = total
      processedPagination.pageNum = current
      processedPagination.pageSize = size
    } else {
      ElMessage.error(res.msg || '获取已处理工单列表失败')
    }
  } catch (error) {
    console.error('获取已处理工单列表失败：', error)
    ElMessage.error('获取已处理工单列表失败')
  } finally {
    processedLoading.value = false
  }
}

// 待审批搜索
const handlePendingSearch = () => {
  pendingPagination.pageNum = 1
  loadPendingList()
}

// 重置待审批搜索
const resetPendingSearch = () => {
  Object.keys(pendingSearchForm).forEach(key => {
    if (Array.isArray(pendingSearchForm[key])) {
      pendingSearchForm[key] = []
    } else {
      pendingSearchForm[key] = ''
    }
  })
  pendingPagination.pageNum = 1
  loadPendingList()
}

// 已处理搜索
const handleProcessedSearch = () => {
  processedPagination.pageNum = 1
  loadProcessedList()
}

// 重置已处理搜索
const resetProcessedSearch = () => {
  Object.keys(processedSearchForm).forEach(key => {
    if (Array.isArray(processedSearchForm[key])) {
      processedSearchForm[key] = []
    } else {
      processedSearchForm[key] = ''
    }
  })
  processedPagination.pageNum = 1
  loadProcessedList()
}

// 审批操作
const handleApprove = (row) => {
  currentRow.value = row
  approvalForm.id = row.id
  approvalForm.code = row.code
  approvalForm.isApproved = true
  approvalForm.remark = ''
  approvalDialogVisible.value = true
}

// 提交审批
const submitApproval = async () => {
  if (!approvalForm.remark.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }

  submitLoading.value = true
  try {
    const params = {
      id: approvalForm.id,
      code: approvalForm.code,
      isApproved: approvalForm.isApproved,
      remark: approvalForm.remark
    }

    const res = await approvalWorkOrder(params)
    if (res.code === 1) {
      ElMessage.success('审批提交成功')
      approvalDialogVisible.value = false
      // 重新加载当前标签页的数据
      if (activeTab.value === 'pending') {
        loadPendingList()
      } else {
        loadProcessedList()
      }
    } else {
      ElMessage.error(res.msg || '审批提交失败')
    }
  } catch (error) {
    console.error('审批提交失败：', error)
    ElMessage.error('审批提交失败')
  } finally {
    submitLoading.value = false
  }
}

// 查看详情
const handleView = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

// 待审批分页操作
const handlePendingSizeChange = (val) => {
  pendingPagination.pageSize = val
  pendingPagination.pageNum = 1
  loadPendingList()
}

const handlePendingCurrentChange = (val) => {
  pendingPagination.pageNum = val
  loadPendingList()
}

// 已处理分页操作
const handleProcessedSizeChange = (val) => {
  processedPagination.pageSize = val
  processedPagination.pageNum = 1
  loadProcessedList()
}

const handleProcessedCurrentChange = (val) => {
  processedPagination.pageNum = val
  loadProcessedList()
}

// 处理标签页切换
const handleTabClick = (tab) => {
  console.log('切换到标签页：', tab.props.name)
  // 切换标签页时重新加载对应数据
  if (tab.props.name === 'pending') {
    loadPendingList()
  } else {
    loadProcessedList()
  }
}

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'pending') {
    loadPendingList()
  } else {
    loadProcessedList()
  }
})

// 初始化
onMounted(async () => {
  await getCurrentUserInfo()
  loadPendingList()
})
</script>

<style scoped>
.approval-container {
  height: 100%;
  width: 100%;
  padding: 20px;
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

:deep(.el-tabs__content) {
  padding-top: 20px;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-tag) {
  margin-right: 5px;
}

@media screen and (max-width: 768px) {
  .approval-container {
    padding: 10px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>