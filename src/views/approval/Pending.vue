<template>
  <div class="pending-container">
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
          <el-form-item label="提交时间">
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
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column prop="urgency" label="紧急程度" width="120">
          <template #default="{ row }">
            <el-tag :type="getUrgencyTag(row.urgency)">{{ row.urgency }}</el-tag>
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
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approvalDialogVisible" title="工单审批" width="500px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="approvalForm.result">
            <el-radio label="approve">同意</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approvalForm.comment" type="textarea" rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approvalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval">确认</el-button>
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
    createTime: '2025-05-20 10:00:00',
    urgency: '高'
  },
  {
    orderNo: 'WO202505200002',
    title: '网络设备升级需求',
    type: 'requirement',
    applicant: '李四',
    department: '运维部',
    createTime: '2025-05-20 11:00:00',
    urgency: '中'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 审批对话框
const approvalDialogVisible = ref(false)
const approvalForm = reactive({
  result: 'approve',
  comment: ''
})
const currentRow = ref(null)

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

// 紧急程度标签
const getUrgencyTag = (urgency) => {
  const map = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  }
  return map[urgency]
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

// 审批操作
const handleApprove = (row) => {
  currentRow.value = row
  approvalDialogVisible.value = true
}

// 提交审批
const submitApproval = () => {
  // TODO: 实现审批提交
  console.log('审批信息：', {
    orderNo: currentRow.value?.orderNo,
    ...approvalForm
  })
  ElMessage.success('审批提交成功')
  approvalDialogVisible.value = false
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
.pending-container {
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
  .pending-container {
    padding: 10px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>