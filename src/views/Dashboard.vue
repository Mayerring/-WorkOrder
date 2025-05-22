<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="data-overview">
      <el-col :xs="12" :sm="12" :md="6" v-for="(item, index) in overviewData" :key="index">
        <el-card class="overview-card" :body-style="{ padding: '20px' }">
          <div class="overview-content">
            <div class="overview-icon" :style="{ backgroundColor: item.bgColor }">
              <el-icon :size="24">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">{{ item.title }}</div>
              <div class="overview-number">{{ item.number }}</div>
              <div class="overview-desc">
                较上周
                <span :class="item.trend === 'up' ? 'trend-up' : 'trend-down'">
                  {{ item.trend === 'up' ? '↑' : '↓' }} {{ item.rate }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="section-card quick-actions">
      <template #header>
        <div class="card-header">
          <span class="header-title">快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="action in quickActions" :key="action.name">
          <div class="action-item" @click="handleAction(action)">
            <div class="action-icon">
              <el-icon :size="24">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <span class="action-name">{{ action.name }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="chart-section">
      <!-- 待办事项 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="section-card todo-list">
          <template #header>
            <div class="card-header">
              <span class="header-title">待办事项</span>
              <el-button link type="primary" @click="viewMore('todo')">
                查看更多<el-icon>
                  <ArrowRight />
                </el-icon>
              </el-button>
            </div>
          </template>
          <el-scrollbar height="300px">
            <el-table :data="todoList" style="width: 100%" :header-cell-style="{ background: '#f5f7fa' }">
              <el-table-column prop="title" label="标题" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="getTypeTag(row.type)" effect="plain" size="small">
                    {{ row.type }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
            </el-table>
          </el-scrollbar>
        </el-card>
      </el-col>

      <!-- 工单统计 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="section-card statistics">
          <template #header>
            <div class="card-header">
              <span class="header-title">工单统计</span>
              <el-radio-group v-model="chartTimeRange" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="pieChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 处理进度 -->
    <el-card class="section-card progress">
      <template #header>
        <div class="card-header">
          <span class="header-title">处理进度</span>
          <el-select v-model="progressTimeRange" size="small" style="width: 120px">
            <el-option label="最近7天" value="week" />
            <el-option label="最近30天" value="month" />
            <el-option label="最近90天" value="quarter" />
          </el-select>
        </div>
      </template>
      <div ref="lineChart" class="chart"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const pieChart = ref(null)
const lineChart = ref(null)
const chartTimeRange = ref('week')
const progressTimeRange = ref('week')
let pieInstance = null
let lineInstance = null

// 数据概览
const overviewData = [
  {
    title: '待处理工单',
    number: 28,
    icon: 'Document',
    trend: 'up',
    rate: '12%',
    bgColor: '#409EFF22'
  },
  {
    title: '处理中工单',
    number: 15,
    icon: 'Loading',
    trend: 'down',
    rate: '5%',
    bgColor: '#E6A23C22'
  },
  {
    title: '本周完成',
    number: 126,
    icon: 'Check',
    trend: 'up',
    rate: '25%',
    bgColor: '#67C23A22'
  },
  {
    title: '平均处理时长',
    number: '2.5h',
    icon: 'Timer',
    trend: 'down',
    rate: '8%',
    bgColor: '#F56C6C22'
  }
]

// 快捷操作
const quickActions = [
  { name: '发起工单', icon: 'Plus', route: '/workorder/create' },
  { name: '待办事项', icon: 'List', route: '/todo' },
  { name: '我的工单', icon: 'Document', route: '/workorder/list' },
  { name: '待审批', icon: 'Check', route: '/approval/pending' },
  { name: '统计报表', icon: 'TrendCharts', route: '/statistics' }
]

// 待办列表
const todoList = ref([
  {
    title: '服务器维护',
    type: '运维',
    status: '待处理',
    createTime: '2025-05-20 10:00:00'
  },
  {
    title: '网络故障',
    type: '故障',
    status: '处理中',
    createTime: '2025-05-20 09:30:00'
  },
  {
    title: '系统升级',
    type: '运维',
    status: '待处理',
    createTime: '2025-05-20 09:00:00'
  }
])

// 状态样式
const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success',
    '已驳回': 'danger'
  }
  return map[status]
}

// 类型样式
const getTypeTag = (type) => {
  const map = {
    '运维': 'info',
    '故障': 'danger',
    '需求': 'warning'
  }
  return map[type]
}

// 处理快捷操作
const handleAction = (action) => {
  router.push(action.route)
}

// 查看更多
const viewMore = (type) => {
  const routeMap = {
    todo: '/todo',
    statistics: '/statistics'
  }
  router.push(routeMap[type])
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChart.value) return
  pieInstance = echarts.init(pieChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0,
      icon: 'circle'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 20, name: '待处理', itemStyle: { color: '#E6A23C' } },
          { value: 15, name: '处理中', itemStyle: { color: '#409EFF' } },
          { value: 30, name: '已完成', itemStyle: { color: '#67C23A' } },
          { value: 5, name: '已驳回', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  pieInstance.setOption(option)
}

// 初始化折线图
const initLineChart = () => {
  if (!lineChart.value) return
  lineInstance = echarts.init(lineChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['工单数量', '完成数量'],
      icon: 'circle'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#ebeef5'
        }
      }
    },
    series: [
      {
        name: '工单数量',
        type: 'line',
        smooth: true,
        data: [12, 13, 10, 13, 9, 23, 21],
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF22' },
            { offset: 1, color: '#409EFF00' }
          ])
        }
      },
      {
        name: '完成数量',
        type: 'line',
        smooth: true,
        data: [8, 9, 8, 10, 7, 18, 15],
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67C23A22' },
            { offset: 1, color: '#67C23A00' }
          ])
        }
      }
    ]
  }
  lineInstance.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  pieInstance?.resize()
  lineInstance?.resize()
}

onMounted(() => {
  initPieChart()
  initLineChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieInstance?.dispose()
  lineInstance?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-overview {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
  transition: all 0.3s;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.overview-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.overview-info {
  flex: 1;
}

.overview-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.overview-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.overview-desc {
  font-size: 12px;
  color: #909399;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-item {
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  background-color: #f5f7fa;
  margin-bottom: 16px;
}

.action-item:hover {
  background-color: #ecf5ff;
  transform: translateY(-2px);
}

.action-icon {
  margin-bottom: 8px;
  color: #409EFF;
}

.action-name {
  font-size: 14px;
  color: #606266;
}

.chart-section {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-table th) {
  font-weight: 600;
}

:deep(.el-card) {
  border-radius: 8px;
  border: none;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
</style>