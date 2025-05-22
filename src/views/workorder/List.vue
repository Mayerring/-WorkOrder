<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getWorkOrderList } from '@/api/workorder'
import { ElMessage } from 'element-plus'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  pageSize: 10,
  status: '',
  keyword: ''
})

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已完成', value: 'completed' },
  { label: '已关闭', value: 'closed' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getWorkOrderList(queryParams.value)
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('获取工单列表失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    pageSize: 10,
    status: '',
    keyword: ''
  }
  fetchData()
}

const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  queryParams.value.page = val
  fetchData()
}

const handleCreate = () => {
  try {
    router.push({ name: 'WorkOrderCreate' })
  } catch (error) {
    console.error('导航失败：', error)
    ElMessage.error('页面跳转失败，请重试')
  }
}

const handleDetail = (row) => {
  router.push(`/workorder/detail/${row.id}`)
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
          <el-select v-model="queryParams.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="请输入工单标题/编号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleCreate">创建工单</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="工单编号" width="120" />
      <el-table-column prop="title" label="工单标题" />
      <el-table-column prop="type" label="工单类型" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'completed' ? 'success' : 'info'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDetail(row)">
            查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
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
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>