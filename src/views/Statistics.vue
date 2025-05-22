<template>
  <div class="statistics-container">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <span>工单统计</span>
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" @change="handleDateChange" />
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
import { ref, onMounted, reactive, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const dateRange = ref([])
const trendChart = ref(null)
const typeChart = ref(null)
const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0
})

// 初始化趋势图
const initTrendChart = () => {
  const chart = echarts.init(trendChart.value)
  const option = {
    title: {
      text: '工单趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      top: 40,
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [0, 0, 0, 0, 0, 0, 0],
      type: 'line',
      smooth: true
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化类型分布图
const initTypeChart = () => {
  const chart = echarts.init(typeChart.value)
  const option = {
    title: {
      text: '工单类型分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'horizontal',
      top: 'bottom'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      center: ['50%', '45%'],
      data: [
        { value: 0, name: '硬件故障' },
        { value: 0, name: '软件问题' },
        { value: 0, name: '网络故障' },
        { value: 0, name: '其他' }
      ],
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

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/workorder/statistics', {
      params: {
        startDate: dateRange.value[0],
        endDate: dateRange.value[1]
      }
    })
    // 更新统计数据
    Object.assign(statistics, response.data)
    // TODO: 更新图表数据
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 日期变化处理
const handleDateChange = () => {
  fetchStatistics()
}

onMounted(() => {
  initTrendChart()
  initTypeChart()
  fetchStatistics()
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