<template>
  <div class="create-workorder">
    <div class="page-header">
      <h2>创建工单</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="workorder-form">
      <el-form-item label="工单类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio label="fault">故障</el-radio>
          <el-radio label="requirement">需求</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入工单标题" />
      </el-form-item>

      <el-form-item label="优先级" prop="priority">
        <el-select v-model="form.priority" placeholder="请选择优先级">
          <el-option label="低" value="low" />
          <el-option label="中" value="medium" />
          <el-option label="高" value="high" />
          <el-option label="紧急" value="urgent" />
        </el-select>
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述问题或需求" />
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

      <el-form-item label="抄送人">
        <el-select v-model="form.ccList" multiple filterable placeholder="请选择抄送人">
          <el-option label="张三" value="zhangsan" />
          <el-option label="李四" value="lisi" />
          <el-option label="王五" value="wangwu" />
        </el-select>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])

const form = reactive({
  type: 'fault',
  title: '',
  priority: '',
  description: '',
  ccList: []
})

const rules = {
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const handleFileChange = (file, fileList) => {
  // TODO: 处理文件上传
  console.log('文件变更：', file, fileList)
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // TODO: 调用创建工单API
        console.log('表单数据：', form)
        ElMessage.success('创建成功')
        router.push('/workorder')
      } catch (error) {
        ElMessage.error('创建失败')
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
</style>