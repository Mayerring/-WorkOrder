<template>
  <div class="workorder-detail">
    <div class="page-header">
      <h2>工单详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="workorder-id">工单号：{{ workorder.code }}</span>
          <el-tag :type="getStatusType(workorder.status)">
            {{ workorder.statusDesc }}
          </el-tag>
          <el-button v-if="workorder.status === 600" @click="handlePrint" link>打印工单</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ workorder.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="workorder.type === 0 ? 'primary' : 'warning'">
            {{ workorder.typeDesc }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(workorder.priorityLevel)">
            {{ workorder.priorityLevelDesc }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ workorder.submitterInfo?.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ workorder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ workorder.deadlineTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前处理人">
          <span v-if="workorder.handlerInfo && workorder.handlerInfo.length > 0">
            {{ workorder.handlerInfo[workorder.handlerInfo.length - 1].userName }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          {{ workorder.content || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <div v-if="workorder.accessoryName">
            <el-link :underline="false" type="primary" @click="downloadFile(workorder)">
              <el-icon>
                <Document />
              </el-icon>
              {{ workorder.accessoryName }}
            </el-link>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 处理记录 -->
      <div class="section-title">处理记录</div>
      <el-timeline>
        <!-- 提交记录 -->
        <el-timeline-item v-if="workorder.submitterInfo" :timestamp="workorder.submitterInfo.handleTime" type="primary">
          <div class="timeline-content">
            <div class="action">{{ workorder.submitterInfo.handleTypeDesc }}</div>
            <div class="operator">操作人：{{ workorder.submitterInfo.userName }} ({{ workorder.submitterInfo.departmentName
              }})
            </div>
            <div class="comment" v-if="workorder.submitterInfo.remark">
              备注：{{ workorder.submitterInfo.remark }}
            </div>
          </div>
        </el-timeline-item>

        <!-- 审批记录 -->
        <el-timeline-item v-for="auditor in workorder.auditorInfo" :key="auditor.id" :timestamp="auditor.handleTime"
          :type="auditor.finished ? 'success' : 'warning'">
          <div class="timeline-content">
            <div class="action">{{ auditor.handleTypeDesc }}</div>
            <div class="operator">操作人：{{ auditor.userName }} ({{ auditor.departmentName }})</div>
            <div class="comment" v-if="auditor.remark">
              备注：{{ auditor.remark }}
            </div>
          </div>
        </el-timeline-item>

        <!-- 派单记录 -->
        <el-timeline-item v-if="workorder.distributerInfo" :timestamp="workorder.distributerInfo.handleTime"
          type="warning">
          <div class="timeline-content">
            <div class="action">{{ workorder.distributerInfo.handleTypeDesc }}</div>
            <div class="operator">操作人：{{ workorder.distributerInfo.userName }} ({{
              workorder.distributerInfo.departmentName
              }})</div>
            <div class="comment" v-if="workorder.distributerInfo.remark">
              备注：{{ workorder.distributerInfo.remark }}
            </div>
          </div>
        </el-timeline-item>

        <!-- 处理记录 -->
        <el-timeline-item v-for="handler in workorder.handlerInfo" :key="handler.id" :timestamp="handler.handleTime"
          :type="handler.finished ? 'success' : 'warning'">
          <div class="timeline-content">
            <div class="action">{{ handler.handleTypeDesc }}</div>
            <div class="operator">操作人：{{ handler.userName }} ({{ handler.departmentName }})</div>
            <div class="comment" v-if="handler.remark">
              备注：{{ handler.remark }}
            </div>
          </div>
        </el-timeline-item>

        <!-- 确认记录 -->
        <el-timeline-item v-if="workorder.checkerInfo" :timestamp="workorder.checkerInfo.handleTime || '待确认'"
          :type="workorder.checkerInfo.finished ? 'success' : 'info'">
          <div class="timeline-content">
            <div class="action">{{ workorder.checkerInfo.handleTypeDesc }}</div>
            <div class="operator">操作人：{{ workorder.checkerInfo.userName }} ({{ workorder.checkerInfo.departmentName }})
            </div>
            <div class="comment" v-if="workorder.checkerInfo.remark">
              备注：{{ workorder.checkerInfo.remark }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>

      <!-- 处理表单 -->
      <div v-if="canHandle" class="handle-form">
        <div class="section-title">处理</div>
        <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules">
          <el-form-item prop="action">
            <el-radio-group v-model="handleForm.action">
              <el-radio label="approve">通过</el-radio>
              <el-radio label="reject">驳回</el-radio>
              <el-radio label="transfer">转派</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="handleForm.action === 'transfer'" prop="handler">
            <el-select v-model="handleForm.handler" placeholder="请选择处理人">
              <el-option label="张三" value="zhangsan" />
              <el-option label="李四" value="lisi" />
              <el-option label="王五" value="wangwu" />
            </el-select>
          </el-form-item>

          <el-form-item prop="comment">
            <el-input v-model="handleForm.comment" type="textarea" :rows="3" placeholder="请输入处理意见" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitHandle" :loading="submitting">
              提交
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderDetail, printWorkOrder } from '@/api/workorder'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const handleFormRef = ref(null)

// 工单详情数据
const workorder = reactive({
  id: null,
  code: '',
  type: 0,
  typeDesc: '',
  title: '',
  submitterInfo: null,
  auditorInfo: [],
  distributerInfo: null,
  handlerInfo: [],
  checkerInfo: null,
  priorityLevel: 0,
  priorityLevelDesc: '',
  status: 0,
  statusDesc: '',
  createTime: '',
  updateTime: '',
  cancelTime: null,
  deleteTime: null,
  deadlineTime: '',
  content: '',
  accessoryUrl: '',
  accessoryName: ''
})

const handleForm = reactive({
  action: 'approve',
  handler: '',
  comment: ''
})

const handleRules = {
  action: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
  handler: [{ required: true, message: '请选择处理人', trigger: 'change' }],
  comment: [{ required: true, message: '请输入处理意见', trigger: 'blur' }]
}

const canHandle = computed(() => {
  // 根据工单状态判断是否可以处理
  return workorder.status === 400 || workorder.status === 410
})

// 获取工单详情
const loadWorkOrderDetail = async () => {
  loading.value = true
  try {
    const params = {
      id: Number(route.params.id),
      code: route.params.code || ''
    }

    const res = await getWorkOrderDetail(params)
    if (res.code === 1) {
      Object.assign(workorder, res.data)
    } else {
      ElMessage.error(res.msg || '获取工单详情失败')
    }
  } catch (error) {
    console.error('获取工单详情失败：', error)
    ElMessage.error('获取工单详情失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 100:
      return 'info' // 未审核
    case 200:
      return 'warning' // 审核中
    case 270:
      return 'danger' // 审核失败
    case 300:
      return 'info' // 未派单
    case 400:
      return 'warning' // 处理中
    case 410:
      return 'danger' // 已超时
    case 500:
      return 'success' // 已完成
    case 600:
      return 'success' // 已确认完成
    case 670:
      return 'danger' // 确认失败
    case 700:
      return 'info' // 已取消
    default:
      return 'info'
  }
}

const getPriorityType = (level) => {
  switch (level) {
    case 0:
      return 'danger'
    case 1:
      return 'warning'
    case 2:
      return 'info'
    default:
      return 'info'
  }
}

const downloadFile = (file) => {
  if (file.accessoryUrl) {
    window.open(file.accessoryUrl, '_blank')
  } else {
    ElMessage.warning('文件链接不可用')
  }
}

const submitHandle = async () => {
  if (!handleFormRef.value) return

  await handleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用处理API
        console.log('处理表单：', handleForm)
        ElMessage.success('处理成功')
        router.back()
      } catch (error) {
        ElMessage.error('处理失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handlePrint = async () => {
  try {
    const res = await printWorkOrder({ id: workorder.id })
    // 适配 axios 封装
    const blob = res instanceof Blob ? res : res.data

    // 获取文件名（支持中文）
    let filename = `工单_${workorder.code || workorder.id}.pdf`
    let disposition = res.headers?.['content-disposition'] || res.headers?.get?.('content-disposition')
    if (disposition) {
      const match = disposition.match(/filename=([^;]+)/)
      if (match && match[1]) {
        filename = decodeURIComponent(match[1].replace(/['"]/g, ''))
      }
    }

    // 直接下载
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error('打印失败')
  }
}


// 初始化
onMounted(() => {
  loadWorkOrderDetail()
})
</script>

<style scoped>
.workorder-detail {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.workorder-id {
  font-weight: bold;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin: 20px 0;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.timeline-content {
  .action {
    font-weight: bold;
    margin-bottom: 5px;
  }

  .operator {
    font-size: 13px;
    color: #606266;
    margin-bottom: 5px;
  }

  .comment {
    font-size: 13px;
    color: #606266;
  }
}

.attachment-item {
  margin-bottom: 5px;

  .el-icon {
    margin-right: 5px;
  }
}

.handle-form {
  max-width: 600px;
}
</style>