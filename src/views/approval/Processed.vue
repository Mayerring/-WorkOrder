<template>
  <div class="processed-container">
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
          <el-form-item label="审批结果">
            <el-select v-model="searchForm.result" placeholder="请选择审批结果" clearable>
              <el-option label="同意" value="approve" />
              <el-option label="驳回" value="reject" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理时间">
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
        <el-table-column prop="approvalResult" label="审批结果" width="120">
          <template #default="{ row }">
            <el-tag :type="row.approvalResult === 'approve' ? 'success' : 'danger'">
              {{ row.approvalResult === 'approve' ? '同意' : '驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalTime" label="处理时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  type: '',
  result: '',
  dateRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    orderNo: 'WO202505190001',
    title: '服务器维护申请',
    type: 'operation',
    applicant: '张三',
    department: '技术部',
    approvalResult: 'approve',
    approvalTime: '2025-05-20 15:30:00'
  },
  {
    orderNo: 'WO202505190002',
    title: '网络设备升级需求',
    type: 'requirement',
    applicant: '李四',
    department: '运维部',
    approvalResult: 'reject',
    approvalTime: '2025-05-19 16:00:00'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

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
.processed-container {
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
  .processed-container {
    padding: 10px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>