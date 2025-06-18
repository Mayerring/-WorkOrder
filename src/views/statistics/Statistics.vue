<template>
  <div class="statistics-container">
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="统计周期">
            <el-select v-model="filterForm.timeType" placeholder="请选择统计周期" @change="handleTimeTypeChange"
              style="width: 100px;">
              <el-option label="按周统计" :value="1" />
              <el-option label="按月统计" :value="2" />
              <el-option label="按年统计" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker v-model="filterForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" :readonly="true" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>工单类型分布</span>
            </div>
          </template>
          <div class="chart" ref="typeChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>工单状态分布</span>
              <el-tag type="info">{{ getTimeTypeText(filterForm.timeType) }}</el-tag>
            </div>
          </template>
          <div class="chart" ref="statusChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本周工单处理数量</span>
            </div>
          </template>
          <div class="chart" ref="trendChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>超时预警工单</span>
          <el-tag type="danger">{{ timeoutPagination.total }}</el-tag>
        </div>
      </template>

      <el-table :data="timeoutWorkorders" style="width: 100%" v-loading="timeoutLoading">
        <el-table-column prop="id" label="工单号" width="120" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'fault' ? 'danger' : 'warning'">
              {{ row.type === 'fault' ? '故障' : '需求' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="danger">已超时</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="timeoutHours" label="超时时长" width="120">
          <template #default="{ row }">
            <span class="timeout">{{ row.timeoutHours || '未知' }}小时</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>


      <!-- 分页 -->
      <div class="pagination-container" v-if="timeoutPagination.total > 0">
        <el-pagination v-model:current-page="timeoutPagination.pageNum" v-model:page-size="timeoutPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="timeoutPagination.total"
          layout="total, sizes, prev, pager, next, jumper" @size-change="handleTimeoutPageChange(1)"
          @current-change="handleTimeoutPageChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getStatusData, getHandleQuantity } from '@/api/dashboard'
import { getWorkOrderPage } from '@/api/workOrder'
import { ElMessage } from 'element-plus'

const router = useRouter()
const typeChartRef = ref(null)
const statusChartRef = ref(null)
const trendChartRef = ref(null)
const loading = ref(false)

const filterForm = reactive({
  dateRange: [],
  timeType: 1 // 默认按周统计
})

// 状态统计数据
const statusData = ref([])

// 超时工单数据
const timeoutWorkorders = ref([])
const timeoutLoading = ref(false)
const timeoutPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// const getStatusDesc = (status) => {
//   const statusMap = {
//     100: '未审核',
//     200: '审核中',
//     270: '审核失败',
//     300: '未派单',
//     400: '处理中',
//     410: '已超时',
//     500: '已处理',
//     600: '已确认',
//     670: '确认失败',
//     700: '已取消'
//   }
//   return statusMap[status] || `状态${status}`
// }

const getTimeTypeText = (timeType) => {
  const map = {
    1: '按周统计',
    2: '按月统计',
    3: '按年统计'
  }
  return map[timeType] || '按周统计'
}

// 获取状态统计数据
const fetchStatusData = async () => {
  try {
    loading.value = true
    const response = await getStatusData({
      timeType: filterForm.timeType
    })

    if (response && response.data) {
      statusData.value = response.data
      updateStatusChart()
    }
  } catch (error) {
    console.error('获取状态统计数据失败：', error)
    ElMessage.error('获取状态统计数据失败')
  } finally {
    loading.value = false
  }
}

// 获取超时工单数据
const fetchTimeoutWorkorders = async () => {
  try {
    timeoutLoading.value = true
    const response = await getWorkOrderPage({
      pageNum: timeoutPagination.pageNum,
      pageSize: timeoutPagination.pageSize,
      status: [410] // 410代表超时工单
    })

    if (response && response.data) {
      timeoutWorkorders.value = response.data.list || []
      timeoutPagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('获取超时工单失败：', error)
    ElMessage.error('获取超时工单失败')
    timeoutWorkorders.value = []
    timeoutPagination.total = 0
  } finally {
    timeoutLoading.value = false
  }
}

// 处理超时工单分页变化
const handleTimeoutPageChange = (page) => {
  timeoutPagination.pageNum = page
  fetchTimeoutWorkorders()
}

// 统计周期变化时自动计算时间范围
const handleTimeTypeChange = () => {
  setDateRangeByTimeType()
  fetchStatusData()
}

function setDateRangeByTimeType() {
  const now = new Date()
  let start, end
  end = new Date(now)
  end.setHours(23, 59, 59, 999)
  if (filterForm.timeType === 1) { // 周
    start = new Date(now)
    start.setDate(now.getDate() - 6)
    start.setHours(0, 0, 0, 0)
  } else if (filterForm.timeType === 2) { // 月
    start = new Date(now)
    start.setDate(now.getDate() - 30)
    start.setHours(0, 0, 0, 0)
  } else if (filterForm.timeType === 3) { // 年
    start = new Date(now)
    start.setDate(now.getDate() - 364)
    start.setHours(0, 0, 0, 0)
  }
  filterForm.dateRange = [start, end]
}

const handleSearch = () => {
  // TODO: 调用查询API
  console.log('查询条件：', filterForm)
  fetchStatusData()
}

const handleReset = () => {
  filterForm.dateRange = []
  filterForm.timeType = 1
  fetchStatusData()
}

const handleView = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

const initTypeChart = () => {
  const chart = echarts.init(typeChartRef.value)
  chart.setOption({
    title: {
      text: '工单类型分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '工单类型',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '故障' },
          { value: 65, name: '需求' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

const updateStatusChart = () => {
  if (!statusChartRef.value) return

  const chart = echarts.getInstanceByDom(statusChartRef.value) || echarts.init(statusChartRef.value)

  // 根据接口返回的数据动态生成图表数据
  const chartData = statusData.value.map(item => ({
    value: item.quantity,
    name: item.statusDesc || `状态${item.status}`
  }))

  chart.setOption({
    title: {
      text: '工单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '工单状态',
        type: 'pie',
        radius: '50%',
        data: chartData.length > 0 ? chartData : [
          { value: 0, name: '暂无数据' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

const initStatusChart = () => {
  const chart = echarts.init(statusChartRef.value)
  chart.setOption({
    title: {
      text: '工单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '工单状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 0, name: '加载中...' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['当日工单总数', '当日完成工单'],
      bottom: 0
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '当日工单总数',
        type: 'line',
        data: [10, 15, 8, 12, 9, 5, 7]
      },
      {
        name: '当日完成工单',
        type: 'line',
        data: [8, 12, 7, 10, 8, 4, 6]
      }
    ]
  })
}

// 监听状态数据变化，自动更新图表
watch(statusData, () => {
  updateStatusChart()
}, { deep: true })

onMounted(() => {
  initTypeChart()
  initStatusChart()
  initTrendChart()

  // 初始化时获取状态统计数据
  fetchStatusData()

  // 初始化时获取超时工单数据
  fetchTimeoutWorkorders()

  // 初始化时设置默认时间范围
  setDateRangeByTimeType()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    const typeChart = echarts.getInstanceByDom(typeChartRef.value)
    const statusChart = echarts.getInstanceByDom(statusChartRef.value)
    const trendChart = echarts.getInstanceByDom(trendChartRef.value)

    typeChart?.resize()
    statusChart?.resize()
    trendChart?.resize()
  })
})
</script>

<style scoped>
.statistics-container {
  padding: 15px;
}

.filter-card {
  height: 80px;
  margin-bottom: 15px;
}

.chart-row {
  margin-bottom: 15px;
}

.chart-card {
  height: 410px;
}

.chart {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeout {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>