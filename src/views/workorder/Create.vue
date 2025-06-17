<template>
  <div class="create-workorder">
    <div class="page-header">
      <h2>创建工单</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="workorder-form">
      <el-form-item label="工单类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio :label="0">需求</el-radio>
          <el-radio :label="1">故障</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入工单标题" />
      </el-form-item>

      <el-form-item label="优先级" prop="priorityLevel">
        <el-radio-group v-model="form.priorityLevel">
          <el-radio :label="0">高</el-radio>
          <el-radio :label="1">中</el-radio>
          <el-radio :label="2">低</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述问题或需求" />
      </el-form-item>

      <el-form-item label="工作流" prop="flowId">
        <el-select v-model="form.flowId" placeholder="请选择工作流" style="width: 100%" filterable>
          <el-option v-for="flow in flowList" :key="flow.flowId" :label="flow.flowName" :value="flow.flowId.toString()">
            <div class="flow-option">
              <span class="flow-id">{{ flow.flowName }}</span>
              <div class="flow-nodes">
                <el-tag v-for="(node, index) in flow.nodes" :key="index"
                  :type="node.nodeType === 2 ? 'warning' : node.nodeType === 3 ? 'primary' : 'success'" size="small"
                  class="flow-node-tag">
                  {{ node.nodeTypeDesc }}: {{ node.handlerName }}
                </el-tag>
              </div>
            </div>
          </el-option>
        </el-select>
        <div class="flow-desc" v-if="form.flowId">
          <span>当前选择的工作流程：</span>
          <div class="flow-nodes">
            {{flowList.find(f => f.flowId.toString() == form.flowId.toString())?.nodes.map(n =>
              `${n.nodeTypeDesc}(${n.handlerName})`).join(' → ')}}
          </div>
        </div>
      </el-form-item>

      <el-form-item label="截止时间" prop="deadlineTime">
        <el-date-picker v-model="form.deadlineTime" type="datetime" placeholder="请选择截止时间" format="YYYY-MM-DD HH:mm:ss"
          value-format="x" :disabled-date="(time) => time.getTime() < Date.now()" style="width: 100%" />
      </el-form-item>

      <el-form-item label="附件">
        <el-upload class="upload-demo" action="#" :auto-upload="false" :on-change="handleFileChange"
          :file-list="fileList">
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持jpg、png、pdf格式文件，单个文件不超过10MB
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="loading">
          提交
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createWorkOrder } from '@/api/workorder'
import { getFlowPage } from '@/api/flow'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])
const flowList = ref([])

// 检查用户是否登录
onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadFlowList()
})

// 获取工作流列表
const loadFlowList = async () => {
  try {
    const res = await getFlowPage({
      pageNum: 1,
      pageSize: 50
    })
    if (res.code === 1) {
      flowList.value = res.data.records.map(flow => ({
        ...flow,
        flowId: String(flow.flowId)
      }))
    }
  } catch (error) {
    console.error('获取工作流列表失败：', error)
    ElMessage.error('获取工作流列表失败')
  }
}

// 格式化工作流节点信息
const formatFlowNodes = (nodes) => {
  if (!nodes || nodes.length === 0) return ''
  return nodes.map(node => `${node.nodeTypeDesc}:${node.handlerName}`).join(' → ')
}

const form = reactive({
  type: 0,
  title: '',
  priorityLevel: 0,
  content: '',
  flowId: '',
  deadlineTime: '',
  accessoryUrl: '',
  accessoryName: ''
})

const rules = {
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  priorityLevel: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  flowId: [{ required: true, message: '请选择工作流', trigger: 'change' }],
  deadlineTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

const handleFileChange = (uploadFile, uploadFiles) => {
  fileList.value = uploadFiles
  if (uploadFile.status === 'ready') {
    form.accessoryName = uploadFile.name
    // TODO: 实际项目中需要调用上传接口获取accessoryUrl
    // form.accessoryUrl = 上传后的文件URL
  }
}

const submitForm = async () => {
  if (!formRef.value) return

  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const submitData = {
          ...form,
          deadlineTime: Number(form.deadlineTime),
          type: Number(form.type),
          priorityLevel: Number(form.priorityLevel)
        }

        const res = await createWorkOrder(submitData)
        if (res.code === 1 && res.data.isSuccess) {
          ElMessage.success('创建工单成功')
          router.push('/workorder/list')
        } else {
          ElMessage.error(res.msg || '创建工单失败')
        }
      } catch (error) {
        console.error('创建工单失败：', error)
        if (error.response?.status === 403) {
          ElMessage.error('没有创建工单的权限')
        } else {
          ElMessage.error('创建工单失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  fileList.value = []
}
</script>

<style scoped>
.create-workorder {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
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

.workorder-form {
  max-width: 800px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.flow-option {
  padding: 8px 0;
}

.flow-id {
  font-weight: bold;
  margin-bottom: 4px;
  display: block;
}

.flow-nodes {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.flow-node-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.flow-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.flow-desc span {
  color: #333;
  font-weight: bold;
}
</style>