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
              <el-radio-group v-model="chartTimeRange" size="small" @change="handleTimeRangeChange">
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

    <!-- 工单处理进度 -->
    <el-card class="section-card progress">
      <template #header>
        <div class="card-header">
          <span class="header-title">工单处理数量趋势</span>
          <el-select v-model="progressTimeRange" size="small" style="width: 120px"
            @change="handleProgressTimeRangeChange">
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
import { getDashboardOverview, getTodoList, getStatusData, getHandleQuantity } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const router = useRouter()
const pieChart = ref(null)
const lineChart = ref(null)
const chartTimeRange = ref('week')
const progressTimeRange = ref('week')
let pieInstance = null
let lineInstance = null

// 数据概览 - 改为响应式数据
const overviewData = ref([
  {
    title: '本月已处理工单数',
    number: 0,
    icon: 'Check',
    trend: 'up',
    rate: '0%',
    bgColor: '#67C23A22'
  },
  {
    title: '待处理工单数',
    number: 0,
    icon: 'Document',
    trend: 'up',
    rate: '0%',
    bgColor: '#409EFF22'
  },
  {
    title: '未审核工单数',
    number: 0,
    icon: 'Loading',
    trend: 'down',
    rate: '0%',
    bgColor: '#E6A23C22'
  },
  {
    title: '超时工单数',
    number: 0,
    icon: 'Timer',
    trend: 'down',
    rate: '0%',
    bgColor: '#F56C6C22'
  }
])

// 获取数据概览
const fetchOverviewData = async () => {
  try {
    const res = await getDashboardOverview()
    if (res.code === 1) {
      const data = res.data
      // 更新数据概览
      overviewData.value[0].number = data.monthFinishedNum || 0
      overviewData.value[1].number = data.unHandledNum || 0
      overviewData.value[2].number = data.unAuditedNum || 0
      overviewData.value[3].number = data.delayNum || 0
    } else {
      ElMessage.error(res.msg || '获取数据概览失败')
    }
  } catch (error) {
    console.error('获取数据概览失败：', error)
    ElMessage.error('获取数据概览失败')
  }
}

// 快捷操作
const quickActions = [
  { name: '工单列表', icon: 'Document', route: '/workorder/list' },
  { name: '发起工单', icon: 'Plus', route: '/workorder/create' },
  { name: '待您审批', icon: 'Check', route: '/approval' },
  { name: '派单管理', icon: 'Share', route: '/dispatch' },
  { name: '待办事项', icon: 'List', route: '/todo' },
  { name: '统计报表', icon: 'TrendCharts', route: '/statistics' }
]

// 待办列表 - 移除静态数据
const todoList = ref([])

// 获取待办事项数据
const fetchTodoData = async () => {
  try {
    const res = await getTodoList()
    if (res.code === 1) {
      // 根据接口返回的数据格式处理数据
      todoList.value = res.data.map(item => ({
        title: item.title || '',
        type: item.typeDesc || '',
        status: item.statusDesc || '',
        createTime: item.createTime || ''
      }))
    } else {
      ElMessage.error(res.msg || '获取待办事项失败')
    }
  } catch (error) {
    console.error('获取待办事项失败：', error)
    ElMessage.error('获取待办事项失败')
  }
}

// 获取状态统计数据
const fetchStatusData = async () => {
  try {
    // 将时间范围映射为数字：week->1, month->2, year->3
    const timeTypeMap = {
      'week': 1,
      'month': 2,
      'year': 3
    }

    const res = await getStatusData({
      timeType: timeTypeMap[chartTimeRange.value]
    })
    if (res.code === 1) {
      // 更新饼图数据
      updatePieChart(res.data)
    } else {
      ElMessage.error(res.msg || '获取状态统计数据失败')
    }
  } catch (error) {
    console.error('获取状态统计数据失败：', error)
    ElMessage.error('获取状态统计数据失败')
  }
}

// 获取工单处理数量数据
const fetchHandleQuantityData = async () => {
  try {
    // 将时间范围映射为数字：week->1, month->2, quarter->3
    const timeTypeMap = {
      'week': 1,
      'month': 2,
      'quarter': 3
    }

    const res = await getHandleQuantity({
      timeType: timeTypeMap[progressTimeRange.value]
    })
    if (res.code === 1) {
      handleQuantityData.value = res.data || []
      updateLineChart()
    } else {
      ElMessage.error(res.msg || '获取工单处理数量数据失败')
    }
  } catch (error) {
    console.error('获取工单处理数量数据失败：', error)
    ElMessage.error('获取工单处理数量数据失败')
  }
}

// 更新饼图数据
const updatePieChart = (data) => {
  if (!pieInstance) return

  // 检查是否有数据
  if (!data || data.length === 0) {
    // 没有数据时显示默认状态
    pieInstance.setOption({
      series: [{
        data: [
          { value: 1, name: '暂无数据', itemStyle: { color: '#F0F0F0' } }
        ]
      }]
    })
    return
  }

  // 根据接口返回的数据格式更新饼图
  const pieData = data.map(item => ({
    value: item.quantity || 0,
    name: item.statusDesc || '未知',
    itemStyle: {
      color: getStatusColor(item.status, item.statusDesc)
    }
  }))

  pieInstance.setOption({
    series: [{
      data: pieData
    }]
  })
}

// 更新折线图数据
const updateLineChart = () => {
  if (!lineInstance) return

  // 根据接口返回的数据动态生成图表数据
  const dates = handleQuantityData.value.map(item => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })

  const totalData = handleQuantityData.value.map(item => item.dailyTotalNum || 0)
  const finishedData = handleQuantityData.value.map(item => item.dailyFinishedNum || 0)

  lineInstance.setOption({
    xAxis: {
      data: dates.length > 0 ? dates : ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    series: [
      {
        name: '工单数量',
        data: totalData.length > 0 ? totalData : [0, 0, 0, 0, 0, 0, 0]
      },
      {
        name: '完成数量',
        data: finishedData.length > 0 ? finishedData : [0, 0, 0, 0, 0, 0, 0]
      }
    ]
  })
}

// 获取状态对应的颜色
const getStatusColor = (status, statusDesc) => {
  // 马卡龙配色方案
  const colorMap = {
    100: '#F5A3A8', // 未审核 - 深粉色
    200: '#F5C4A3', // 审核中 - 深橙色
    270: '#F5A3D1', // 审核失败 - 深紫粉
    300: '#F5D4A3', // 未派单 - 深黄色
    400: '#A3C4F5', // 处理中 - 深蓝色
    410: '#F5A3A3', // 已超时 - 深红色
    500: '#A3F5A3', // 已处理 - 深绿色
    600: '#A3F5C4', // 已验收 - 深青绿
    670: '#F5A3D1', // 确认失败 - 深紫粉
    700: '#D1A3F5'  // 已取消 - 深紫色
  }

  // 优先使用状态码，如果没有则使用描述
  if (colorMap[status]) {
    return colorMap[status]
  }

  // 根据描述匹配颜色
  const descColorMap = {
    '未审核': '#F5A3A8',
    '审核中': '#F5C4A3',
    '审核失败': '#F5A3D1',
    '未派单': '#F5D4A3',
    '处理中': '#A3C4F5',
    '已超时': '#F5A3A3',
    '已处理': '#A3F5A3',
    '已验收': '#A3F5C4',
    '确认失败': '#F5A3D1',
    '已取消': '#D1A3F5'
  }

  return descColorMap[statusDesc] || '#E0E0E0'
}

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

// 初始化饼图 - 移除静态数据
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
      bottom: 1,
      icon: 'circle'
    },
    grid: {
      top: '10%',
      bottom: '15%'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
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
          { value: 0, name: '暂无数据', itemStyle: { color: '#F0F0F0' } }
        ]
      }
    ]
  }
  pieInstance.setOption(option)
}

// 初始化折线图 - 移除静态数据
const initLineChart = () => {
  if (!lineChart.value) return
  lineInstance = echarts.init(lineChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        let result = params[0].axisValue + '<br/>'
        params.forEach(param => {
          result += param.marker + param.seriesName + ': ' + param.value + '<br/>'
        })
        return result
      }
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
        data: [0, 0, 0, 0, 0, 0, 0],
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
        data: [0, 0, 0, 0, 0, 0, 0],
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

onMounted(async () => {
  // 先初始化图表
  initPieChart()
  initLineChart()
  window.addEventListener('resize', handleResize)

  // 然后获取数据
  await fetchOverviewData()
  await fetchTodoData()
  await fetchStatusData()
  await fetchHandleQuantityData()
})

// 监听时间范围变化
const handleTimeRangeChange = () => {
  fetchStatusData()
}

// 监听进度时间范围变化
const handleProgressTimeRangeChange = () => {
  fetchHandleQuantityData()
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieInstance?.dispose()
  lineInstance?.dispose()
})

// 工单处理数量数据
const handleQuantityData = ref([])
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
  margin-bottom: 5px;
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