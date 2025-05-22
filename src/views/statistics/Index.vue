<template>
  <div class="statistics-container">
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="时间范围">
            <el-date-picker v-model="filterForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" :shortcuts="dateShortcuts" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
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
              <span>工单趋势</span>
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
          <el-tag type="danger">{{ warningWorkorders.length }}</el-tag>
        </div>
      </template>

      <el-table :data="warningWorkorders" style="width: 100%">
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
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="timeoutHours" label="超时时长" width="120">
          <template #default="{ row }">
            <span class="timeout">{{ row.timeoutHours }}小时</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const typeChartRef = ref(null)
const statusChartRef = ref(null)
const trendChartRef = ref(null)

const filterForm = reactive({
  dateRange: []
})

const dateShortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  }
]

// 模拟数据
const warningWorkorders = ref([
  {
    id: 'WO5001',
    title: '服务器CPU使用率异常',
    type: 'fault',
    status: 'processing',
    createTime: '2025-05-19 10:00:00',
    timeoutHours: 24
  },
  {
    id: 'WO2025002',
    title: '新增监控告警功能',
    type: 'requirement',
    status: 'pending',
    createTime: '2025-05-19 14:00:00',
    timeoutHours: 20
  }
])

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    rejected: 'danger'
  }
  return map[status]
}

const getStatusText = (status) => {
  const map = {
    pending: '待审批',
    processing: '处理中',
    completed: '已完成',
    rejected: '已驳回'
  }
  return map[status]
}

const handleSearch = () => {
  // TODO: 调用查询API
  console.log('查询条件：', filterForm)
}

const handleReset = () => {
  filterForm.dateRange = []
  handleSearch()
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
      trigger: 'item'
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

const initStatusChart = () => {
  const chart = echarts.init(statusChartRef.value)
  chart.setOption({
    title: {
      text: '工单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
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
          { value: 20, name: '待审批' },
          { value: 30, name: '处理中' },
          { value: 40, name: '已完成' },
          { value: 10, name: '已驳回' }
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
    title: {
      text: '工单趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新建工单', '完成工单'],
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
        name: '新建工单',
        type: 'line',
        data: [10, 15, 8, 12, 9, 5, 7]
      },
      {
        name: '完成工单',
        type: 'line',
        data: [8, 12, 7, 10, 8, 4, 6]
      }
    ]
  })
}

onMounted(() => {
  initTypeChart()
  initStatusChart()
  initTrendChart()

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
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart {
  height: 320px;
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
</style>