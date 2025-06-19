<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderPage } from '@/api/workorder'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  status: '',
  title: '', // 工单标题
  type: '',
  priorityLevel: ''
})

// 工单状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '未审核', value: 100 },
  { label: '审核中', value: 200 },
  { label: '审核失败', value: 270 },
  { label: '未派单', value: 300 },
  { label: '处理中', value: 400 },
  { label: '已超时', value: 410 },
  { label: '已处理', value: 500 },
  { label: '已验收', value: 600 },
  { label: '验收失败', value: 670 },
  { label: '已取消', value: 700 }
]

const fetchData = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = { ...queryParams.value }

    // 删除空字符串参数，这样后端就会查询所有数据
    Object.keys(params).forEach(key => {
      if (params[key] === '') {
        delete params[key]
      }
    })

    // 如果状态不为空，转换为数组
    if (params.status !== undefined) {
      params.status = [params.status]
    }

    // 如果输入了关键词，只搜索标题
    if (params.keyword) {
      params.title = params.keyword
      delete params.keyword
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.pageNum = 1
  fetchData()
}

const handleReset = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    status: '',
    title: '',
    type: '',
    priorityLevel: ''
  }
  fetchData()
}

const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  fetchData()
}

const handleDetail = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

// 获取状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    100: 'warning',   // 未审核
    200: 'info',      // 审核中
    270: 'danger',    // 审核失败
    300: 'warning',   // 未派单
    400: 'primary',   // 处理中
    410: 'danger',    // 已超时
    500: 'success',   // 已完成
    600: 'success',   // 已确认完成
    670: 'danger',    // 确认失败
    700: 'info'       // 已取消
  }
  return statusMap[status] || 'info'
}

// 获取优先级标签类型
const getPriorityType = (level) => {
  const levelMap = {
    0: 'danger',    // 高
    1: 'warning',   // 中
    2: 'info'       // 低
  }
  return levelMap[level] || 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="工单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="queryParams.keyword" placeholder="请输入标题" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="router.push('/workorder/create')">创建工单</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column prop="code" label="工单编号" width="180" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="typeDesc" label="类型" width="100" />
      <el-table-column label="优先级" width="100">
        <template #default="{ row }">
          <el-tag :type="getPriorityType(row.priorityLevel)" size="small">
            {{ row.priorityLevelDesc }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">
            {{ row.statusDesc }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交人" width="180">
        <template #default="{ row }">
          <div>{{ row.submitterInfo?.userName || '-' }}</div>
          <div class="text-gray">{{ row.submitterInfo?.departmentName || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="deadlineTime" label="截止时间" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDetail(row)">
            查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<style scoped>
.app-container {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.search-box {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-gray {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

:deep(.el-form--inline .el-form-item) {
  margin-bottom: 0;
}

:deep(.el-input),
:deep(.el-select) {
  width: 200px;
}
</style>