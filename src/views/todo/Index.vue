<template>
  <div class="todo-container">
    <el-tabs v-model="activeTab" class="todo-tabs">
      <el-tab-pane label="待处理" name="pending">
        <el-table :data="pendingList" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="工单号" width="120" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 'fault' ? 'danger' : 'warning'">
                {{ row.type === 'fault' ? '故障' : '需求' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)">
                {{ getPriorityText(row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="timeoutHours" label="剩余时间" width="120">
            <template #default="{ row }">
              <span :class="{ 'timeout-warning': row.timeoutHours <= 24 }">
                {{ row.timeoutHours }}小时
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleView(row)">查看</el-button>
              <el-button link type="success" @click="handleProcess(row)">处理</el-button>
              <el-button link type="warning" @click="handleTransfer(row)">转派</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已处理" name="processed">
        <el-table :data="processedList" v-loading="loading" style="width: 100%">
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
          <el-table-column prop="processTime" label="处理时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleView(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 转派对话框 -->
    <el-dialog v-model="transferDialogVisible" title="转派工单" width="500px">
      <el-form ref="transferFormRef" :model="transferForm" :rules="transferRules" label-width="80px">
        <el-form-item label="处理人" prop="handler">
          <el-select v-model="transferForm.handler" placeholder="请选择处理人">
            <el-option label="张三" value="zhangsan" />
            <el-option label="李四" value="lisi" />
            <el-option label="王五" value="wangwu" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input v-model="transferForm.comment" type="textarea" :rows="3" placeholder="请输入转派原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="transferDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTransfer" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('pending')
const transferDialogVisible = ref(false)
const submitting = ref(false)
const transferFormRef = ref(null)
const currentWorkorder = ref(null)

// 模拟数据
const pendingList = ref([
  {
    id: 'WO2025001',
    title: '服务器CPU使用率异常',
    type: 'fault',
    priority: 'high',
    createTime: '2025-05-20 10:00:00',
    timeoutHours: 12
  },
  {
    id: 'WO2025002',
    title: '新增监控告警功能',
    type: 'requirement',
    priority: 'medium',
    createTime: '2025-05-20 11:00:00',
    timeoutHours: 36
  }
])

const processedList = ref([
  {
    id: 'WO2025003',
    title: '数据库备份失败',
    type: 'fault',
    status: 'completed',
    createTime: '2025-05-19 10:00:00',
    processTime: '2025-05-19 11:00:00'
  }
])

const transferForm = reactive({
  handler: '',
  comment: ''
})

const transferRules = {
  handler: [{ required: true, message: '请选择处理人', trigger: 'change' }],
  comment: [{ required: true, message: '请输入转派原因', trigger: 'blur' }]
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

const handleView = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

const handleProcess = (row) => {
  ElMessageBox.confirm('确认开始处理该工单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用处理API
    ElMessage.success('已开始处理')
  })
}

const handleTransfer = (row) => {
  currentWorkorder.value = row
  transferForm.handler = ''
  transferForm.comment = ''
  transferDialogVisible.value = true
}

const submitTransfer = async () => {
  if (!transferFormRef.value) return

  await transferFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用转派API
        console.log('转派表单：', {
          workorderId: currentWorkorder.value.id,
          ...transferForm
        })
        ElMessage.success('转派成功')
        transferDialogVisible.value = false
      } catch (error) {
        ElMessage.error('转派失败')
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.todo-container {
  padding: 20px;
  background-color: white;
  border-radius: 4px;
}

.todo-tabs {
  margin-bottom: 20px;
}

.timeout-warning {
  color: #f56c6c;
  font-weight: bold;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>