<template>
  <div class="statistics-container">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <span>工单统计</span>
          <el-radio-group v-model="timeType" @change="handleTimeTypeChange">
            <el-radio-button :label="1">本周</el-radio-button>
            <el-radio-button :label="2">本月</el-radio-button>
            <el-radio-button :label="3">本年</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 数据概览 -->
      <el-row :gutter="20" class="data-overview">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="data-item">
              <div class="data-title">总工单数</div>
              <div class="data-value">{{ statistics.total || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="data-item">
              <div class="data-title">待处理</div>
              <div class="data-value">{{ statistics.pending || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="data-item">
              <div class="data-title">处理中</div>
              <div class="data-value">{{ statistics.processing || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="data-item">
              <div class="data-title">已完成</div>
              <div class="data-value">{{ statistics.completed || 0 }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <div class="charts-container">
        <el-row :gutter="20">
          <el-col :span="12">
            <div ref="trendChart" class="chart"></div>
          </el-col>
          <el-col :span="12">
            <div ref="typeChart" class="chart"></div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getStatusData, getHandleQuantity } from '@/api/dashboard'

const trendChart = ref(null)
const typeChart = ref(null)
const timeType = ref(1) // 1:周，2：月，3：年
const statistics = ref({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0
})

// 获取工单状态统计
const fetchStatusData = async () => {
  try {
    const res = await getStatusData({ timeType: timeType.value })
    if (res.code === 1) {
      const data = res.data
      // 更新统计数据
      statistics.value.total = data.reduce((sum, item) => sum + item.quantity, 0)
      // 更新饼图数据
      if (typeChart.value) {
        const chart = echarts.getInstanceByDom(typeChart.value)
        chart.setOption({
          series: [{
            data: data.map(item => ({
              value: item.quantity,
              name: item.statusDesc
            }))
          }]
        })
      }
    }
  } catch (error) {
    console.error('获取工单状态统计失败:', error)
    ElMessage.error('获取工单状态统计失败')
  }
}

// 获取工单处理数量
const fetchHandleQuantity = async () => {
  try {
    const today = new Date()
    const res = await getHandleQuantity({
      date: today.toISOString().split('T')[0] // 格式化为 yyyy-MM-dd
    })
    if (res.code === 1) {
      const data = res.data
      // 更新趋势图数据
      if (trendChart.value) {
        const chart = echarts.getInstanceByDom(trendChart.value)
        chart.setOption({
          xAxis: {
            data: [data.date]
          },
          series: [
            {
              name: '工单总数',
              data: [data.dailyTotalNum]
            },
            {
              name: '已完成工单',
              data: [data.dailyFinishedNum]
            }
          ]
        })
      }
    }
  } catch (error) {
    console.error('获取工单处理数量失败:', error)
    ElMessage.error('获取工单处理数量失败')
  }
}

// 初始化趋势图
const initTrendChart = () => {
  const chart = echarts.init(trendChart.value)
  const option = {
    title: {
      text: '工单处理趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['工单总数', '已完成工单']
    },
    grid: {
      top: 60,
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '工单总数',
        type: 'line',
        smooth: true,
        data: []
      },
      {
        name: '已完成工单',
        type: 'line',
        smooth: true,
        data: []
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化状态分布图
const initTypeChart = () => {
  const chart = echarts.init(typeChart.value)
  const option = {
    title: {
      text: '工单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      top: 'bottom'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      center: ['50%', '45%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 时间范围变化处理
const handleTimeTypeChange = (type) => {
  timeType.value = type
  fetchStatusData()
  fetchHandleQuantity()
}

onMounted(() => {
  initTrendChart()
  initTypeChart()
  fetchStatusData()
  fetchHandleQuantity()
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', () => {
    if (trendChart.value) {
      echarts.getInstanceByDom(trendChart.value)?.resize()
    }
    if (typeChart.value) {
      echarts.getInstanceByDom(typeChart.value)?.resize()
    }
  })
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
  min-width: 900px;
}

.statistics-card {
  margin-bottom: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.data-overview {
  margin-bottom: 20px;
}

.data-item {
  text-align: center;
  padding: 20px 0;
  height: 100%;
}

.data-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.charts-container {
  margin-top: 20px;
}

.chart {
  height: 400px;
  margin-bottom: 20px;
  width: 100%;
}

@media screen and (max-width: 1400px) {
  .el-col-6 {
    width: 50%;
    margin-bottom: 20px;
  }

  .charts-container .el-col-12 {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }

  .el-col-6 {
    width: 100%;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
  }

  .data-value {
    font-size: 20px;
  }

  .chart {
    height: 300px;
  }
}
</style>