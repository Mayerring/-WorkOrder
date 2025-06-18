<template>
  <div class="workflow-settings">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button v-if="isAdmin" type="primary" @click="handleAdd">新增工作流</el-button>
    </div>

    <!-- 工作流列表 -->
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="flowId" label="流程ID" width="100" />
      <el-table-column prop="flowName" label="工作流名称" width="180" />
      <el-table-column label="节点信息" show-overflow-tooltip>
        <template #default="{ row }">
          <div class="node-info">
            <div class="node-item">
              <span class="node-label">审核节点：</span>
              <el-tag v-for="node in row.nodes?.filter(n => n.nodeType === 2)" :key="node.id" size="small"
                type="primary" class="mx-1">
                {{ node.handlerName }}
              </el-tag>
            </div>
            <div class="node-item">
              <span class="node-label">指派节点：</span>
              <el-tag size="small" type="warning">
                {{row.nodes?.find(n => n.nodeType === 3)?.handlerName || '-'}}
              </el-tag>
            </div>
            <div class="node-item">
              <span class="node-label">验收节点：</span>
              <el-tag size="small" type="success">
                {{row.nodes?.find(n => n.nodeType === 5)?.handlerName || '-'}}
              </el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="isAdmin" type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange" @current-change="handlePageChange" />
    </div>

    <!-- 工作流表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增工作流' : '编辑工作流'" width="500px"
      :close-on-click-modal="false">
      <el-form :model="workflowForm" label-width="120px" :rules="rules" ref="workflowFormRef">
        <el-form-item label="工作流名称" prop="flowName">
          <div class="select-wrapper">
            <el-input v-model="workflowForm.flowName" placeholder="请输入工作流名称" />
          </div>
        </el-form-item>

        <el-form-item label="审核人选择" required>
          <el-form-item prop="auditHandlerIds">
            <div class="select-wrapper">
              <el-select v-model="workflowForm.auditHandlerIds" multiple placeholder="请选择审核处理人"
                @change="handleMultiHandlerChange">
                <el-option v-for="item in handlerOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
          </el-form-item>
        </el-form-item>

        <el-form-item label="指派人选择" required>
          <el-form-item prop="distributeNode.handlerId">
            <div class="select-wrapper">
              <el-select v-model="workflowForm.distributeNode.handlerId"
                @change="(val) => handleSingleHandlerChange(val, 'distributeNode')" placeholder="请选择指派处理人">
                <el-option v-for="item in handlerOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
          </el-form-item>
        </el-form-item>

        <el-form-item label="验收人选择" required>
          <el-form-item prop="checkNode.handlerId">
            <div class="select-wrapper">
              <el-select v-model="workflowForm.checkNode.handlerId"
                @change="(val) => handleSingleHandlerChange(val, 'checkNode')" placeholder="请选择验收处理人">
                <el-option v-for="item in handlerOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </div>
          </el-form-item>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, getAllStaff } from '@/api/user'
// import { getAllStaff } from '@/api/admin'
import { createWorkflow, getFlowPage, deleteFlow, editFlow } from '@/api/flow'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = ref(false)

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const workflowFormRef = ref(null)
const handlerOptions = ref([])

// 根据接口文档修改表单数据结构
const workflowForm = reactive({
  flowId: undefined,
  flowName: '',
  auditHandlerIds: [], // 用于绑定多选框的值
  nodes: [], // 审核节点
  distributeNode: { // 指派节点
    handlerId: undefined,
    handlerName: ''
  },
  checkNode: { // 验收节点
    handlerId: undefined,
    handlerName: ''
  }
})

// 表单验证规则
const rules = {
  flowName: [
    { required: true, message: '请输入工作流名称', trigger: 'blur' }
  ],
  'distributeNode.handlerId': [
    { required: true, message: '请选择指派处理人', trigger: 'change' }
  ],
  'auditHandlerIds': [
    { required: true, message: '请选择审核处理人', trigger: 'change' }
  ],
  'checkNode.handlerId': [
    { required: true, message: '请选择验收节点处理人', trigger: 'change' }
  ]
}

// 获取用户信息并设置权限
const getUserRole = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 1) {
      isAdmin.value = res.data.role === 'admin'
      userStore.setUserInfo(res.data)
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 获取处理人列表
const loadHandlerOptions = async () => {
  try {
    const res = await getAllStaff()
    if (res.code === 1 && Array.isArray(res.data)) {
      // 过滤掉管理员角色，只保留普通用户
      handlerOptions.value = res.data
        .filter(staff => staff.role === 'user')
        .map(staff => ({
          id: staff.id,
          name: staff.name
        }))
      console.log('处理人列表：', handlerOptions.value) // 添加日志
    } else {
      console.error('获取处理人列表失败：', res) // 添加错误日志
      ElMessage.error(res.msg || '获取处理人列表失败')
    }
  } catch (error) {
    console.error('获取处理人列表失败:', error)
    ElMessage.error('获取处理人列表失败')
  }
}

// 加载工作流列表
const loadWorkflowList = async () => {
  loading.value = true
  try {
    const res = await getFlowPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.code === 1) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
      pagination.pageNum = res.data.current || 1
      pagination.pageSize = res.data.size || 10
    } else {
      ElMessage.error(res.msg || '获取工作流列表失败')
    }
  } catch (error) {
    console.error('获取工作流列表失败：', error)
    ElMessage.error('获取工作流列表失败')
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadWorkflowList()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadWorkflowList()
}

// 处理人选择变更
const handleSingleHandlerChange = (value, nodeType) => {
  const handler = handlerOptions.value.find(h => h.id === value)
  if (handler) {
    workflowForm[nodeType] = {
      nodeType: nodeType === 'distributeNode' ? 3 : 5,
      handlerId: handler.id,
      handlerName: handler.name
    }
  }
}

const handleMultiHandlerChange = (values) => {
  // 更新 nodes 数组
  workflowForm.nodes = values.map(value => {
    const handler = handlerOptions.value.find(h => h.id === value)
    return {
      nodeType: 2, // 审核节点类型
      handlerId: handler?.id,
      handlerName: handler?.name || ''
    }
  })
}

// 新增工作流
const handleAdd = async () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以新增工作流')
    return
  }
  dialogType.value = 'add'
  resetWorkflowForm()
  dialogVisible.value = true
}

// 编辑工作流
const handleEdit = async (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以编辑工作流')
    return
  }
  try {
    // 加载处理人列表
    await loadHandlerOptions()
    dialogType.value = 'edit'

    // 根据接口文档重构数据
    const auditNodes = row.nodes.filter(n => n.nodeType === 2)
    const distributeNode = row.nodes.find(n => n.nodeType === 3) || { handlerId: undefined, handlerName: '' }
    const checkNode = row.nodes.find(n => n.nodeType === 5) || { handlerId: undefined, handlerName: '' }

    Object.assign(workflowForm, {
      flowId: row.flowId,
      flowName: row.flowName,
      auditHandlerIds: auditNodes.map(node => node.handlerId), // 设置多选框的值
      nodes: auditNodes.map(node => ({
        handlerId: node.handlerId,
        handlerName: node.handlerName
      })),
      distributeNode: {
        handlerId: distributeNode.handlerId,
        handlerName: distributeNode.handlerName
      },
      checkNode: {
        handlerId: checkNode.handlerId,
        handlerName: checkNode.handlerName
      }
    })
    dialogVisible.value = true
  } catch (error) {
    console.error('编辑工作流失败：', error)
    ElMessage.error('编辑失败，请稍后重试')
  }
}

// 删除工作流
const handleDelete = async (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以删除工作流')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该工作流吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteFlow({ flowId: row.flowId })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      loadWorkflowList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除工作流失败：', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 重置表单
const resetWorkflowForm = () => {
  workflowForm.flowId = undefined
  workflowForm.flowName = ''
  workflowForm.auditHandlerIds = []
  workflowForm.nodes = []
  workflowForm.distributeNode = {
    nodeType: 3,
    handlerId: undefined,
    handlerName: ''
  }
  workflowForm.checkNode = {
    nodeType: 5,
    handlerId: undefined,
    handlerName: ''
  }
}

// 提交表单
const submitForm = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以操作工作流')
    return
  }

  if (!workflowFormRef.value) return
  workflowFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = dialogType.value === 'add' ? createWorkflow : editFlow
        const submitData = {
          flowId: workflowForm.flowId,
          flowName: workflowForm.flowName,
          nodes: workflowForm.nodes.map(node => ({
            nodeType: 2,
            handlerId: node.handlerId,
            handlerName: node.handlerName
          })),
          distributeNode: {
            nodeType: 3,
            handlerId: workflowForm.distributeNode.handlerId,
            handlerName: workflowForm.distributeNode.handlerName
          },
          checkNode: {
            nodeType: 5,
            handlerId: workflowForm.checkNode.handlerId,
            handlerName: workflowForm.checkNode.handlerName
          }
        }
        const res = await api(submitData)
        if (res.code === 1 && res.data.success) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
          dialogVisible.value = false
          loadWorkflowList()
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (error) {
        console.error('提交工作流失败：', error)
        ElMessage.error('提交失败，请稍后重试')
      }
    }
  })
}

// 初始化
onMounted(async () => {
  try {
    await getUserRole()
    await loadHandlerOptions() // 移除 isAdmin 判断，所有用户都需要加载处理人列表
    await loadWorkflowList()
  } catch (error) {
    console.error('初始化失败：', error)
    ElMessage.error('初始化失败，请刷新页面重试')
  }
})
</script>

<style scoped>
.workflow-settings {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  height: 100%;
}

.toolbar {
  margin-bottom: 20px;
}

.node-info {
  display: flex;
  gap: 8px;
}

.node-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-label {
  color: #606266;
  font-size: 14px;
  min-width: 70px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.select-wrapper {
  width: 100%;
}

.select-wrapper :deep(.el-select) {
  width: 100%;
}

.select-wrapper :deep(.el-input) {
  width: 100%;
}

/* 添加处理人选择框的宽度样式 */
:deep(.el-form-item__content) {
  width: 350px;
}

@media screen and (max-width: 768px) {
  .workflow-settings {
    padding: 10px;
  }
}
</style>