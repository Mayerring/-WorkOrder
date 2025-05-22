<template>
  <div class="workorder-detail">
    <div class="page-header">
      <h2>工单详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="workorder-id">工单号：{{ workorder.id }}</span>
          <el-tag :type="getStatusType(workorder.status)">
            {{ getStatusText(workorder.status) }}
          </el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ workorder.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="workorder.type === 'fault' ? 'danger' : 'warning'">
            {{ workorder.type === 'fault' ? '故障' : '需求' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(workorder.priority)">
            {{ getPriorityText(workorder.priority) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ workorder.creator }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ workorder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="当前处理人">{{ workorder.handler || '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          {{ workorder.description }}
        </el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <div v-if="workorder.attachments && workorder.attachments.length">
            <div v-for="file in workorder.attachments" :key="file.name" class="attachment-item">
              <el-link :underline="false" type="primary" @click="downloadFile(file)">
                <el-icon>
                  <Document />
                </el-icon>
                {{ file.name }}
              </el-link>
            </div>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 处理记录 -->
      <div class="section-title">处理记录</div>
      <el-timeline>
        <el-timeline-item v-for="record in workorder.records" :key="record.id" :timestamp="record.time"
          :type="getTimelineItemType(record.action)">
          <div class="timeline-content">
            <div class="action">{{ getActionText(record.action) }}</div>
            <div class="operator">操作人：{{ record.operator }}</div>
            <div class="comment" v-if="record.comment">
              备注：{{ record.comment }}
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
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const handleFormRef = ref(null)

// 模拟数据
const workorder = reactive({
  id: route.params.id,
  title: '服务器CPU使用率异常',
  type: 'fault',
  status: 'pending',
  priority: 'high',
  creator: '张三',
  createTime: '2025-05-20 10:00:00',
  handler: '李四',
  description: '生产环境服务器CPU使用率持续超过90%，需要紧急处理。',
  attachments: [
    { name: '监控截图.png', url: '#' }
  ],
  records: [
    {
      id: 1,
      action: 'create',
      operator: '张三',
      time: '2025-05-20 10:00:00',
      comment: '创建工单'
    },
    {
      id: 2,
      action: 'assign',
      operator: '系统',
      time: '2025-05-20 10:01:00',
      comment: '自动分配给李四'
    }
  ]
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
  return workorder.status === 'pending'
})

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

const getPriorityType = (priority) => {
  const map = {
    low: 'info',
    medium: '',
    high: 'warning',
    urgent: 'danger'
  }
  return map[priority]
}

const getPriorityText = (priority) => {
  const map = {
    low: '低',
    medium: '中',
    high: '高',
    urgent: '紧急'
  }
  return map[priority]
}

const getTimelineItemType = (action) => {
  const map = {
    create: 'primary',
    approve: 'success',
    reject: 'danger',
    transfer: 'warning',
    complete: 'success'
  }
  return map[action]
}

const getActionText = (action) => {
  const map = {
    create: '创建工单',
    approve: '审批通过',
    reject: '驳回',
    transfer: '转派',
    assign: '分配',
    complete: '完成'
  }
  return map[action]
}

const downloadFile = (file) => {
  // TODO: 实现文件下载
  console.log('下载文件：', file)
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