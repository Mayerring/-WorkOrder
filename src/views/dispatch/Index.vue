<template>
  <div class="dispatch-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="工单编号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入工单编号" clearable />
          </el-form-item>
          <el-form-item label="工单类型">
            <el-select v-model="searchForm.type" placeholder="请选择工单类型" clearable>
              <el-option label="故障报修" value="fault" />
              <el-option label="需求申请" value="requirement" />
              <el-option label="运维服务" value="operation" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="searchForm.status" placeholder="请选择处理状态" clearable>
              <el-option label="待分派" value="pending" />
              <el-option label="已分派" value="assigned" />
              <el-option label="处理中" value="processing" />
              <el-option label="已完成" value="completed" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="工单类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicant" label="申请人" width="120" />
        <el-table-column prop="department" label="申请部门" width="150" />
        <el-table-column prop="status" label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="处理人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" type="primary" link @click="handleAssign(row)">分派</el-button>
            <el-button v-if="row.status === 'assigned'" type="warning" link @click="handleReassign(row)">转派</el-button>
            <el-button type="info" link @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 分派对话框 -->
    <el-dialog v-model="assignDialogVisible" :title="isReassign ? '工单转派' : '工单分派'" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理单位">
          <el-cascader v-model="assignForm.department" :options="departmentOptions" :props="{ checkStrictly: true }"
            clearable placeholder="请选择处理单位" />
        </el-form-item>
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assignee" placeholder="请选择处理人" clearable filterable>
            <el-option v-for="user in userList" :key="user.id" :label="user.name" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="assignForm.remark" type="textarea" rows="4" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  type: '',
  status: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    orderNo: 'WO202505200001',
    title: '服务器维护申请',
    type: 'operation',
    applicant: '张三',
    department: '技术部',
    status: 'pending',
    assignee: '',
    createTime: '2025-05-20 10:00:00'
  },
  {
    orderNo: 'WO202505200002',
    title: '网络设备升级需求',
    type: 'requirement',
    applicant: '李四',
    department: '运维部',
    status: 'assigned',
    assignee: '王五',
    createTime: '2025-05-20 11:00:00'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 分派对话框
const assignDialogVisible = ref(false)
const isReassign = ref(false)
const assignForm = reactive({
  department: [],
  assignee: '',
  remark: ''
})
const currentRow = ref(null)

// 部门选项
const departmentOptions = [
  {
    value: 'tech',
    label: '技术部',
    children: [
      {
        value: 'dev',
        label: '开发组'
      },
      {
        value: 'ops',
        label: '运维组'
      }
    ]
  },
  {
    value: 'support',
    label: '支持部',
    children: [
      {
        value: 'service',
        label: '客服组'
      }
    ]
  }
]

// 用户列表
const userList = [
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
]

// 工单类型标签
const getTypeTag = (type) => {
  const map = {
    fault: 'danger',
    requirement: 'warning',
    operation: 'success'
  }
  return map[type]
}

// 工单类型名称
const getTypeName = (type) => {
  const map = {
    fault: '故障报修',
    requirement: '需求申请',
    operation: '运维服务'
  }
  return map[type]
}

// 状态标签
const getStatusTag = (status) => {
  const map = {
    pending: 'info',
    assigned: 'warning',
    processing: 'primary',
    completed: 'success'
  }
  return map[status]
}

// 状态名称
const getStatusName = (status) => {
  const map = {
    pending: '待分派',
    assigned: '已分派',
    processing: '处理中',
    completed: '已完成'
  }
  return map[status]
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
}

// 查看详情
const handleView = (row) => {
  router.push(`/workorder/detail/${row.orderNo}`)
}

// 分派操作
const handleAssign = (row) => {
  currentRow.value = row
  isReassign.value = false
  assignDialogVisible.value = true
}

// 转派操作
const handleReassign = (row) => {
  currentRow.value = row
  isReassign.value = true
  assignDialogVisible.value = true
}

// 提交分派
const submitAssign = () => {
  // TODO: 实现分派提交
  console.log('分派信息：', {
    orderNo: currentRow.value?.orderNo,
    isReassign: isReassign.value,
    ...assignForm
  })
  ElMessage.success(isReassign.value ? '转派成功' : '分派成功')
  assignDialogVisible.value = false
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style scoped>
.dispatch-container {
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